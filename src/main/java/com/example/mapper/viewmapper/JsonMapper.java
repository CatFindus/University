package com.example.mapper.viewmapper;

import com.example.exeptions.IncorrectRequestException;
import com.example.model.dto.Request.DtoRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.dto.Response.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import static com.example.consts.LoggerConstants.LOGGER_ERROR_JSON_STRING;
import static com.example.consts.ModelConstants.ERROR_JSON_STRING;
import static com.example.consts.ModelConstants.INCORRECT_BODY_OF_REQUEST;

public class JsonMapper implements ViewMapper {
    private final static Logger logger = LoggerFactory.getLogger(JsonMapper.class);
    private final ObjectMapper mapper;

    public JsonMapper() {
        mapper = new ObjectMapper();
    }

    @Override
    public <T extends DtoRequest> T getDtoFromRequest(Class<T> clazz, Reader reader) throws IncorrectRequestException {
        T result;
        try (BufferedReader br = new BufferedReader(reader)) {
            StringBuilder json = new StringBuilder();
            while (br.ready()) {
                json.append(br.readLine());
            }
            result = mapper.readValue(json.toString(), clazz);
        } catch (IOException e) {
            throw new IncorrectRequestException(INCORRECT_BODY_OF_REQUEST);
        }
        return result;
    }

    public String getStringFromResponse(List<DtoResponse> response) {
        String result;
        try {
            result = mapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            ErrorResponse errResp = new ErrorResponse(ERROR_JSON_STRING);
            logger.error(LOGGER_ERROR_JSON_STRING, errResp.getErrorID());
            return List.of(errResp).toString();
        }
        return result;
    }
}
