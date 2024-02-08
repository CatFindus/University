package com.example.repository;

import com.example.model.vo.Student;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class StudentsRepository {
    private static final StudentsRepository instance = new StudentsRepository();
    private final ConcurrentSkipListMap<Integer, Student> students;

    private StudentsRepository() {
        students = new ConcurrentSkipListMap<>();
        students.put(3, new Student("1", "2", "3", LocalDate.of(2001, 12, 1), "4"));
    }

    static StudentsRepository getInstance() {
        return instance;
    }

    Student getStudentById(int id) {
        return students.get(id);
    }

    boolean addStudent(Student student) {
        if (students.containsValue(student)) return false;
        else students.put(student.getId(), student);
        return true;
    }

    boolean removeStudent(Integer id) {
        return students.remove(id) != null;
    }

    public List<Student> getStudents(List<Predicate<Student>> predicates) {
        Stream<Student> stream = students.values().stream();
        for (Predicate<Student> predicate : predicates) stream = stream.filter(predicate);
        return stream.toList();
    }
}
