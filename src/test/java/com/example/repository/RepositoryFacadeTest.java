package com.example.repository;

import com.example.model.vo.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryFacadeTest {
    StudentsRepository sr = Mockito.mock(StudentsRepository.class);
    TeachersRepository tr = Mockito.mock(TeachersRepository.class);
    GroupsRepository gr = Mockito.mock(GroupsRepository.class);
    SchedulesRepository schlr = Mockito.mock(SchedulesRepository.class);
    RepositoryFacade repo = new RepositoryFacade(sr,tr,schlr,gr);


    @Test
    void getStudent() {
        repo.getStudent(1);
        Mockito.verify(sr, Mockito.times(1)).getStudentById(1);
    }

    @Test
    void getStudents() {
        repo.getStudents(new ArrayList<>());
        Mockito.verify(sr, Mockito.times(1)).getStudents(new ArrayList<>());
    }

    @Test
    void addStudent() {
        Student student = new Student();
        repo.addStudent(student);
        Mockito.verify(sr, Mockito.times(1)).addStudent(student);
    }

    @Test
    void removeStudent() {
        repo.removeStudent(1);
        Mockito.verify(sr, Mockito.times(1)).removeStudent(1);
    }

    @Test
    void getGroup() {
        gr.getGroupById(1);
        Mockito.verify(gr, Mockito.times(1)).getGroupById(1);
    }

    @Test
    void getGroupsByPredicate() {
        Predicate<Group> predicate = new Predicate<Group>() {
            @Override
            public boolean test(Group group) {
                return true;
            }
        };
       gr.getGroups(predicate);
       Mockito.verify(gr, Mockito.times(1)).getGroups(predicate);
    }

    @Test
    void getGroupsByPredicates() {
        List<Predicate<Group>> predicates = new ArrayList<>();
        predicates.add(group -> group.hasStudent(1));
        repo.getGroups(predicates);
        Mockito.verify(gr, Mockito.times(2)).getGroups(predicates);
    }

    @Test
    void addGroup() {
        Group group = new Group();
        repo.addGroup(group);
        Mockito.verify(gr, Mockito.times(1)).addGroup(group);
    }

    @Test
    void removeGroup() {
        Group group = new Group();
        repo.removeGroup(group);
        repo.removeGroup(1);
        Mockito.verify(gr, Mockito.times(1)).removeGroup(group);
        Mockito.verify(gr, Mockito.times(1)).removeGroup(Mockito.anyInt());
    }

    @Test
    void getTeacher() {
        repo.getTeacher(1);
        Mockito.verify(tr, Mockito.times(1)).getTeacherById(1);
    }

    @Test
    void getTeachers() {
        repo.getTeachers(new ArrayList<>());
        Mockito.verify(tr, Mockito.times(1)).getTeachers(new ArrayList<>());
    }

    @Test
    void removeTeacher() {
        repo.removeTeacher(1);
        Mockito.verify(tr, Mockito.times(1)).removeTeacher(1);
    }

    @Test
    void getGroupIdByStudent() {
        Student student = new Student();
        repo.getGroupIdByStudent(student);
        Mockito.verify(gr, Mockito.times(1)).getGroups(Mockito.any(Predicate.class));
    }

    @Test
    void addTeacher() {
        Teacher teacher = new Teacher();
        repo.addTeacher(teacher);
        Mockito.verify(tr, Mockito.times(1)).addTeacher(teacher);
    }

    @Test
    void removeStudentFromGroup() {
        Student student = new Student();
        Mockito.when(gr.removeStudentFromGroup(student, 1)).thenReturn(true);
        assertTrue(repo.removeStudentFromGroup(student,1));
        Mockito.verify(gr, Mockito.times(1)).removeStudentFromGroup(student,1);
    }

    @Test
    void addStudentToGroup() {
        Student student = new Student();
        Mockito.when(gr.addStudentToGroup(student, 1)).thenReturn(true);
        assertTrue(repo.addStudentToGroup(student,1));
        Mockito.verify(gr, Mockito.times(1)).addStudentToGroup(student,1);
    }

    @Test
    void getSchedules() {
        LocalDateTime ldt = LocalDateTime.now();
        repo.getSchedules(ldt, ldt, new ArrayList<>());
        Mockito.verify(schlr, Mockito.times(1)).getSchedules(ldt,ldt,new ArrayList<>());
    }

    @Test
    void addSchedule() {
        Schedule schedule = new Schedule(LocalDate.now());
        repo.addSchedule(schedule);
        Mockito.verify(schlr, Mockito.times(1)).addSchedule(schedule);
    }

    @Test
    void addScheduleUnit() {
        ScheduleUnit unit = ScheduleUnit.builder().build();
        repo.addScheduleUnit(unit);
        Mockito.verify(schlr, Mockito.times(1)).addSchedule(unit);
    }

    @Test
    void getScheduleByUnit() {
        ScheduleUnit unit = ScheduleUnit.builder().build();
        Schedule schedule = new Schedule(LocalDate.now());
        Mockito.when(schlr.getSheduleByUnit(Mockito.any())).thenReturn(schedule);
        repo.getScheduleByUnit(unit);
        Mockito.verify(schlr, Mockito.times(1)).getSheduleByUnit(unit);
    }

    @Test
    void getSchedule() {
        repo.getSchedule(LocalDate.now());
        Mockito.verify(schlr, Mockito.times(1)).getSchedule(Mockito.any());
    }
}