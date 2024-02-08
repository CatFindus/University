package com.example.model.service;

import com.example.exeptions.IncorrectRequestException;
import com.example.exeptions.NoDataException;
import com.example.mapper.StudentMapper;
import com.example.mapper.StudentMapperImpl;
import com.example.model.dto.Request.DtoRequest;
import com.example.model.dto.Request.StudentRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.vo.Group;
import com.example.model.vo.ModelUnit;
import com.example.model.vo.Student;
import com.example.repository.RepositoryFacade;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.example.consts.LoggerConstants.*;
import static com.example.consts.ModelConstants.*;

@RequiredArgsConstructor
public class StudentService implements Service {
    private final static Logger logger = LoggerFactory.getLogger(StudentService.class);
    private final RepositoryFacade repo = new RepositoryFacade();
    private final StudentMapper mapper = new StudentMapperImpl();

    @Override
    public List<ModelUnit> getDataById(String idString) throws IncorrectRequestException {
        logger.trace(SERVICE_GETDATABYID_BEGIN, idString);
        int id;
        try {
            id = Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }
        Student result = repo.getStudent(id);
        logger.trace(SERVICE_GETDATABYID_END, result != null ? result.getId() : null);
        if (result != null) return List.of(result);
        else return new ArrayList<>();
    }

    @Override
    public List<ModelUnit> getDataByParameters(Map<String, String[]> parameterMap) throws IncorrectRequestException {
        logger.trace(SERVICE_GETDATABYPARAMS_BEGIN, parameterMap.keySet());
        List<Predicate<Student>> predicates = new ArrayList<>();
        List<Student> students = null;
        for (String key : parameterMap.keySet()) {
            String value = parameterMap.get(key)[0];
            switch (key) {
                case RQ_FIRST_NAME -> predicates.add(s -> s.hasFirstName(value));
                case RQ_MIDDLE_NAME -> predicates.add(s -> s.hasMiddleName(value));
                case RQ_SURNAME -> predicates.add(s -> s.hasSurName(value));
                case RQ_GROUP_ID -> students = getStudentsByGroupId(value);
                case RQ_GROUP_NUMBER -> {
                    if (!parameterMap.containsKey(RQ_GROUP_ID)) students = getStudentsByGroupNumber(value);
                }
                case RQ_PHONE_NUMBER -> predicates.add(s -> s.hasPhoneNumber(value));
                case RQ_BIRTHDAY -> addBirthDayPredicate(predicates, value);
                case RQ_ID -> students = getStudentsByParameters(parameterMap, key);
                default -> throw new IncorrectRequestException(String.format(PARAM_NOT_RECOGNISED, key));
            }
        }
        if (students == null) students = repo.getStudents(predicates);
        else students = filterStudentListByPredicates(students, predicates);
        logger.trace(SERVICE_GETDATABYPARAMS_END, students.stream().map(Student::getId).toList());
        return new ArrayList<>(students);
    }

    private List<Student> filterStudentListByPredicates(List<Student> students, List<Predicate<Student>> predicates) {
        Stream<Student> stream = students.stream();
        for (Predicate<Student> predicate : predicates) stream = stream.filter(predicate);
        return stream.toList();
    }

    private List<Student> getStudentsByParameters(Map<String, String[]> parameterMap, String key) throws IncorrectRequestException {
        List<Student> students = new ArrayList<>();
        Student student;
        try {
            student = repo.getStudent(Integer.parseInt(parameterMap.get(key)[0]));
            if (student != null) students.add(student);
        } catch (NumberFormatException e) {
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }
        return students;
    }

    private void addBirthDayPredicate(List<Predicate<Student>> predicates, String value) throws IncorrectRequestException {
        try {
            predicates.add(s -> s.hasBirthDay(LocalDate.parse(value)));
        } catch (DateTimeParseException e) {
            throw new IncorrectRequestException(INCORRECT_DATE_FORMAT);
        }
    }

    private List<Student> getStudentsByGroupId(String value) throws IncorrectRequestException {
        List<Student> students;
        try {
            Group group = repo.getGroup(Integer.parseInt(value));
            if (group == null) return new ArrayList<>();
            students = new ArrayList<>(group.getStudents());
        } catch (NumberFormatException e) {
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }
        return students;
    }

    private List<Student> getStudentsByGroupNumber(String groupNumber) {
        List<Group> groups = repo.getGroups(gr -> gr.hasNumber(groupNumber));
        Set<Student> students = new HashSet<>();
        for (Group group : groups) students.addAll(group.getStudents());
        return students.stream().toList();
    }

    @Override
    public List<DtoResponse> mappingVoToDto(List<ModelUnit> modelUnitList) {
        logger.trace(SERVICE_MAP_DTO_BEGIN, modelUnitList.size());
        List<DtoResponse> list = new ArrayList<>();
        for (ModelUnit student : modelUnitList)
            list.add(mapper.mapToResponse((Student) student, repo.getGroupIdByStudent((Student) student)));
        logger.trace(SERVICE_MAP_DTO_END, list.size());
        return list;
    }

    @Override
    public boolean create(ModelUnit modelUnit) {
        logger.trace(SERVICE_CREATE, ((Student) modelUnit).getId());
        return repo.addStudent((Student) modelUnit);
    }

    @Override
    public List<DtoResponse> update(String path, DtoRequest dtoRequest) throws IncorrectRequestException, NoDataException {
        logger.trace(SERVICE_UPDATE_BEGIN, path, dtoRequest);
        StudentRequest studentRequest = (StudentRequest) dtoRequest;
        List<ModelUnit> studentList = getDataById(path);
        if (studentList.isEmpty()) throw new NoDataException(DATA_NOT_FOUND);
        Student student = getStudent(studentList, studentRequest);
        List<DtoResponse> result = List.of(mapper.mapToResponse(student, repo.getGroupIdByStudent(student)));
        logger.trace(SERVICE_UPDATE_END);
        return result;
    }

    private Student getStudent(List<ModelUnit> studentList, StudentRequest studentRequest) {
        Student student = (Student) studentList.get(0);
        if (studentRequest.getFirstName() != null) student.setFirstName(studentRequest.getFirstName());
        if (studentRequest.getMiddleName() != null) student.setMiddleName(studentRequest.getMiddleName());
        if (studentRequest.getSurName() != null) student.setSurName(studentRequest.getSurName());
        if (studentRequest.getBirthDay() != null) student.setBirthDay(studentRequest.getBirthDay());
        if (studentRequest.getPhoneNumber() != null) student.setPhoneNumber(studentRequest.getPhoneNumber());
        return student;
    }

    @Override
    public void delete(String path) throws IncorrectRequestException, NoDataException {
        logger.trace(SERVICE_DELETE_BEGIN, path);
        try {
            int studentId = Integer.parseInt(path);
            Student student = repo.getStudent(studentId);
            if (student == null) throw new NoDataException(DELETE_NOT_SUCCESSFULLY);
            Integer groupId = repo.getGroupIdByStudent(student);
            if (groupId != null) repo.getGroup(groupId).getStudents().remove(student);
            boolean deleteSuccessful = repo.removeStudent(studentId);
            if (!deleteSuccessful) throw new NoDataException(DELETE_NOT_SUCCESSFULLY);
            logger.trace(SERVICE_DELETE_END, true);
        } catch (NumberFormatException e) {
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }
    }
}
