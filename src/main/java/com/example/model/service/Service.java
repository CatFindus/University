package com.example.model.service;

import com.example.exeptions.IncorrectRequestException;
import com.example.exeptions.NoDataException;
import com.example.model.dto.Request.DtoRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.entities.ModelUnit;

import java.util.List;
import java.util.Map;

public interface Service {
    DtoResponse getDataById(String idString) throws IncorrectRequestException, NoDataException;

    List<DtoResponse> getDataByParameters(Map<String, String[]> parameterMap) throws IncorrectRequestException;

    List<DtoResponse> mappingVoToDto(List<ModelUnit> modelUnitList);
    DtoResponse mappingVoToDto(ModelUnit modelUnit);

    //create, add elements
    DtoResponse create(String path, DtoRequest request) throws NoDataException, IncorrectRequestException;

    DtoResponse update(String path, DtoRequest request) throws IncorrectRequestException, NoDataException;
    //remove, replace elements
    DtoResponse delete(String path, DtoRequest request) throws IncorrectRequestException, NoDataException;
}
