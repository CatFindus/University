package com.example.model.service;

import com.example.exeptions.IncorrectRequestException;
import com.example.exeptions.NoDataException;
import com.example.mapper.GroupMapper;
import com.example.mapper.GroupMapperImpl;
import com.example.model.dto.Request.DtoRequest;
import com.example.model.dto.Request.GroupRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.vo.Group;
import com.example.model.vo.ModelUnit;
import com.example.model.vo.Student;
import com.example.repository.RepositoryFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class GroupService implements Service{
    private final RepositoryFacade repo = new RepositoryFacade();
    private final GroupMapper mapper= new GroupMapperImpl();
    private final Service service = new StudentService();

    @Override
    public List<ModelUnit> getDataById(String idString) throws IncorrectRequestException {
        int id;
        try {
            id = Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            throw new IncorrectRequestException();
        }
        Group result = repo.getGroup(id);
        if(result!=null) return List.of(result);
        else return new ArrayList<>();
    }

    @Override
    public List<ModelUnit> getDataByParameters(Map<String, String[]> parameterMap) throws IncorrectRequestException {
        List<Predicate<Group>> predicates = new ArrayList<>();
        List<Group> groups=null;
        for(String key:parameterMap.keySet()) {
            String value = parameterMap.get(key)[0];
            switch (key) {
                case "number" -> predicates.add( gr -> gr.getNumber().equalsIgnoreCase(value));
                case "id" -> {
                    try {
                        groups = List.of(repo.getGroup(Integer.parseInt(value)));
                    } catch (NumberFormatException e) { throw new IncorrectRequestException(); }
                }
                default -> throw new IncorrectRequestException();
            }
        }
        if (groups==null) groups = repo.getGroups(predicates);
        else groups = filterGroupListByPredicates(groups, predicates);
        List<ModelUnit> result = new ArrayList<>();
        for(Group group:groups) result.add(group);
        return result;
    }

    private List<Group> filterGroupListByPredicates(List<Group> groups, List<Predicate<Group>> predicates) {
        Stream<Group> stream = groups.stream();
        for (Predicate<Group> predicate:predicates) stream=stream.filter(predicate);
        return stream.toList();
    }

    @Override
    public List<DtoResponse> mappingVoToDto(List<ModelUnit> modelUnitList) {
        List<DtoResponse> list = new ArrayList<>();
        for(ModelUnit unit: modelUnitList) {
            Group group = (Group) unit;
            List<DtoResponse> studentResponses = new ArrayList<>(service.mappingVoToDto(new ArrayList<>(group.getStudents())));
            list.add(mapper.mapToResponse(group, studentResponses));
        }
        return list;
    }

    @Override
    public boolean create(ModelUnit modelUnit) {
        return repo.addGroup((Group) modelUnit);
    }

    @Override
    public List<DtoResponse> update(String path, DtoRequest dtoRequest) throws IncorrectRequestException, NoDataException {
        if (path.isEmpty()) throw new IncorrectRequestException();
        if(path.contains("/")) {
            String[] strings = path.split("/");
            if(strings.length==1) return updateGroup(strings[0], dtoRequest);
            else {
                try {
                    Integer groupId = Integer.parseInt(strings[0]);
                    for (int i = 1; i < strings.length; i++) {
                        int studentId = Integer.parseInt(strings[i]);
                        Student student = repo.getStudent(studentId);
                        repo.addStudentToGroup(student, groupId);
                    }
                    Group group = repo.getGroup(groupId);
                    List<ModelUnit> students = new ArrayList<>(group.getStudents());
                    List<DtoResponse> studentResponses = service.mappingVoToDto(students);
                    return List.of(mapper.mapToResponse(group, studentResponses));
                } catch (NumberFormatException e) { throw new IncorrectRequestException(); }
            }
        } else {
            return updateGroup(path, dtoRequest);
        }
    }

    private List<DtoResponse> updateGroup(String id, DtoRequest dtoRequest) throws IncorrectRequestException {
        try {
            int intId = Integer.parseInt(id);
            Group group = repo.getGroup(intId);
            GroupRequest groupRequest = (GroupRequest)  dtoRequest;
            if(groupRequest.getNumber()!=null) group.setNumber(groupRequest.getNumber());
            List<ModelUnit> students = new ArrayList<>(group.getStudents());
            List<DtoResponse> studentResponses = new ArrayList<>(service.mappingVoToDto(students));
            return List.of(mapper.mapToResponse(group, studentResponses));
        } catch (NumberFormatException e) { throw new IncorrectRequestException(); }
    }

    @Override
    public void delete(String path) throws IncorrectRequestException, NoDataException {
        if (path.isEmpty()) throw new IncorrectRequestException();
        if(path.contains("/")) {
            String[] strings = path.split("/");
            if(strings.length==1) deleteGroup(strings[0]);
            try {
                for (int i = 1; i < strings.length; i++) {
                    Integer studentId = Integer.parseInt(strings[i]);
                    Integer groupId = Integer.parseInt(strings[0]);
                    repo.removeStudentFromGroup(repo.getStudent(studentId), groupId);
                }
            } catch (NumberFormatException e) { throw new IncorrectRequestException(); }            
        } else {
            deleteGroup(path);
        }
    }

    private void deleteGroup(String id) throws IncorrectRequestException, NoDataException {
        try {
            Integer intId = Integer.parseInt(id);
            boolean removedSuccessful = repo.removeGroup(intId);
            if(!removedSuccessful) throw new NoDataException();
        } catch (NumberFormatException e) { throw new IncorrectRequestException(); }
    }
}
