package com.example.model.service;

import com.example.exeptions.IncorrectRequestException;
import com.example.exeptions.NoDataException;
import com.example.mapper.EntityMapper;
import com.example.model.dto.Request.DtoRequest;
import com.example.model.dto.Request.StudentRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.dto.Response.NoDataResponse;
import com.example.model.entities.ModelUnit;
import com.example.model.entities.Student;
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
public class StudentService implements Service {
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
        Optional<Student> mayBeStudent = repo.getStudent(id);
        logger.trace(SERVICE_GETDATABYID_END, mayBeStudent.map(Student::getId).orElse(null));
        if (mayBeStudent.isEmpty()) {
            throw new NoDataException(DATA_NOT_FOUND);
        } else {
            return mappingVoToDto(mayBeStudent.get());
        }
    }

    @Override
    public List<DtoResponse> getDataByParameters(Map<String, String[]> parameterMap) throws IncorrectRequestException {
        logger.trace(SERVICE_GETDATABYPARAMS_BEGIN, parameterMap.keySet());
        Map<String, Object> requestMap = new HashMap<>();
        int limit = 100;
        int offset = 0;
        List<Student> students;
        for (String key : parameterMap.keySet()) {
            String value = parameterMap.get(key)[0];
            switch (key) {
                case RQ_FIRST_NAME -> requestMap.put(RQ_FIRST_NAME, value);
                case RQ_MIDDLE_NAME -> requestMap.put(RQ_MIDDLE_NAME, value);
                case RQ_SURNAME -> requestMap.put(RQ_SURNAME, value);
                case RQ_GROUP_ID -> requestMap.put(RQ_GROUP_ID, Integer.parseInt(value));
                case RQ_GROUP_NUMBER -> requestMap.put(RQ_GROUP_NUMBER, value);
                case RQ_PHONE_NUMBER -> requestMap.put(RQ_PHONE_NUMBER, value);
                case RQ_BIRTHDAY -> addBirthDayParameter(requestMap, value);
                case RQ_ID -> requestMap.put(RQ_ID, Integer.parseInt(value));
                case RQ_LIMIT -> limit = Integer.parseInt(value);
                case RQ_OFFSET -> offset = Integer.parseInt(value);
                default -> throw new IncorrectRequestException(String.format(PARAM_NOT_RECOGNISED, key));
            }
        }
        Transaction transaction = session.beginTransaction();
        students = repo.getStudents(requestMap, limit, offset);
        List<DtoResponse> responses = mappingVoToDto(new ArrayList<>(students));
        transaction.commit();
        logger.trace(SERVICE_GETDATABYPARAMS_END, students.stream().map(Student::getId).toList());
        return responses;
    }

    private void addBirthDayParameter(Map<String,Object> parameterMap, String value) throws IncorrectRequestException {
        try {
            parameterMap.put(RQ_BIRTHDAY, LocalDate.parse(value));
        } catch (DateTimeParseException e) {
            throw new IncorrectRequestException(INCORRECT_DATE_FORMAT);
        }
    }

    @Override
    public List<DtoResponse> mappingVoToDto(List<ModelUnit> modelUnitList) {
        logger.trace(SERVICE_MAP_DTO_BEGIN, modelUnitList.size());
        List<DtoResponse> list = new ArrayList<>();
        for (ModelUnit student : modelUnitList)
            list.add(mapper.mapStudentToResponse((Student) student));
        logger.trace(SERVICE_MAP_DTO_END, list.size());
        return list;
    }

    @Override
    public DtoResponse mappingVoToDto(ModelUnit modelUnit) {
        Student student = (Student) modelUnit;
        return mapper.mapStudentToResponse(student);
    }


    @Override
    public DtoResponse create(String path, DtoRequest request) {
        logger.trace(SERVICE_CREATE, request);
        Transaction transaction = session.beginTransaction();
        Student student = mapper.mapStudentFromRequest((StudentRequest) request);
        repo.addStudent(student);
        DtoResponse response = mappingVoToDto(student);
        transaction.commit();
        return response;
    }

    @Override
    public DtoResponse update(String path, DtoRequest dtoRequest) throws IncorrectRequestException, NoDataException {
        logger.trace(SERVICE_UPDATE_BEGIN, path, dtoRequest);
        StudentRequest studentRequest = (StudentRequest) dtoRequest;
        Transaction transaction = session.beginTransaction();
        Student student;
        try {
            Optional<Student> mayBeStudent = repo.getStudent(Integer.parseInt(path));
            if (mayBeStudent.isEmpty()) throw new NoDataException(DATA_NOT_FOUND);
            student = updateFields(mayBeStudent.get(),studentRequest);
        } catch (NumberFormatException e) {
            transaction.rollback();
            throw new IncorrectRequestException(INCORRECT_PATH_FORMAT);
        }
        student = repo.update(student);
        DtoResponse result = mappingVoToDto(student);
        transaction.commit();
        logger.trace(SERVICE_UPDATE_END);
        return result;
    }

    private Student updateFields(Student student, StudentRequest studentRequest) {
        if (studentRequest.getFirstName() != null) student.setFirstName(studentRequest.getFirstName());
        if (studentRequest.getMiddleName() != null) student.setMiddleName(studentRequest.getMiddleName());
        if (studentRequest.getSurName() != null) student.setSurName(studentRequest.getSurName());
        if (studentRequest.getBirthDay() != null) student.setBirthDay(studentRequest.getBirthDay());
        if (studentRequest.getPhoneNumber() != null) student.setPhoneNumber(studentRequest.getPhoneNumber());
        return student;
    }

    @Override
    public DtoResponse delete(String path, DtoRequest request) throws IncorrectRequestException, NoDataException {
        logger.trace(SERVICE_DELETE_BEGIN, path);
        Transaction transaction = session.beginTransaction();
        try {
            int studentId = Integer.parseInt(path);
            boolean deleteSuccessful = repo.removeStudent(studentId);
            if (!deleteSuccessful) throw new NoDataException(DELETE_NOT_SUCCESSFULLY);
            logger.trace(SERVICE_DELETE_END, true);
        } catch (NumberFormatException e) {
            transaction.rollback();
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }
        transaction.commit();
        return new NoDataResponse();
    }
}
