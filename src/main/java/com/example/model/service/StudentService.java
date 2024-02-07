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
import com.example.view.View;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class StudentService implements Service {
    private final RepositoryFacade repo = new RepositoryFacade();
    private final StudentMapper mapper= new StudentMapperImpl();
    @Override
    public List<ModelUnit> getDataById(String idString) throws IncorrectRequestException {
        int id;
        try {
            id = Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            throw new IncorrectRequestException();
        }
        Student result = repo.getStudent(id);
        if(result!=null) return List.of(result);
        else return new ArrayList<>();
    }
    @Override
    public List<ModelUnit> getDataByParameters(Map<String, String[]> parameterMap) throws IncorrectRequestException {
        List<Predicate<Student>> predicates = new ArrayList<>();
        List<Student> students=null;
            for(String key:parameterMap.keySet()) {
                String value = parameterMap.get(key)[0];
                switch (key) {
                    case "firstname" -> predicates.add( s -> s.hasFirstName(value));
                    case "middlename" -> predicates.add( s -> s.hasMiddleName(value));
                    case "surname" -> predicates.add( s -> s.hasSurName(value));
                    case "groupid" -> students = getStudentsByGroupId(value);
                    case "groupnumber" -> { if(!parameterMap.containsKey("groupid")) students = getStudentsByGroupNumber(value); }
                    case "phonenumber" -> predicates.add( s -> s.hasPhoneNumber(value));
                    case "birthday" -> addBirthDayPredicate(predicates, value);
                    case "id" -> students = getStudentsByParameters(parameterMap, key);
                    default -> throw new IncorrectRequestException();
                }
            }
        if (students==null) students = repo.getStudents(predicates);
        else students = filterStudentListByPredicates(students, predicates);
        List<ModelUnit> result = new ArrayList<>();
        for(Student student:students) result.add(student);
        return result;
    }

    private List<Student> filterStudentListByPredicates(List<Student> students, List<Predicate<Student>> predicates) {
        Stream<Student> stream = students.stream();
        for(Predicate<Student> predicate: predicates) stream=stream.filter(predicate);
        return stream.toList();
    }

    private List<Student> getStudentsByParameters(Map<String, String[]> parameterMap, String key) throws IncorrectRequestException {
        List<Student> students = new ArrayList<>();
        Student student;
        try {
            student = repo.getStudent(Integer.parseInt(parameterMap.get(key)[0]));
            if(student!=null) students.add(student);
        } catch (NumberFormatException e) { throw new IncorrectRequestException(); }
        return students;
    }
    private void addBirthDayPredicate(List<Predicate<Student>> predicates, String value) throws IncorrectRequestException {
        try {
            predicates.add(s -> s.hasBirthDay(LocalDate.parse(value)));
        } catch (DateTimeParseException e) { throw new IncorrectRequestException(); }
    }

    private List<Student> getStudentsByGroupId(String value) throws IncorrectRequestException {
        List<Student> students;
        try {
            Group group = repo.getGroup(Integer.parseInt(value));
            if (group==null) return new ArrayList<>();
            students = new ArrayList<>(group.getStudents());
        } catch (NumberFormatException e) { throw new IncorrectRequestException(); }
        return students;
    }

    private List<Student> getStudentsByGroupNumber(String groupNumber) {
        List<Group> groups = repo.getGroups(gr -> gr.hasNumber(groupNumber));
        Set<Student> students = new HashSet<>();
        for(Group group:groups) students.addAll(group.getStudents());
        return students.stream().toList();
    }
    @Override
    public List<DtoResponse> mappingVoToDto(List<ModelUnit> modelUnitList) {
        List<DtoResponse> list = new ArrayList<>();
        for(ModelUnit student: modelUnitList) list.add(mapper.mapToResponse((Student) student, repo.getGroupIdByStudent((Student) student)));
        return list;
    }
    @Override
    public boolean create(ModelUnit modelUnit) {
        return repo.addStudent((Student) modelUnit);
    }
    @Override
    public List<DtoResponse> update(String id, DtoRequest dtoRequest) throws IncorrectRequestException, NoDataException {
        StudentRequest studentRequest = (StudentRequest) dtoRequest;
        List<ModelUnit> studentList = getDataById(id);
        if (studentList.isEmpty()) throw new NoDataException();
        Student student = (Student) studentList.get(0);
        if(studentRequest.getFirstName()!=null) student.setFirstName(studentRequest.getFirstName());
        if(studentRequest.getMiddleName()!=null) student.setMiddleName(studentRequest.getMiddleName());
        if(studentRequest.getSurName()!=null) student.setSurName(studentRequest.getSurName());
        if(studentRequest.getBirthDay()!=null) student.setBirthDay(studentRequest.getBirthDay());
        if(studentRequest.getPhoneNumber()!=null) student.setPhoneNumber(studentRequest.getPhoneNumber());
        List<DtoResponse> result = List.of(mapper.mapToResponse(student, repo.getGroupIdByStudent(student)));
        return result;
    }
    @Override
    public void delete(String path) throws IncorrectRequestException, NoDataException {
        try {
            boolean deleteSuccessful = repo.removeStudent(Integer.parseInt(path));
            if (!deleteSuccessful) throw new  NoDataException();
        } catch (NumberFormatException e) {
            throw new IncorrectRequestException();
        }
    }
}
