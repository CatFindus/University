package com.example.model.dto.Request;

import com.example.consts.LoggerConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

@Data
public class StudentRequest implements DtoRequest {
    private final static Logger logger = LoggerFactory.getLogger(StudentRequest.class);
    private String firstName;
    private String middleName;
    private String surName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate birthDay;
    private String phoneNumber;

    public StudentRequest(String firstName, String middleName, String surName, LocalDate birthDay, String phoneNumber) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.surName = surName;
        this.birthDay = birthDay;
        this.phoneNumber = phoneNumber;
        logger.debug(LoggerConstants.POJO_CREATED, this);
    }
}
