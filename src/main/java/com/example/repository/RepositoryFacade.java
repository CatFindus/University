package com.example.repository;

import com.example.model.entities.*;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.consts.LoggerConstants.*;

public class RepositoryFacade {
    private final static Logger logger = LoggerFactory.getLogger(RepositoryFacade.class);
    private final StudentsRepository studentsRepository;
    private final TeachersRepository teachersRepository;
    private final SchedulesRepository schedulesRepository;
    private final GroupsRepository groupsRepository;
    private final SubjectsRepository subjectsRepository;

    public RepositoryFacade(Session session) {
        studentsRepository = new StudentsRepository(session);
        teachersRepository = new TeachersRepository(session);
        schedulesRepository = new SchedulesRepository(session);
        groupsRepository = new GroupsRepository(session);
        subjectsRepository = new SubjectsRepository(session);
    }

    public Optional<Student> getStudent(int id) {
        Optional<Student> student = studentsRepository.getStudentById(id);
        logger.trace(REPO_GET_STUDENT_BY_ID, id, student.isPresent());
        return student;
    }

    public List<Student> getStudents(Map<String, Object> parameterMap, int limit, int offset) {
        List<Student> students = studentsRepository.getStudents(parameterMap, limit, offset);
        logger.trace(REPO_GET_STUDENT_BY_PREDICATES, parameterMap.size(), students.stream().map(Student::getId).toList());
        return students;
    }

    public Student update(Student student) {
        return studentsRepository.update(student);
    }

    public void addStudent(Student student) {
        studentsRepository.add(student);
        logger.trace(REPO_ADD_STUDENT, student);

    }

    public boolean removeStudent(Integer id) {
        boolean removed = studentsRepository.remove(id);
        logger.trace(REPO_REMOVE_STUDENT, id, removed);
        return removed;
    }

    public Optional<Group> getGroup(int id) {
        Optional<Group> group = groupsRepository.getGroupById(id);
        logger.trace(REPO_GET_GROUP, id, group.isPresent());
        return group;
    }

    public List<Group> getGroups(Map<String, Object> parameterMap, int limit, int offset) {
        List<Group> groups = groupsRepository.getGroups(parameterMap, limit, offset);
        logger.trace(REPO_GET_GROUPS, parameterMap.size(), groups.size());
        return groups;
    }

    public void addGroup(Group group) {
        groupsRepository.add(group);
        logger.trace(REPO_ADD_GROUP, group);
    }

    public Group update(Group group) {
        return groupsRepository.update(group);
    }

    public boolean removeGroup(Integer id) {
        boolean removed = groupsRepository.remove(id);
        logger.trace(REPO_REMOVE_GROUP, id, removed);
        return removed;
    }

    public Teacher update(Teacher teacher) { return teachersRepository.update(teacher); }

    public Optional<Teacher> getTeacher(Integer id) {
        Optional<Teacher> teacher = teachersRepository.getById(id);
        logger.trace(REPO_GET_TEACHER, id, teacher);
        return teacher;
    }

    public List<Teacher> getTeachers(Map<String, Object> parameterMap, int limit, int offset) {
        List<Teacher> teachers = teachersRepository.getTeachers(parameterMap, limit, offset);
        logger.trace(REPO_GET_TEACHERS_BY_PREDICATES, parameterMap.size(), teachers.stream().map(Teacher::getId).toList());
        return teachers;
    }

    public boolean removeTeacher(Integer id) {
        boolean removed = teachersRepository.removeTeacher(id);
        logger.trace(REPO_REMOVE_TEACHER, id, removed);
        return removed;
    }

    public boolean addSubjectToTeacher(Subject subject, Integer teacherId) {
        return teachersRepository.addSubjectToTeacher(subject, teacherId);
    }

    public boolean removeSubjectFromTeacher(Subject subject, Integer teacherId) {
        return teachersRepository.removeSubjectFromTeacher(subject, teacherId);
    }

    public Integer getGroupIdByStudent(int studentId) {
        Optional<Student> mayBeStudent = studentsRepository.getStudentById(studentId);
        if (mayBeStudent.isPresent() && mayBeStudent.get().getGroup()!=null) {
            Student student = mayBeStudent.get();
            logger.trace(REPO_GET_GROUP_ID, student, student.getGroup());
            return student.getGroup().getId();
        }
        else return null;
    }

    public void addTeacher(Teacher teacher) {
        teachersRepository.addTeacher(teacher);
        logger.trace(REPO_ADD_TEACHER, teacher.getId());
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

    public List<ScheduleUnit> getSchedules(LocalDateTime begin, LocalDateTime end) {
        List<ScheduleUnit> unitList = schedulesRepository.getSchedules(begin, end);
        logger.trace(REPO_GET_SCHEDULES, unitList.size());
        return unitList;
    }

    public List<ScheduleUnit> getSchedules(Map<String,Object> parameterMap, int limit, int offset) {
        List<ScheduleUnit> unitList = schedulesRepository.getSchedules(parameterMap, limit, offset);
        logger.trace(REPO_GET_SCHEDULES, unitList.size());
        return unitList;
    }

    public void addSchedule(ScheduleUnit scheduleUnit) {
        schedulesRepository.addSchedule(scheduleUnit);
        logger.trace(REPO_ADD_SCHEDULES, scheduleUnit.getBegin().toLocalDate());
    }

    public ScheduleUnit update(ScheduleUnit unit) {
        return schedulesRepository.update(unit);
    }

    public boolean deleteSchedule(Long id) {
        boolean deleted = schedulesRepository.deleteSchedule(id);
        logger.trace(REPO_DELETE_SCHEDULES, id, deleted);
        return deleted;
    }

    public Optional<ScheduleUnit> getScheduleById(Long Id) {
        Optional<ScheduleUnit> mayBeSchedule = schedulesRepository.getById(Id);
        logger.trace(REPO_GET_SCHEDULE_BY_UNIT, mayBeSchedule.isPresent());
        return mayBeSchedule;
    }

    public List<ScheduleUnit> getAllSchedules() {
        return schedulesRepository.getAll();

    }

    public Optional<Subject> getSubjectById(Integer id) {
        return subjectsRepository.getById(id);
    }

    public Optional<Subject> getSubjectByRequestName(String requestName) {
        return subjectsRepository.getSubjectByRequestName(requestName);
    }

    public List<Subject> getAllSubjects() {
        return subjectsRepository.getAll();
    }

    public void addSubject(Subject subject) {
        subjectsRepository.add(subject);
    }

    public boolean removeSubject(Integer id) {
        return subjectsRepository.removeSubject(id);
    }

    public Subject update(Subject subject) {
        return subjectsRepository.updateSubject(subject);
    }

    public List<Subject> getSubjects(Map<String, Object> requestMap, int limit, int offset) {
        return subjectsRepository.getSubjects(requestMap, limit, offset);
    }

    public Integer getStudentsCountByGroup(Integer groupId) {
        return studentsRepository.getStudentsCountByGroup(groupId);
    }
    public Integer getSchedulesCountByGroupPerDay(Integer groupId, LocalDate date) {
        return schedulesRepository.getSchedulesCountByGroupPerDay(groupId, date);
    }
}
