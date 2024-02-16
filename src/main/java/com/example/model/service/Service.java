package com.example.model.service;

import com.example.exeptions.IncorrectRequestException;
import com.example.exeptions.NoDataException;
import com.example.model.dto.Request.DtoRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.vo.ModelUnit;

import java.util.List;
import java.util.Map;

public interface Service {
    List<ModelUnit> getDataById(String idString) throws IncorrectRequestException;

    List<ModelUnit> getDataByParameters(Map<String, String[]> parameterMap) throws IncorrectRequestException;

    List<DtoResponse> mappingVoToDto(List<ModelUnit> modelUnitList);

    boolean create(ModelUnit modelUnit);

    List<DtoResponse> update(String id, DtoRequest dtoRequest) throws IncorrectRequestException, NoDataException;

    void delete(String path) throws IncorrectRequestException, NoDataException;
}
