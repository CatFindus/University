package com.example.model.service;

import com.example.exeptions.IncorrectRequestException;
import com.example.exeptions.NoDataException;
import com.example.mapper.EntityMapper;
import com.example.model.dto.Request.DtoRequest;
import com.example.model.dto.Request.SubjectRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.dto.Response.NoDataResponse;
import com.example.model.entities.ModelUnit;
import com.example.model.entities.Subject;
import com.example.repository.RepositoryFacade;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.example.consts.LoggerConstants.*;
import static com.example.consts.ModelConstants.*;

@AllArgsConstructor
public class SubjectService implements Service{

    private final static Logger logger = LoggerFactory.getLogger(StudentService.class);
    private final RepositoryFacade repo;
    private final Session session;
    private final EntityMapper mapper;

    @Override
    public DtoResponse getDataById(String idString) throws IncorrectRequestException, NoDataException {
        logger.trace(SERVICE_GETDATABYID_BEGIN, idString);
        Transaction transaction = session.beginTransaction();
        try {
            Integer id = Integer.parseInt(idString);
            Optional<Subject> mayBeSubject = repo.getSubjectById(id);
            if(mayBeSubject.isEmpty()) throw new NoDataException(DATA_NOT_FOUND);
            DtoResponse response = mappingVoToDto(mayBeSubject.get());
            transaction.commit();
            logger.trace(SERVICE_GETDATABYID_END, response);
            return response;

        } catch (NumberFormatException e) {
            transaction.rollback();
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }
    }

    @Override
    public List<DtoResponse> getDataByParameters(Map<String, String[]> parameterMap) throws IncorrectRequestException {
        logger.trace(SERVICE_GETDATABYPARAMS_BEGIN, parameterMap.keySet());
        Map<String, Object> requestMap = new HashMap<>();
        int limit = 100;
        int offset = 0;
        List<Subject> subjects;
        try {
            for (String key : parameterMap.keySet()) {
                String value = parameterMap.get(key)[0];
                switch (key) {
                    case RQ_NAME -> requestMap.put(RQ_NAME, value);
                    case RQ_REQUEST_NAME -> requestMap.put(RQ_REQUEST_NAME, value);
                    case RQ_ID -> requestMap.put(RQ_ID, Integer.parseInt(value));
                    case RQ_LIMIT -> limit = Integer.parseInt(value);
                    case RQ_OFFSET -> offset = Integer.parseInt(value);
                    default -> throw new IncorrectRequestException(String.format(PARAM_NOT_RECOGNISED, key));
                }
            }
        } catch (NumberFormatException e) {
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }
        Transaction transaction = session.beginTransaction();
        subjects = repo.getSubjects(requestMap, limit, offset);
        List<DtoResponse> responses = mappingVoToDto(new ArrayList<>(subjects));
        transaction.commit();
        logger.trace(SERVICE_GETDATABYPARAMS_END, subjects.stream().map(Subject::getId).toList());
        return responses;
    }

    @Override
    public List<DtoResponse> mappingVoToDto(List<ModelUnit> modelUnitList) {
        List<DtoResponse> responses = new ArrayList<>();
        for (ModelUnit modelUnit : modelUnitList) {
            Subject subject = (Subject) modelUnit;
            DtoResponse response = mappingVoToDto(subject);
            responses.add(response);
        }
        return responses;
    }

    @Override
    public DtoResponse mappingVoToDto(ModelUnit modelUnit) {
        logger.trace(SERVICE_MAP_DTO_BEGIN, modelUnit);
        DtoResponse response = mapper.mapSubjectToResponse((Subject) modelUnit);
        logger.trace(SERVICE_MAP_DTO_END, response);
        return response;
    }

    @Override
    public DtoResponse create(String path, DtoRequest request) throws NoDataException, IncorrectRequestException {
        logger.trace(SERVICE_CREATE, request);
        Transaction transaction = session.beginTransaction();
        if (request==null) throw new IncorrectRequestException(INCORRECT_BODY_OF_REQUEST);
        Subject subject = mapper.mapSubjectFromRequest((SubjectRequest) request);
        repo.addSubject(subject);
        transaction.commit();
        return mappingVoToDto(subject);
    }

    @Override
    public DtoResponse update(String path, DtoRequest request) throws IncorrectRequestException, NoDataException {
        Transaction transaction = session.beginTransaction();
        try {
            Integer id = Integer.parseInt(path);
            Optional<Subject> mayBeSubject = repo.getSubjectById(id);
            if(mayBeSubject.isEmpty()) throw new NoDataException(DATA_NOT_FOUND);
            Subject subject = mayBeSubject.get();
            if (request==null) throw new IncorrectRequestException(INCORRECT_BODY_OF_REQUEST);
            SubjectRequest requestToUpdate = (SubjectRequest) request;
            if (requestToUpdate.getRequestName()!=null) subject.setRequestName(requestToUpdate.getRequestName());
            if (requestToUpdate.getName()!=null) subject.setName(requestToUpdate.getName());
            subject = repo.update(subject);
            transaction.commit();
            return mapper.mapSubjectToResponse(subject);
        } catch (NumberFormatException e) {
            transaction.rollback();
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }
    }

    @Override
    public DtoResponse delete(String path, DtoRequest request) throws IncorrectRequestException, NoDataException {
        Transaction transaction = session.beginTransaction();
        try {
            Integer id = Integer.parseInt(path);
            boolean deleted = repo.removeSubject(id);
            if (deleted) {
                transaction.commit();
                return new NoDataResponse();
            } else {
                transaction.rollback();
                throw new NoDataException(DATA_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            transaction.rollback();
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }
    }
}
