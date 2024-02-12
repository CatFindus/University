package com.example.model.vo;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.consts.LoggerConstants.POJO_CREATED;
import static com.example.consts.ModelConstants.*;

@Getter
@Setter
@EqualsAndHashCode
public class Teacher implements ModelUnit {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static AtomicInteger idCount;
    @Getter(AccessLevel.NONE)
    private final Logger logger = LoggerFactory.getLogger(Teacher.class);
    @Getter(AccessLevel.NONE)
    private final static Properties properties = new Properties();

    static {
        try (InputStream is = Objects.requireNonNull(Student.class.getClassLoader().getResource(MODEL_PROPERTY)).openStream()) {
            properties.load(is);
        } catch (IOException ignored) {
        }
        idCount = new AtomicInteger(Integer.parseInt(properties.getProperty(TEACHER_ID_COUNT, DEFAULT_TEACHER_ID_COUNT)));
    }

    private final Integer id;
    private String firstName;
    private String middleName;
    private String surName;
    private LocalDate birthDay;
    private LocalDate experienceBegin;
    private String phoneNumber;
    private Set<Subject> subjects;

    public Teacher(String firstName, String middleName, String surName, LocalDate birthDay, LocalDate experienceBegin, String phoneNumber) {
        this.id = idCount.getAndIncrement();
        this.firstName = firstName;
        this.surName = surName;
        this.middleName = middleName;
        this.birthDay = birthDay;
        this.experienceBegin = experienceBegin;
        this.phoneNumber = phoneNumber;
        this.subjects = new CopyOnWriteArraySet<>();
        logger.debug(POJO_CREATED, this);
    }

    public Teacher() {
        this.id = idCount.getAndIncrement();
        this.subjects = new CopyOnWriteArraySet<>();
        logger.debug(POJO_CREATED, this);
    }

    public boolean hasFirstName(String firstName) {
        if (firstName == null) return false;
        return this.firstName.equalsIgnoreCase(firstName);
    }

    public boolean hasMiddleName(String middleName) {
        if (middleName == null) return false;
        return this.middleName.equalsIgnoreCase(middleName);
    }

    public boolean hasSurName(String surName) {
        if (surName == null) return false;
        return this.surName.equalsIgnoreCase(surName);
    }

    public boolean hasPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return false;
        return this.phoneNumber.equals(phoneNumber);
    }

    public boolean hasBirthDay(LocalDate birthDay) {
        if (birthDay == null) return false;
        return this.birthDay.equals(birthDay);
    }

    public int getExperience() {
        if (experienceBegin == null) return 0;
        Period period = Period.between(this.experienceBegin, LocalDate.now());
        return period.getYears();
    }
}


