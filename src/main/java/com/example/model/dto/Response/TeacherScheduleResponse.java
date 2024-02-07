package com.example.model.dto.Response;

import lombok.Data;

@Data
public class TeacherScheduleResponse implements DtoResponse{
    private Integer id;
    private String firstName;
    private String middleName;
    private String surName;
}
