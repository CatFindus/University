package com.example.repository;

import com.example.model.vo.Group;
import com.example.model.vo.Student;

import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class GroupsRepository {
    private static final GroupsRepository instance = new GroupsRepository();
    private final ConcurrentSkipListMap<Integer, Group> groups;

    private GroupsRepository() {
        groups = new ConcurrentSkipListMap<>();
    }

    static GroupsRepository getInstance() {
        return instance;
    }

    Group getGroupById(int id) {
        return groups.get(id);
    }

    List<Group> getGroups(Predicate<Group> predicate) {
        return groups.values().stream().filter(predicate).toList();
    }

    List<Group> getGroups(List<Predicate<Group>> predicates) {
        Stream<Group> stream = groups.values().stream();
        for (Predicate<Group> predicate : predicates) stream = stream.filter(predicate);
        return stream.toList();
    }

    boolean addGroup(Group group) {
        if (groups.containsValue(group)) return false;
        else groups.put(group.getId(), group);
        return true;
    }

    boolean removeGroup(Group group) {
        if (groups.containsValue(group)) {
            Integer id = groups.values().stream().findFirst().get().getId();
            groups.remove(id);
            return true;
        } else return false;
    }

    boolean removeGroup(Integer id) {
        Group group = groups.remove(id);
        return group != null;
    }

    boolean removeStudentFromGroup(Student student, Integer groupId) {
        Group group = groups.get(groupId);
        if (group == null) return false;
        return group.getStudents().remove(student);
    }

    boolean addStudentToGroup(Student student, Integer groupId) {
        Group group = groups.get(groupId);
        if (group == null) return false;
        return group.getStudents().add(student);
    }
}
