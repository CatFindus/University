package com.example.mapper.viewmapper;

import com.example.exeptions.IncorrectRequestException;
import com.example.model.dto.Request.DtoRequest;

import java.io.Reader;

public interface ViewMapper {
    <T extends DtoRequest> T getDtoFromRequest(Class<T> clazz, Reader reader) throws IncorrectRequestException;
}
