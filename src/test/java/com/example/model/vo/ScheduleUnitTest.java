package com.example.model.vo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleUnitTest {
    static Student student1;
    static Student student2;
    static Group group1;
    static Group group2;
    static Teacher teacher1;
    static Teacher teacher2;
    static ScheduleUnit su1000_1031;
    static ScheduleUnit su1030_1100;
    static ScheduleUnit su1059_1130;
    static ScheduleUnit su1100_1130;
    static ScheduleUnit su1000_1030;
    static ScheduleUnit su1100_1130_2;

    @BeforeAll
    static void init() {
        student1 = new Student("student1fn", "student1mn", "studen1sn", LocalDate.now(), "+71234567890");
        student2 = new Student("student2fn", "student2mn", "studen2sn", LocalDate.now(), "+71234567890");
        teacher1 = new Teacher("Test1FN", "Test1MN", "Test1SN", LocalDate.of(2020, 1, 1), LocalDate.of(2000, 1, 1), "+71234567890");
        teacher2 = new Teacher("Test2FN", "Test2MN", "Test2SN", LocalDate.of(2020, 1, 1), LocalDate.of(2000, 1, 1), "+71234567890");
        group1 = new Group("group1");
        group2 = new Group("group2");
        group1.addStudent(student1);
        LocalDateTime ldt0 = LocalDateTime.parse("2023-10-10T10:00:00");
        LocalDateTime ldt1 = LocalDateTime.parse("2023-10-10T10:30:00");
        LocalDateTime ldt2 = LocalDateTime.parse("2023-10-10T10:31:00");
        LocalDateTime ldt3 = LocalDateTime.parse("2023-10-10T10:59:00");
        LocalDateTime ldt4 = LocalDateTime.parse("2023-10-10T11:00:00");
        LocalDateTime ldt5 = LocalDateTime.parse("2023-10-10T11:30:00");
        su1000_1031 = new ScheduleUnit(ldt0, ldt2, teacher1, group1, Subject.CULTURE_STUDIES);
        su1030_1100 = new ScheduleUnit(ldt1, ldt4, teacher1, group2, Subject.PHYSICS);
        su1059_1130 = new ScheduleUnit(ldt3, ldt5, teacher2, group2, Subject.HISTORY_RELIGION);
        su1100_1130 = new ScheduleUnit(ldt4, ldt5, teacher2, group2, Subject.HIGHTMATH);
        su1000_1030 = new ScheduleUnit(ldt0, ldt1, teacher1, group2, Subject.PROBABILITY_THEORY);
        su1100_1130_2 = new ScheduleUnit(ldt4, ldt5, teacher1, group1, Subject.CULTURE_STUDIES);
    }

    @Test
    void isIntersect() {
        assertTrue(su1000_1031.isIntersect(su1030_1100));
        assertTrue(su1059_1130.isIntersect(su1030_1100));
        assertFalse(su1000_1030.isIntersect(su1030_1100));
        assertFalse(su1030_1100.isIntersect(su1100_1130));
    }

    @Test
    void compareTo() {
        assertEquals(0, su1000_1031.compareTo(su1030_1100));
        assertEquals(1, su1000_1030.compareTo(su1030_1100));
        assertEquals(1, su1100_1130.compareTo(su1100_1130_2));
    }

    @Test
    void isTimeContainInGap() {
        LocalDateTime begin = LocalDateTime.parse("2023-10-10T10:30:00");
        LocalDateTime end = LocalDateTime.parse("2023-10-10T11:00:00");
        assertTrue(su1030_1100.isTimeContainInGap(begin, end));
        assertFalse(su1059_1130.isTimeContainInGap(begin, end));
        assertFalse(su1000_1031.isTimeContainInGap(begin, end));
    }

    @Test
    void hasSubject() {
        assertTrue(su1000_1031.hasSubject("culturestudies"));
        assertFalse(su1000_1031.hasSubject("hightmath"));
    }

    @Test
    void hasTeacher() {
        assertTrue(su1000_1031.hasTeacher(teacher1.getId()));
        assertFalse(su1000_1031.hasTeacher(teacher2.getId()));
    }

    @Test
    void hasGroup() {
        assertTrue(su1000_1031.hasGroup(group1.getId()));
        assertFalse(su1000_1031.hasGroup(group2.getId()));
    }

    @Test
    void hasStudent() {
        assertTrue(su1000_1031.hasStudent(student1.getId()));
        assertFalse(su1000_1031.hasStudent(student2.getId()));
    }
    @Test
    void  equalsAndHashCode() {
        LocalDateTime time = LocalDateTime.now();
        Teacher teacher = new Teacher();
        Group group = new Group();
        ScheduleUnit scheduleUnit1 = new ScheduleUnit(time,time, teacher, group, null);
        ScheduleUnit scheduleUnit2 = new ScheduleUnit(time,time, teacher, group, null);
        assertEquals(scheduleUnit1, scheduleUnit2);
        assertEquals(scheduleUnit1.hashCode(), scheduleUnit2.hashCode());
    }
}