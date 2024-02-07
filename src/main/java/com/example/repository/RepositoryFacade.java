package com.example.repository;

import com.example.model.vo.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

public class RepositoryFacade {
   private final StudentsRepository studentsRepository;
   private final TeachersRepository teachersRepository;
   private final SchedulesRepository schedulesRepository;
   private final GroupsRepository groupsRepository;

   public RepositoryFacade() {
      studentsRepository=StudentsRepository.getInstance();
      teachersRepository=TeachersRepository.getInstance();
      schedulesRepository = SchedulesRepository.getInstance();
      groupsRepository=GroupsRepository.getInstance();
   }

   public Student getStudent(int id) {
      return studentsRepository.getStudentById(id);
   }
   public List<Student> getStudents(List<Predicate<Student>> predicates) {
       return studentsRepository.getStudents(predicates);
   }
   public List<Student> getStudents(Group group, Predicate<Student> predicate) {
      return group.getStudents().stream().filter(predicate).toList();
   }
   public boolean addStudent(Student student) {
      return studentsRepository.addStudent(student);
   }
   public boolean removeStudent(Integer id) { return studentsRepository.removeStudent(id); }
   public Group getGroup(int id) {
      return groupsRepository.getGroupById(id);
   }
   public List<Group> getGroups(Predicate<Group> predicate) {
      return groupsRepository.getGroups(predicate);
   }
   public List<Group> getGroups(List<Predicate<Group>> predicates) { return groupsRepository.getGroups(predicates); }
   public boolean addGroup(Group group) { return groupsRepository.addGroup(group); }
   public boolean removeGroup(Group group) { return groupsRepository.removeGroup(group); }
   public boolean removeGroup(Integer id) { return groupsRepository.removeGroup(id); }
   public Teacher getTeacher(Integer id) {
      return teachersRepository.getTeacherById(id);
   }
   public List<Teacher> getTeachers(Predicate<Teacher> predicate) {
      return teachersRepository.getTeachers(predicate);
   }
   public List<Teacher> getTeachers(List<Predicate<Teacher>> predicates) {
      return teachersRepository.getTeachers(predicates);
   }
   public boolean removeTeacher(Integer id) { return teachersRepository.removeTeacher(id); }
   public Integer getGroupIdByStudent(Student student) {
      List<Group> groups = groupsRepository.getGroups(group -> group.hasStudent(student));
      if(groups==null || groups.isEmpty()) return null;
      return groups.get(0).getId();
   }
   public boolean addTeacher(Teacher teacher) {
      return teachersRepository.addTeacher(teacher);
   }
   public boolean removeStudentFromGroup(Student student, Integer groupId) {
      return groupsRepository.removeStudentFromGroup(student,groupId);
   }
   public boolean addStudentToGroup(Student student, Integer groupId) {
      return groupsRepository.addStudentToGroup(student, groupId);
   }
   public List<ScheduleUnit> getSchedules(LocalDateTime begin, LocalDateTime end, List<Predicate<ScheduleUnit>> predicates) {
      return schedulesRepository.getSchedules(begin,end,predicates);
   }
   public boolean addSchedule(Schedule schedule) {
      return schedulesRepository.addSchedule(schedule);
   }
   public boolean addScheduleUnit(ScheduleUnit unit) {
      return schedulesRepository.addSchedule(unit);
   }
   public Schedule getScheduleByUnit(ScheduleUnit unit) {
      return schedulesRepository.getSheduleByUnit(unit);
   }
}
