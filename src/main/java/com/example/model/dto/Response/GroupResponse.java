package com.example.model.dto.Response;

import lombok.Data;

import java.util.List;

@Data
public class GroupResponse implements DtoResponse {
    private final Integer id;
    private String number;
    private final List<DtoResponse> students;
}
