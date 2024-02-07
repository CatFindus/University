package com.example.repository;

import com.example.model.vo.Student;
import com.example.model.vo.Teacher;

import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TeachersRepository {
    private static TeachersRepository instance = new TeachersRepository();
    private final ConcurrentSkipListMap<Integer, Teacher> teachers;

    private TeachersRepository() {
        teachers = new ConcurrentSkipListMap<>();
    }
    static TeachersRepository getInstance() {
        return instance;
    }

    Teacher getTeacherById(Integer id) {
        return teachers.get(id);
    }
    List<Teacher> getTeachers(Predicate<Teacher> predicate) {
        return teachers.values().stream().filter(predicate).toList();
    }
    public List<Teacher> getTeachers(List<Predicate<Teacher>> predicates) {
        Stream<Teacher> stream = teachers.values().stream();
        for(Predicate<Teacher> predicate:predicates) stream=stream.filter(predicate);
        return stream.toList();
    }
    boolean addTeacher(Teacher teacher) {
        if (teachers.containsValue(teacher)) return false;
        else teachers.put(teacher.getId(),teacher);
        return true;
    }
    boolean removeTeacher(Teacher teacher) {
        if (teachers.containsValue(teacher)) {
            Integer id = teachers.values().stream().findFirst().get().getId();
            teachers.remove(id);
            return true;
        }
        else return false;
    }
    boolean removeTeacher(Integer id) {
        if(teachers.remove(id)==null) return false;
        else return true;
    }

}
