package com.example.controller.servlets;

import com.example.exeptions.IncorrectRequestException;
import com.example.exeptions.NoDataException;
import com.example.mapper.ScheduleMapper;
import com.example.mapper.ScheduleMapperImpl;
import com.example.mapper.viewmapper.JsonMapper;
import com.example.mapper.viewmapper.ViewMapper;
import com.example.model.dto.Request.ScheduleUnitRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.dto.Response.ErrorResponse;
import com.example.model.service.ScheduleService;
import com.example.model.vo.ModelUnit;
import com.example.model.vo.ScheduleUnit;
import com.example.model.vo.Subject;
import com.example.view.JsonView;
import com.example.view.View;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.consts.ControlerConstants.INCORRECT_REQUEST_ARGS;
import static com.example.consts.ControlerConstants.WARN_MSG;
import static com.example.consts.LoggerConstants.*;
import static com.example.consts.ModelConstants.*;

@WebServlet(name = "ScheduleServlet", urlPatterns = "/schedules/*")
public class SchedulesServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(TeachersServlet.class);
    private transient View view;
    private transient ScheduleService service;
    private transient ViewMapper jsonMapper;
    private transient ScheduleMapper mapper;

    private void initialization(HttpServletResponse resp) {
        view = new JsonView(resp);
        jsonMapper = new JsonMapper();
        service = new ScheduleService();
        mapper = new ScheduleMapperImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        logger.trace(DO_GET_BEGIN);
        initialization(resp);
        try {
            doGetRequestValidation(req);
            List<ModelUnit> units = service.getDataByParameters(req.getParameterMap());
            if (units.isEmpty()) view.update(new ArrayList<>());
            else {
                view.update(service.mappingVoToDto(units));
            }
            logger.trace(DO_GET_END);
        } catch (IncorrectRequestException e) {
            ErrorResponse response = new ErrorResponse(e.getMessage());
            logger.warn(WARN_MSG, response.getErrorID(), e.getMessage());
            view.update(List.of(response));
        }
    }

    private void doGetRequestValidation(HttpServletRequest req) throws IncorrectRequestException {
        logger.trace(START_VALIDATION);
        Map<String, String[]> parameterMap = req.getParameterMap();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PARAM_PATTERN);
        if (req.getPathInfo() != null || parameterMap.size() < 2)
            throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
        try {
            LocalDateTime begin = null, end = null;
            for (String key : parameterMap.keySet()) {
                switch (key) {
                    case RQ_BEGIN_DATE_TIME ->
                            begin = LocalDateTime.parse(parameterMap.get(RQ_BEGIN_DATE_TIME)[0], formatter);
                    case RQ_END_DATE_TIME ->
                            end = LocalDateTime.parse(parameterMap.get(RQ_END_DATE_TIME)[0], formatter);
                    case RQ_SUBJECT -> subjectValidation(parameterMap);
                    case RQ_STUDENT_ID -> studentIdValidation(parameterMap);
                    case RQ_GROUP_ID -> groupIdValidation(parameterMap);
                    case RQ_TEACHER_ID -> teacherIdValidation(parameterMap);
                    default -> throw new IncorrectRequestException();
                }
            }
            if (begin == null || end == null || begin.isAfter(end))
                throw new IncorrectRequestException(INCORRECT_DATE_FORMAT);
            logger.trace(END_VALIDATION_SUCCESSFUL);
        } catch (DateTimeParseException e) {
            logger.trace(END_VALIDATION_UNSUCCESSFUL, e.getMessage());
            throw new IncorrectRequestException(INCORRECT_DATE_FORMAT);
        } catch (NumberFormatException e) {
            logger.trace(END_VALIDATION_UNSUCCESSFUL, e.getMessage());
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }

    }

    private void teacherIdValidation(Map<String, String[]> parameterMap) throws IncorrectRequestException {
        logger.trace(START_VALIDATION);
        if (parameterMap.get(RQ_TEACHER_ID).length != 1) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
        try {
            Integer.parseInt(parameterMap.get(RQ_TEACHER_ID)[0]);
            logger.trace(END_VALIDATION_SUCCESSFUL);
        } catch (NumberFormatException e) {
            logger.trace(END_VALIDATION_UNSUCCESSFUL, e.getMessage());
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }

    }

    private void groupIdValidation(Map<String, String[]> parameterMap) throws IncorrectRequestException {
        logger.trace(START_VALIDATION);
        if (parameterMap.get(RQ_GROUP_ID).length != 1) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
        try {
            Integer.parseInt(parameterMap.get(RQ_GROUP_ID)[0]);
            logger.trace(END_VALIDATION_SUCCESSFUL);
        } catch (NumberFormatException e) {
            logger.trace(END_VALIDATION_UNSUCCESSFUL, e.getMessage());
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }
    }

    private void studentIdValidation(Map<String, String[]> parameterMap) throws IncorrectRequestException {
        logger.trace(START_VALIDATION);
        if (parameterMap.get(RQ_STUDENT_ID).length != 1) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
        try {
            Integer.parseInt(parameterMap.get(RQ_STUDENT_ID)[0]);
            logger.trace(END_VALIDATION_SUCCESSFUL);
        } catch (NumberFormatException e) {
            logger.trace(END_VALIDATION_UNSUCCESSFUL, e.getMessage());
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }

    }

    private void subjectValidation(Map<String, String[]> parameterMap) throws IncorrectRequestException {
        logger.trace(START_VALIDATION);
        for (String value : parameterMap.get(RQ_SUBJECT))
            if (!Subject.containRequestName(value)) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
        logger.trace(END_VALIDATION_SUCCESSFUL);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.trace(DO_POST_BEGIN);
        initialization(resp);
        try {
            doPostRequestValidation(req);
            ScheduleUnitRequest request = jsonMapper.getDtoFromRequest(ScheduleUnitRequest.class, req.getReader());
            ScheduleUnit unit = mapper.mapDtoToScheduleUnit(request);
            if (unit != null && service.create(unit)) {
                resp.setStatus(201);
                view.update(List.of(mapper.mapScheduleUnitToDto(unit)));
            } else {
                resp.setStatus(200);
                view.update(new ArrayList<>());

            }
            logger.trace(DO_POST_END);
        } catch (IncorrectRequestException e) {
            ErrorResponse response = new ErrorResponse(e.getMessage());
            logger.warn(WARN_MSG, response.getErrorID(), e.getMessage());
            view.update(List.of(response));
        }
    }

    private void doPostRequestValidation(HttpServletRequest req) throws IncorrectRequestException {
        logger.trace(START_VALIDATION);
        if (req.getPathInfo() != null || req.getQueryString() != null)
            throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
        logger.trace(END_VALIDATION_SUCCESSFUL);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.trace(DO_PUT_BEGIN);
        initialization(resp);
        try {
            doGetRequestValidation(req);
            ScheduleUnitRequest request = jsonMapper.getDtoFromRequest(ScheduleUnitRequest.class, req.getReader());
            List<DtoResponse> responses = service.update(req.getParameterMap(), request);
            view.update(responses);
            logger.trace(DO_PUT_END);
        } catch (IncorrectRequestException | NoDataException e) {
            ErrorResponse response = new ErrorResponse(e.getMessage());
            logger.warn(WARN_MSG, response.getErrorID(), e.getMessage());
            view.update(List.of(response));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        logger.trace(DO_DELETE_BEGIN);
        initialization(resp);
        try {
            doGetRequestValidation(req);
            service.delete(req.getParameterMap());
            resp.setStatus(204);
            view.update(new ArrayList<>());
            logger.trace(DO_DELETE_END);
        } catch (IncorrectRequestException | NoDataException e) {
            ErrorResponse response = new ErrorResponse(e.getMessage());
            logger.warn(WARN_MSG, response.getErrorID(), e.getMessage());
            view.update(List.of(response));
        }
    }
}
