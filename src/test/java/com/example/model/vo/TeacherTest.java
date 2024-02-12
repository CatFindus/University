package com.example.model.vo;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TeacherTest {
    Teacher t = new Teacher("TestFN", "TestMN", "TestSN", LocalDate.of(2020, 1, 1), LocalDate.of(2000, 1, 1), "+71234567890");

    @Test
    void hasFirstName() {

        assertTrue(t.hasFirstName("TestFN"));
        assertFalse(t.hasFirstName("badFN"));
    }

    @Test
    void hasMiddleName() {
        assertTrue(t.hasMiddleName("TestMN"));
        assertFalse(t.hasMiddleName("badFN"));
    }

    @Test
    void hasSurName() {
        assertTrue(t.hasSurName("TestSN"));
        assertFalse(t.hasSurName("BadSN"));
    }

    @Test
    void hasPhoneNumber() {
        assertTrue(t.hasPhoneNumber("+71234567890"));
        assertFalse(t.hasPhoneNumber("+7123456789"));
    }

    @Test
    void hasBirthDay() {
        assertTrue(t.hasBirthDay(LocalDate.of(2020, 1, 1)));
        assertFalse(t.hasBirthDay(LocalDate.of(2000, 1, 1)));
    }

    @Test
    void getExperience() {

        assertEquals(LocalDate.now().getYear() - 2000, t.getExperience());
    }
}