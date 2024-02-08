package com.example.repository;

import com.example.model.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

import static com.example.consts.LoggerConstants.*;

public class RepositoryFacade {
    private final static Logger logger = LoggerFactory.getLogger(RepositoryFacade.class);
    private final StudentsRepository studentsRepository;
    private final TeachersRepository teachersRepository;
    private final SchedulesRepository schedulesRepository;
    private final GroupsRepository groupsRepository;

    public RepositoryFacade() {
        studentsRepository = StudentsRepository.getInstance();
        teachersRepository = TeachersRepository.getInstance();
        schedulesRepository = SchedulesRepository.getInstance();
        groupsRepository = GroupsRepository.getInstance();
    }

    public Student getStudent(int id) {
        Student student = studentsRepository.getStudentById(id);
        logger.trace(REPO_GET_STUDENT_BY_ID, id, student);
        return student;
    }

    public List<Student> getStudents(List<Predicate<Student>> predicates) {
        List<Student> students = studentsRepository.getStudents(predicates);
        logger.trace(REPO_GET_STUDENT_BY_PREDICATES, predicates.size(), students.stream().map(Student::getId).toList());
        return students;
    }

    public boolean addStudent(Student student) {
        boolean added = studentsRepository.addStudent(student);
        logger.trace(REPO_ADD_STUDENT, student, added);
        return added;
    }

    public boolean removeStudent(Integer id) {
        boolean removed = studentsRepository.removeStudent(id);
        logger.trace(REPO_REMOVE_STUDENT, id, removed);
        return removed;
    }

    public Group getGroup(int id) {
        Group group = groupsRepository.getGroupById(id);
        logger.trace(REPO_GET_GROUP, id, group);
        return group;
    }

    public List<Group> getGroups(Predicate<Group> predicate) {
        List<Group> groups = groupsRepository.getGroups(predicate);
        logger.trace(REPO_GET_GROUPS, predicate, groups.size());
        return groups;
    }

    public List<Group> getGroups(List<Predicate<Group>> predicates) {
        List<Group> groups = groupsRepository.getGroups(predicates);
        logger.trace(REPO_GET_GROUPS, predicates.size(), groups.size());
        return groupsRepository.getGroups(predicates);
    }

    public boolean addGroup(Group group) {
        boolean added = groupsRepository.addGroup(group);
        logger.trace(REPO_ADD_GROUP, group, added);
        return added;
    }

    @SuppressWarnings("unused")
    public boolean removeGroup(Group group) {
        boolean removed = groupsRepository.removeGroup(group);
        logger.trace(REPO_REMOVE_GROUP, group, removed);
        return removed;
    }

    public boolean removeGroup(Integer id) {
        boolean removed = groupsRepository.removeGroup(id);
        logger.trace(REPO_REMOVE_GROUP, id, removed);
        return removed;
    }

    public Teacher getTeacher(Integer id) {
        Teacher teacher = teachersRepository.getTeacherById(id);
        logger.trace(REPO_GET_TEACHER, id, teacher);
        return teacher;
    }

    public List<Teacher> getTeachers(List<Predicate<Teacher>> predicates) {
        List<Teacher> teachers = teachersRepository.getTeachers(predicates);
        logger.trace(REPO_GET_TEACHERS_BY_PREDICATES, predicates.size(), teachers.stream().map(Teacher::getId).toList());
        return teachers;
    }

    public boolean removeTeacher(Integer id) {
        boolean removed = teachersRepository.removeTeacher(id);
        logger.trace(REPO_REMOVE_TEACHER, id, removed);
        return removed;
    }

    public Integer getGroupIdByStudent(Student student) {
        List<Group> groups = groupsRepository.getGroups(group -> group.hasStudent(student));
        if (groups == null || groups.isEmpty()) return null;
        Integer id = groups.get(0).getId();
        logger.trace(REPO_GET_GROUP_ID, student, id);
        return id;
    }

    public boolean addTeacher(Teacher teacher) {
        boolean added = teachersRepository.addTeacher(teacher);
        logger.trace(REPO_ADD_TEACHER, teacher.getId(), added);
        return added;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean removeStudentFromGroup(Student student, Integer groupId) {
        boolean removed = groupsRepository.removeStudentFromGroup(student, groupId);
        logger.trace(REPO_REMOVE_STUDENT_FROM_GROUP, student.getId(), groupId, removed);
        return removed;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean addStudentToGroup(Student student, Integer groupId) {
        boolean added = groupsRepository.addStudentToGroup(student, groupId);
        logger.trace(REPO_ADD_STUDENT_TO_GROUP, student.getId(), groupId, added);
        return added;
    }

    public List<ScheduleUnit> getSchedules(LocalDateTime begin, LocalDateTime end, List<Predicate<ScheduleUnit>> predicates) {
        List<ScheduleUnit> unitList = schedulesRepository.getSchedules(begin, end, predicates);
        logger.trace(REPO_GET_SCHEDULES, unitList.size());
        return unitList;
    }

    public boolean addSchedule(Schedule schedule) {
        boolean added = schedulesRepository.addSchedule(schedule);
        logger.trace(REPO_ADD_SCHEDULES, schedule.getDate());
        return added;
    }

    public boolean addScheduleUnit(ScheduleUnit unit) {
        boolean added = schedulesRepository.addSchedule(unit);
        logger.trace(REPO_ADD_SCHEDULES_UNIT, unit.getBegin());
        return added;
    }

    public Schedule getScheduleByUnit(ScheduleUnit unit) {
        Schedule schedule = schedulesRepository.getSheduleByUnit(unit);
        logger.trace(REPO_GET_SCHEDULE_BY_UNIT, schedule.getDate(), unit.getBegin());
        return schedule;
    }
}
