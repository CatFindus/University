package com.example.view;

import com.example.model.dto.Response.DtoResponse;

import java.util.List;

public interface View {
    void update(List<DtoResponse> units);
}
