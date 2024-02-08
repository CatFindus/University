package com.example.model.service;

import com.example.exeptions.IncorrectRequestException;
import com.example.exeptions.NoDataException;
import com.example.mapper.TeacherMapper;
import com.example.mapper.TeacherMapperImpl;
import com.example.model.dto.Request.DtoRequest;
import com.example.model.dto.Request.TeacherRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.vo.ModelUnit;
import com.example.model.vo.Teacher;
import com.example.repository.RepositoryFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.example.consts.LoggerConstants.*;
import static com.example.consts.ModelConstants.*;

public class TeacherService implements Service {
    private final static Logger logger = LoggerFactory.getLogger(StudentService.class);
    private final RepositoryFacade repo = new RepositoryFacade();
    private final TeacherMapper mapper = new TeacherMapperImpl();

    @Override
    public List<ModelUnit> getDataById(String idString) throws IncorrectRequestException {
        logger.trace(SERVICE_GETDATABYID_BEGIN, idString);
        int id;
        try {
            id = Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }
        Teacher teacher = repo.getTeacher(id);
        logger.trace(SERVICE_GETDATABYID_END, teacher != null ? teacher.getId() : null);
        if (teacher != null) return List.of(teacher);
        else return new ArrayList<>();
    }

    @Override
    public List<ModelUnit> getDataByParameters(Map<String, String[]> parameterMap) throws IncorrectRequestException {
        logger.trace(SERVICE_GETDATABYPARAMS_BEGIN, parameterMap.keySet());
        List<Predicate<Teacher>> predicates = new ArrayList<>();
        List<Teacher> teachers = null;
        for (String key : parameterMap.keySet()) {
            String value = parameterMap.get(key)[0];
            switch (key) {
                case RQ_FIRST_NAME -> predicates.add(s -> s.hasFirstName(value));
                case RQ_MIDDLE_NAME -> predicates.add(s -> s.hasMiddleName(value));
                case RQ_SURNAME -> predicates.add(s -> s.hasSurName(value));
                case RQ_PHONE_NUMBER -> predicates.add(s -> s.hasPhoneNumber(value));
                case RQ_BIRTHDAY -> addBirthDayPredicate(predicates, value);
                case RQ_ID -> teachers = getTeachersByParameters(parameterMap, key);
                case RQ_EXPERIENCE -> predicates.add(s -> s.getExperience() == Integer.parseInt(value));
                default -> throw new IncorrectRequestException(String.format(PARAM_NOT_RECOGNISED, key));
            }
        }
        if (teachers == null) teachers = repo.getTeachers(predicates);
        logger.trace(SERVICE_GETDATABYPARAMS_END, teachers.stream().map(Teacher::getId).toList());
        return new ArrayList<>(teachers);
    }

    @Override
    public List<DtoResponse> mappingVoToDto(List<ModelUnit> modelUnitList) {
        logger.trace(SERVICE_MAP_DTO_BEGIN, modelUnitList.size());
        List<DtoResponse> list = new ArrayList<>();
        for (ModelUnit modelUnit : modelUnitList) {
            Teacher teacher = (Teacher) modelUnit;
            list.add(mapper.mapToResponse(teacher, Integer.toString(teacher.getExperience())));
        }
        logger.trace(SERVICE_MAP_DTO_END, list.size());
        return list;

    }

    @Override
    public boolean create(ModelUnit modelUnit) {
        logger.trace(SERVICE_CREATE, ((Teacher) modelUnit).getId());
        return repo.addTeacher((Teacher) modelUnit);
    }

    @Override
    public List<DtoResponse> update(String path, DtoRequest dtoRequest) throws IncorrectRequestException, NoDataException {
        logger.trace(SERVICE_UPDATE_BEGIN, path, dtoRequest);
        TeacherRequest teacherRequest = (TeacherRequest) dtoRequest;
        List<ModelUnit> teacherList = getDataById(path);
        if (teacherList.isEmpty()) throw new NoDataException(DATA_NOT_FOUND);
        Teacher teacher = getTeacher(teacherList, teacherRequest);
        DtoResponse dtoUpdated = mapper.mapToResponse(teacher, Integer.toString(teacher.getExperience()));
        List<DtoResponse> result = List.of(dtoUpdated);
        logger.trace(SERVICE_UPDATE_END);
        return result;
    }

    private Teacher getTeacher(List<ModelUnit> teacherList, TeacherRequest teacherRequest) {
        Teacher teacher = (Teacher) teacherList.get(0);
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
    public void delete(String path) throws IncorrectRequestException, NoDataException {
        logger.trace(SERVICE_DELETE_BEGIN, path);
        try {
            boolean deleteSuccessful = repo.removeTeacher(Integer.parseInt(path));
            if (!deleteSuccessful) throw new NoDataException(DELETE_NOT_SUCCESSFULLY);
            logger.trace(SERVICE_DELETE_END, true);
        } catch (NumberFormatException e) {
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }
    }

    private void addBirthDayPredicate(List<Predicate<Teacher>> predicates, String value) throws IncorrectRequestException {
        try {
            predicates.add(s -> s.hasBirthDay(LocalDate.parse(value)));
        } catch (DateTimeParseException e) {
            throw new IncorrectRequestException(INCORRECT_DATE_FORMAT);
        }
    }

    private List<Teacher> getTeachersByParameters(Map<String, String[]> parameterMap, String key) throws IncorrectRequestException {
        List<Teacher> teachers = new ArrayList<>();
        Teacher teacher;
        try {
            teacher = repo.getTeacher(Integer.parseInt(parameterMap.get(key)[0]));
            if (teacher != null) teachers.add(teacher);
        } catch (NumberFormatException e) {
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }
        return teachers;
    }
}
