package com.example.model.service;

import com.example.exeptions.IncorrectRequestException;
import com.example.exeptions.NoDataException;
import com.example.mapper.EntityMapper;
import com.example.model.dto.Request.DtoRequest;
import com.example.model.dto.Request.TeacherRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.dto.Response.NoDataResponse;
import com.example.model.entities.ModelUnit;
import com.example.model.entities.Subject;
import com.example.model.entities.Teacher;
import com.example.repository.RepositoryFacade;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

import static com.example.consts.LoggerConstants.*;
import static com.example.consts.ModelConstants.*;
@AllArgsConstructor
public class TeacherService implements Service {
    private final static Logger logger = LoggerFactory.getLogger(StudentService.class);
    private final RepositoryFacade repo;
    private final Session session;
    private final EntityMapper mapper;
    @Override
    public DtoResponse getDataById(String idString) throws IncorrectRequestException, NoDataException {
        logger.trace(SERVICE_GETDATABYID_BEGIN, idString);
        int id;
        try {
            id = Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }
        Transaction transaction = session.beginTransaction();
        Optional<Teacher> mayBeTeacher = repo.getTeacher(id);
        logger.trace(SERVICE_GETDATABYID_END, mayBeTeacher.map(Teacher::getId).orElse(null));
        if (mayBeTeacher.isPresent()) {
            DtoResponse response = mappingVoToDto(mayBeTeacher.get());
            transaction.commit();
            return response;
        }
        else {
            transaction.commit();
            throw new NoDataException(DATA_NOT_FOUND);
        }
    }

    @Override
    public List<DtoResponse> getDataByParameters(Map<String, String[]> parameterMap) throws IncorrectRequestException {
        logger.trace(SERVICE_GETDATABYPARAMS_BEGIN, parameterMap.keySet());
        Map<String, Object> requestMap = new HashMap<>();
        int limit = 100;
        int offset = 0;
        List<Teacher> teachers;
        for (String key : parameterMap.keySet()) {
            String value = parameterMap.get(key)[0];
            switch (key) {
                case RQ_FIRST_NAME -> requestMap.put(RQ_FIRST_NAME, value);
                case RQ_MIDDLE_NAME -> requestMap.put(RQ_MIDDLE_NAME, value);
                case RQ_SURNAME -> requestMap.put(RQ_SURNAME, value);
                case RQ_PHONE_NUMBER -> requestMap.put(RQ_PHONE_NUMBER, value);
                case RQ_BIRTHDAY -> addBirthDayParameter(requestMap, value);
                case RQ_ID -> requestMap.put(RQ_ID, Integer.parseInt(value));
                case RQ_EXPERIENCE -> requestMap.put(RQ_EXPERIENCE, Integer.parseInt(value));
                case RQ_LIMIT -> limit = Integer.parseInt(value);
                case RQ_OFFSET -> offset = Integer.parseInt(value);
                default -> throw new IncorrectRequestException(String.format(PARAM_NOT_RECOGNISED, key));
            }
        }
        Transaction transaction = session.beginTransaction();
        teachers = repo.getTeachers(requestMap, limit, offset);
        List<DtoResponse> responses = mappingVoToDto(new ArrayList<>(teachers));
        transaction.commit();
        logger.trace(SERVICE_GETDATABYPARAMS_END, teachers.stream().map(Teacher::getId).toList());
        return responses;
    }

    @Override
    public List<DtoResponse> mappingVoToDto(List<ModelUnit> modelUnitList) {
        logger.trace(SERVICE_MAP_DTO_BEGIN, modelUnitList.size());
        List<DtoResponse> list = new ArrayList<>();
        for (ModelUnit modelUnit : modelUnitList) {
            Teacher teacher = (Teacher) modelUnit;
            list.add(mapper.mapTeacherToResponse(teacher));
        }
        logger.trace(SERVICE_MAP_DTO_END, list.size());
        return list;

    }

    @Override
    public DtoResponse mappingVoToDto(ModelUnit modelUnit) {
        Teacher teacher = (Teacher) modelUnit;
        return mapper.mapTeacherToResponse(teacher);
    }

    @Override
    public DtoResponse create(String path, DtoRequest request) throws NoDataException, IncorrectRequestException {
        logger.trace(SERVICE_CREATE, request);
        DtoResponse response;
        TeacherRequest teacherRequest = (TeacherRequest) request;
        Transaction transaction = session.beginTransaction();
        if (path==null || path.isEmpty()) {
            Teacher teacher = mapper.mapTeacherFromRequest((TeacherRequest) request);
            repo.addTeacher(teacher);
            response = mappingVoToDto(teacher);
            transaction.commit();
        } else {
            try {
                Integer id = Integer.parseInt(path);
                Optional<Subject> mayBeSubject = repo.getSubjectByRequestName(teacherRequest.getRqSubject());
                if (mayBeSubject.isEmpty()) throw new NoDataException(DATA_NOT_FOUND);
                Subject subject = mayBeSubject.get();
                if(!repo.addSubjectToTeacher(subject, id)) throw new NoDataException(DATA_NOT_UPDATED);
                transaction.commit();
                response = mappingVoToDto(repo.getTeacher(id).get());
            } catch (NumberFormatException e) {
                transaction.rollback();
                throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
            }
        }
        return response;
    }

    @Override
    public DtoResponse update(String path, DtoRequest dtoRequest) throws IncorrectRequestException, NoDataException {
        logger.trace(SERVICE_UPDATE_BEGIN, path, dtoRequest);
        if (path == null || path.isEmpty()) throw new IncorrectRequestException(INCORRECT_PATH_FORMAT);
        String[] params = path.split(PATH_SEPARATOR);
        TeacherRequest teacherRequest = (TeacherRequest) dtoRequest;
        Transaction transaction = session.beginTransaction();
        Teacher teacher;
        try {
            if (params.length == 1) {
                Optional<Teacher> mayBeTeacher = repo.getTeacher(Integer.parseInt(params[0]));
                if (mayBeTeacher.isEmpty()) throw new NoDataException(DATA_NOT_FOUND);
                teacher = updateFields(mayBeTeacher.get(), teacherRequest);
                teacher = repo.update(teacher);
            } else {
                throw new IncorrectRequestException(INCORRECT_PATH_FORMAT);
            }
        } catch (NumberFormatException e) { throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT); }
        DtoResponse result = mapper.mapTeacherToResponse(teacher);
        transaction.commit();
        logger.trace(SERVICE_UPDATE_END);
        return result;
    }

    private Teacher updateFields(Teacher teacher, TeacherRequest teacherRequest) {
        if (teacherRequest.getFirstName() != null) teacher.setFirstName(teacherRequest.getFirstName());
        if (teacherRequest.getMiddleName() != null) teacher.setMiddleName(teacherRequest.getMiddleName());
        if (teacherRequest.getSurName() != null) teacher.setSurName(teacherRequest.getSurName());
        if (teacherRequest.getBirthDay() != null) teacher.setBirthDay(teacherRequest.getBirthDay());
        if (teacherRequest.getExperienceBegin() != null)
            teacher.setExperienceBegin(teacherRequest.getExperienceBegin());
        if (teacherRequest.getPhoneNumber() != null) teacher.setPhoneNumber(teacherRequest.getPhoneNumber());
        return teacher;
    }

    @Override
    public DtoResponse delete(String path, DtoRequest request) throws IncorrectRequestException, NoDataException {
        logger.trace(SERVICE_DELETE_BEGIN, path);
        Transaction transaction = session.beginTransaction();
        try {
            Integer id = Integer.parseInt(path);
            if (request==null) {
                boolean deleteSuccessful = repo.removeTeacher(id);
                if (!deleteSuccessful) throw new NoDataException(DELETE_NOT_SUCCESSFULLY);
                logger.trace(SERVICE_DELETE_END, true);
                transaction.commit();
                return new NoDataResponse();
            } else {
                try {
                    TeacherRequest teacherRequest = (TeacherRequest) request;
                    if (teacherRequest.getRqSubject() == null)
                        throw new IncorrectRequestException(INCORRECT_BODY_OF_REQUEST);
                    Optional<Subject> maybeSubject = repo.getSubjectByRequestName(teacherRequest.getRqSubject());
                    if (maybeSubject.isEmpty()) throw new NoDataException(DATA_NOT_FOUND);
                    if (!repo.removeSubjectFromTeacher(maybeSubject.get(),id)) throw new NoDataException(DATA_NOT_UPDATED);
                    transaction.commit();
                    return mappingVoToDto(repo.getTeacher(id).get());
                } catch (NumberFormatException e) {
                    transaction.rollback();
                    throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
                }
            }
        } catch (NumberFormatException e) {
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }
    }

    private void addBirthDayParameter(Map<String, Object> parameterMap, String value) throws IncorrectRequestException {
        try {
            parameterMap.put(RQ_BIRTHDAY, LocalDate.parse(value));
        } catch (DateTimeParseException e) {
            throw new IncorrectRequestException(INCORRECT_DATE_FORMAT);
        }
    }
}
