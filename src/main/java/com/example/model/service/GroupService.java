package com.example.model.service;

import com.example.consts.ControlerConstants;
import com.example.exeptions.IncorrectRequestException;
import com.example.exeptions.NoDataException;
import com.example.mapper.EntityMapper;
import com.example.model.dto.Request.DtoRequest;
import com.example.model.dto.Request.GroupRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.dto.Response.NoDataResponse;
import com.example.model.entities.Group;
import com.example.model.entities.ModelUnit;
import com.example.model.entities.Student;
import com.example.repository.RepositoryFacade;
import com.example.validators.quantity.GroupQuantityValidator;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.example.consts.LoggerConstants.*;
import static com.example.consts.ModelConstants.*;

@AllArgsConstructor
public class GroupService implements Service {
    private final static Logger logger = LoggerFactory.getLogger(GroupService.class);
    private final RepositoryFacade repo;
    private final EntityMapper mapper;
    private final Session session;
    private final Service studentService;

    @Override
    public DtoResponse getDataById(String idString) throws IncorrectRequestException, NoDataException {
        logger.trace(SERVICE_GETDATABYID_BEGIN, idString);
        int id;
        try {
            id = Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }
        Optional<Group> mayBeGroup = repo.getGroup(id);
        logger.trace(SERVICE_GETDATABYID_END, mayBeGroup.map(Group::getId).orElse(null));
        if (mayBeGroup.isEmpty()) throw new NoDataException(DATA_NOT_FOUND);
        DtoResponse response = mappingVoToDto(mayBeGroup.get());
        return response;
    }

    @Override
    public List<DtoResponse> getDataByParameters(Map<String, String[]> parameterMap) throws IncorrectRequestException {
        logger.trace(SERVICE_GETDATABYPARAMS_BEGIN, parameterMap.keySet());
        int limit = 100;
        int offset = 0;
        Map<String, Object> requestMap = new HashMap<>();
        List<Group> groups;
        Transaction transaction = session.beginTransaction();
        try {
            for (String key : parameterMap.keySet()) {
                String value = parameterMap.get(key)[0];
                switch (key) {
                    case RQ_NUMBER -> requestMap.put(RQ_NUMBER, value);
                    case RQ_ID -> requestMap.put(RQ_ID, Integer.parseInt(value));
                    case RQ_LIMIT -> limit = Integer.parseInt(value);
                    case RQ_OFFSET -> offset = Integer.parseInt(value);
                    default -> throw new IncorrectRequestException(String.format(PARAM_NOT_RECOGNISED, key));
                }
            }
        } catch (NumberFormatException e) {
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }
        groups = repo.getGroups(requestMap, limit, offset);
        List<DtoResponse> responses = mappingVoToDto(new ArrayList<>(groups));
        transaction.commit();
        logger.trace(SERVICE_GETDATABYPARAMS_END, groups.stream().map(Group::getId).toList());
        return responses;
    }

    @Override
    public List<DtoResponse> mappingVoToDto(List<ModelUnit> modelUnitList) {
        logger.trace(SERVICE_MAP_DTO_BEGIN, modelUnitList.size());
        List<DtoResponse> list = new ArrayList<>();
        for (ModelUnit unit : modelUnitList) {
            Group group = (Group) unit;
            list.add(mapper.mapGroupToResponse(group));
        }
        logger.trace(SERVICE_MAP_DTO_END, list.size());
        return list;
    }

    @Override
    public DtoResponse mappingVoToDto(ModelUnit modelUnit) {
        Group group = (Group) modelUnit;
        return mapper.mapGroupToResponse(group);
    }

    @Override
    public DtoResponse create(String path, DtoRequest dtoRequest) throws IncorrectRequestException {
        logger.trace(SERVICE_CREATE, dtoRequest);
        Transaction transaction = session.beginTransaction();
        if (path==null) {
            Group group = mapper.mapGroupFromRequest((GroupRequest) dtoRequest);
            repo.addGroup(group);
            DtoResponse response = mappingVoToDto(group);
            transaction.commit();
            return response;
        } else {
            try {
                return addStudentToGroup(path, (GroupRequest) dtoRequest, transaction);
            } catch (NumberFormatException e) { throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT); } catch (
                    NoDataException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private DtoResponse addStudentToGroup(String path, GroupRequest dtoRequest, Transaction transaction) throws IncorrectRequestException, NoDataException {
        int id = Integer.parseInt(path);
        new GroupQuantityValidator(ControlerConstants.POST, id).validate();
        if (dtoRequest.getStudentId()==null) throw new IncorrectRequestException(INCORRECT_BODY_OF_REQUEST);
        Optional<Student> mayBeStudent = repo.getStudent(Integer.parseInt(dtoRequest.getStudentId()));
        if (mayBeStudent.isEmpty()) throw new NoDataException(DATA_NOT_FOUND);
        Student student = mayBeStudent.get();
        boolean studentAdded = repo.addStudentToGroup(student,id);
        if(!studentAdded) {
            transaction.rollback();
            throw new IncorrectRequestException(DATA_NOT_UPDATED);
        } else {
            DtoResponse response = mappingVoToDto(repo.getGroup(id).get());
            transaction.commit();
            return response;
        }
    }

    @Override
    public DtoResponse update(String path, DtoRequest dtoRequest) throws IncorrectRequestException, NoDataException {
        logger.trace(SERVICE_UPDATE_BEGIN, path, dtoRequest);
        Transaction transaction = session.beginTransaction();
        if (path.isEmpty()) throw new IncorrectRequestException();
        try {
            int id = Integer.parseInt(path);
            GroupRequest request = (GroupRequest) dtoRequest;
            Optional<Group> mayBeGroup = repo.getGroup(id);
            if (mayBeGroup.isEmpty()) throw  new NoDataException(DATA_NOT_FOUND);
            Group group = mayBeGroup.get();
            if (request.getNumber()!=null) group.setNumber(request.getNumber());
            group = repo.update(group);
            DtoResponse response = mappingVoToDto(group);
            transaction.commit();
            return response;
        } catch (NumberFormatException e) {
            transaction.rollback();
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT); }
    }

    @Override
    public DtoResponse delete(String path, DtoRequest dtoRequest) throws IncorrectRequestException, NoDataException {
        logger.trace(SERVICE_DELETE_BEGIN, path);
        if (path.isEmpty()) throw new IncorrectRequestException(INCORRECT_PATH_FORMAT);
        int id;
        try {
            id = Integer.parseInt(path);
        } catch (NumberFormatException e) { throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT); }
        Transaction transaction = session.beginTransaction();
        if(dtoRequest==null) {
            boolean deleted = repo.removeGroup(id);
            if(!deleted) {
                transaction.rollback();
                throw new NoDataException(DATA_NOT_UPDATED);
            }
            return new NoDataResponse();
        } else {
            GroupRequest request = (GroupRequest) dtoRequest;
            if (request.getStudentId()==null) {
                transaction.rollback();
                throw new IncorrectRequestException(INCORRECT_BODY_OF_REQUEST);
            }
            new GroupQuantityValidator(ControlerConstants.DELETE, repo.getStudentsCountByGroup(id)).validate();
            try {
                Optional<Student> mayBeStudent = repo.getStudent(Integer.parseInt(request.getStudentId()));
                if (mayBeStudent.isEmpty()) {
                    transaction.rollback();
                    throw new NoDataException(DATA_NOT_FOUND);
                }
                Student student = mayBeStudent.get();
                boolean replaced = repo.removeStudentFromGroup(student, id);
                if(replaced) {
                    DtoResponse response = mappingVoToDto(repo.getGroup(id).get());
                    transaction.commit();
                    return response;
                } else {
                    throw new IncorrectRequestException(DATA_NOT_UPDATED);
                }
            } catch (NumberFormatException e) {
                transaction.rollback();
                throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
            }
        }
    }
}
