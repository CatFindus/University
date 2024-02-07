package com.example.mapper.viewmapper;

import com.example.exeptions.IncorrectRequestException;
import com.example.mapper.viewmapper.ViewMapper;
import com.example.model.dto.Request.DtoRequest;
import com.example.model.dto.Response.DtoResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class JsonMapper implements ViewMapper {
    private ObjectMapper mapper;

    public JsonMapper() {
        mapper=new ObjectMapper();
    }
    @Override
    public  <T extends DtoRequest> T getDtoFromRequest(Class<T> clazz, Reader reader) throws IncorrectRequestException {
        T result;
        try(BufferedReader br = new BufferedReader(reader)) {
            StringBuilder json = new StringBuilder();
            while (br.ready()) {
                json.append(br.readLine());
            }
            result = mapper.readValue(json.toString(), clazz);
        } catch (IOException e) {
            throw new IncorrectRequestException();
        }
        return result;
    }
    public String getStringFromResponse(List<DtoResponse> response) {
        String result;
        try {
            result = mapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
