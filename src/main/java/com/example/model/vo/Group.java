package com.example.model.vo;

import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.consts.LoggerConstants.*;
import static com.example.consts.ModelConstants.*;

@Data
public class Group implements ModelUnit {
    private static AtomicInteger idCount;
    private final Logger logger = LoggerFactory.getLogger(Group.class);
    private static Properties properties = new Properties();

    static {
        try (InputStream is = Objects.requireNonNull(Student.class.getClassLoader().getResource(MODEL_PROPERTY)).openStream()) {
            properties.load(is);
        } catch (IOException ignored) {
        }
        idCount = new AtomicInteger(Integer.parseInt(properties.getProperty(GROUP_ID_COUNT, DEFAULT_GROUP_ID_COUNT)));
    }

    private final Integer id;
    private String number;
    private final CopyOnWriteArrayList<Student> students;

    public Group(String number) {
        this.id = idCount.getAndIncrement();
        this.number = number;
        students = new CopyOnWriteArrayList<>();
        logger.debug(POJO_CREATED, this);
    }

    public Group() {
        this.id = idCount.getAndIncrement();
        students = new CopyOnWriteArrayList<>();
        logger.debug(POJO_CREATED, this);
    }

    public boolean hasNumber(String groupNumber) {
        if (groupNumber == null) return false;
        return this.number.equalsIgnoreCase(groupNumber);
    }

    public boolean hasStudent(Student student) {
        if (student == null) return false;
        return students.contains(student);
    }

    public boolean hasStudent(Integer id) {
        if (id == null) return false;
        return students.stream().anyMatch(s -> s.hasId(id));
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean addStudent(Student student) {
        boolean added = students.addIfAbsent(student);
        logger.trace(ADDING_TO_OBJECT, added ? SUCCESS : UNSUCCESSFUL);
        return added;
    }
}
