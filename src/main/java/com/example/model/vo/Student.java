package com.example.model.vo;

import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@EqualsAndHashCode
public class Student implements ModelUnit {
    private static AtomicInteger idCount;
    private final Logger logger = LoggerFactory.getLogger(Student.class);
    private static Properties properties = new Properties();
    static {
        try(InputStream is = Objects.requireNonNull(Student.class.getClassLoader().getResource("model.properties")).openStream()) {
            properties.load(is);
        } catch (IOException ignored) {}
        idCount = new AtomicInteger(Integer.parseInt(properties.getProperty("STUDENT_ID_COUNT", "1")));
    }
    private final Integer id;
    private String firstName;
    private String middleName;
    private String surName;
    private LocalDate birthDay;
    private String phoneNumber;

    public Student(String firstName, String middleName, String surName, LocalDate birthDay, String phoneNumber) {
        this.id = idCount.getAndIncrement();
        this.firstName = firstName;
        this.middleName = middleName;
        this.surName = surName;
        this.birthDay = birthDay;
        this.phoneNumber = phoneNumber;
    }
    public Student() {
        this.id = idCount.getAndIncrement();
    }
    public boolean hasFirstName(String firstName) {
        if(firstName==null) return false;
        return this.firstName.equalsIgnoreCase(firstName);
    }
    public boolean hasMiddleName(String middleName) {
        if(middleName==null) return false;
        return this.middleName.equalsIgnoreCase(middleName);
    }
    public boolean hasSurName(String surName) {
        if(surName==null) return false;
        return this.surName.equalsIgnoreCase(surName);
    }
    public boolean hasPhoneNumber(String phoneNumber) {
        if(phoneNumber==null) return false;
        return this.phoneNumber.equals(phoneNumber);
    }
    public boolean hasBirthDay(LocalDate birthDay) {
        if(birthDay==null) return false;
        return this.birthDay.equals(birthDay);
    }
    public boolean hasId(Integer id) {
        return this.id.equals(id);
    }
}
