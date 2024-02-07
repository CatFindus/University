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
import com.example.view.View;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class TeacherService implements Service{
    View view;
    private final RepositoryFacade repo = new RepositoryFacade();
    private final TeacherMapper mapper= new TeacherMapperImpl();
    @Override
    public List<ModelUnit> getDataById(String idString) throws IncorrectRequestException {
        int id;
        try {
            id = Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            throw new IncorrectRequestException();
        }
        Teacher teacher = repo.getTeacher(id);
        if(teacher!=null) return List.of(teacher);
        else return new ArrayList<>();
    }

    @Override
    public List<ModelUnit> getDataByParameters(Map<String, String[]> parameterMap) throws IncorrectRequestException {
        List<Predicate<Teacher>> predicates = new ArrayList<>();
        List<Teacher> teachers=null;
        for(String key:parameterMap.keySet()) {
            String value = parameterMap.get(key)[0];
            switch (key) {
                case "firstname" -> predicates.add( s -> s.hasFirstName(value));
                case "middlename" -> predicates.add( s -> s.hasMiddleName(value));
                case "surname" -> predicates.add( s -> s.hasSurName(value));
                case "phonenumber" -> predicates.add( s -> s.hasPhoneNumber(value));
                case "birthday" -> addBirthDayPredicate(predicates, value);
                case "id" -> teachers = getTeachersByParameters(parameterMap, key);
                case "experience" ->  predicates.add(s -> s.getExperience()==Integer.parseInt(value));
                default -> throw new IncorrectRequestException();
            }
        }
        if (teachers==null) teachers = repo.getTeachers(predicates);
        List<ModelUnit> result = new ArrayList<>();
        for(Teacher teacher:teachers) result.add(teacher);
        return result;
    }

    @Override
    public List<DtoResponse> mappingVoToDto(List<ModelUnit> modelUnitList) {
        List<DtoResponse> list = new ArrayList<>();
        for(ModelUnit modelUnit: modelUnitList) {
            Teacher teacher = (Teacher) modelUnit;
            list.add(mapper.mapToResponse(teacher, Integer.toString(teacher.getExperience())));
        }
        return list;

    }

    @Override
    public boolean create(ModelUnit modelUnit) {
        return repo.addTeacher((Teacher) modelUnit);
    }

    @Override
    public List<DtoResponse> update(String id, DtoRequest dtoRequest) throws IncorrectRequestException, NoDataException {
        TeacherRequest teacherRequest = (TeacherRequest) dtoRequest;
        List<ModelUnit> teacherList = getDataById(id);
        if (teacherList.isEmpty()) throw new NoDataException();
        Teacher teacher = (Teacher) teacherList.get(0);
        if(teacherRequest.getFirstName()!=null) teacher.setFirstName(teacherRequest.getFirstName());
        if(teacherRequest.getMiddleName()!=null) teacher.setMiddleName(teacherRequest.getMiddleName());
        if(teacherRequest.getSurName()!=null) teacher.setSurName(teacherRequest.getSurName());
        if(teacherRequest.getBirthDay()!=null) teacher.setBirthDay(teacherRequest.getBirthDay());
        if(teacherRequest.getExperienceBegin()!=null) teacher.setExperienceBegin(teacherRequest.getExperienceBegin());
        if(teacherRequest.getPhoneNumber()!=null) teacher.setPhoneNumber(teacherRequest.getPhoneNumber());
        //if(teacherRequest.getSubjects()!=null) teacher.setSubjects(teacherRequest.getSubjects());
        DtoResponse dtoUpdated = mapper.mapToResponse(teacher, Integer.toString(teacher.getExperience()));
        List<DtoResponse> result = List.of(dtoUpdated);
        return result;
    }

    @Override
    public void delete(String path) throws IncorrectRequestException, NoDataException {
        try {
            boolean deleteSuccessful = repo.removeTeacher(Integer.parseInt(path));
            if (!deleteSuccessful) throw new  NoDataException();
        } catch (NumberFormatException e) {
            throw new IncorrectRequestException();
        }
    }
    private void addBirthDayPredicate(List<Predicate<Teacher>> predicates, String value) throws IncorrectRequestException {
        try {
            predicates.add(s -> s.hasBirthDay(LocalDate.parse(value)));
        } catch (DateTimeParseException e) { throw new IncorrectRequestException(); }
    }
    private List<Teacher> getTeachersByParameters(Map<String, String[]> parameterMap, String key) throws IncorrectRequestException {
        List<Teacher> students = new ArrayList<>();
        Teacher teacher;
        try {
            teacher = repo.getTeacher(Integer.parseInt(parameterMap.get(key)[0]));
            if(teacher!=null) students.add(teacher);
        } catch (NumberFormatException e) { throw new IncorrectRequestException(); }
        return students;
    }
}
