package com.example.controller.servlets;

import com.example.consts.ControlerConstants;
import com.example.exeptions.IncorrectRequestException;
import com.example.exeptions.NoDataException;
import com.example.mapper.StudentMapper;
import com.example.mapper.StudentMapperImpl;
import com.example.mapper.viewmapper.JsonMapper;
import com.example.mapper.viewmapper.ViewMapper;
import com.example.model.dto.Request.StudentRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.dto.Response.ErrorResponse;
import com.example.model.dto.Response.StudentResponse;
import com.example.model.service.Service;
import com.example.model.service.StudentService;
import com.example.model.vo.ModelUnit;
import com.example.model.vo.Student;
import com.example.view.JsonView;
import com.example.view.View;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.consts.ControlerConstants.INCORRECT_REQUEST_ARGS;
import static com.example.consts.ControlerConstants.WARN_MSG;
import static com.example.consts.LoggerConstants.*;
import static com.example.consts.ModelConstants.*;

@WebServlet(name = "StudentsServlet", urlPatterns = "/students/*")
public class StudentsServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(StudentsServlet.class);
    private transient View view;
    private transient Service service;
    private transient ViewMapper jsonMapper;
    private transient StudentMapper mapper;

    private void initialization(HttpServletResponse resp) {
        view = new JsonView(resp);
        jsonMapper = new JsonMapper();
        service = new StudentService();
        mapper = new StudentMapperImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        logger.trace(DO_GET_BEGIN);
        initialization(resp);
        try {
            doGetValidation(req);
            List<ModelUnit> students;
            if (req.getPathInfo() != null)
                students = service.getDataById(req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY));
            else students = service.getDataByParameters(req.getParameterMap());
            view.update(service.mappingVoToDto(students));
            logger.trace(DO_GET_END);
        } catch (IncorrectRequestException e) {
            ErrorResponse response = new ErrorResponse(e.getMessage());
            logger.warn(WARN_MSG, response.getErrorID(), e.getMessage());
            view.update(List.of(response));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        logger.trace(DO_POST_BEGIN);
        initialization(resp);
        try {
            doPostValidation(req);
            StudentRequest studentRequest;
            try {
                studentRequest = jsonMapper.getDtoFromRequest(StudentRequest.class, req.getReader());
            } catch (IOException e) {
                throw new IncorrectRequestException(INCORRECT_BODY_OF_REQUEST);
            }
            Student student = mapper.mapFromRequest(studentRequest);
            StudentResponse studentResponse = mapper.mapToResponse(student, null);
            if (service.create(student)) {
                resp.setStatus(201);
                view.update(List.of(studentResponse));
            }
            logger.trace(DO_POST_END);
        } catch (IncorrectRequestException e) {
            ErrorResponse response = new ErrorResponse(e.getMessage());
            logger.warn(WARN_MSG, response.getErrorID(), e.getMessage());
            view.update(List.of(response));
        }

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.trace(DO_PUT_BEGIN);
        initialization(resp);
        try {
            doPutDeleteValidation(req);
            String studentId = req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY);
            StudentRequest studentRequest = jsonMapper.getDtoFromRequest(StudentRequest.class, req.getReader());
            List<DtoResponse> response = service.update(studentId, studentRequest);
            view.update(response);
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

            doPutDeleteValidation(req);
            String studentId = req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY);
            service.delete(studentId);
            resp.setStatus(204);
            view.update(new ArrayList<>());
            logger.trace(DO_DELETE_END);
        } catch (IncorrectRequestException | NoDataException e) {
            ErrorResponse response = new ErrorResponse(e.getMessage());
            logger.warn(WARN_MSG, response.getErrorID(), e.getMessage());
            view.update(List.of(response));
        }
    }

    private void doGetValidation(HttpServletRequest req) throws IncorrectRequestException {
        logger.trace(START_VALIDATION);
        String path = req.getPathInfo();
        String query = req.getQueryString();
        try {
            if (path != null && query != null) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
            else if (path != null) Integer.parseInt(req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY));
            else if (query != null) queryValidation(req);
            else throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
            logger.trace(END_VALIDATION_SUCCESSFUL);
        } catch (NumberFormatException e) {
            logger.trace(END_VALIDATION_UNSUCCESSFUL,e.getMessage());
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        } catch (DateTimeParseException e) {
            logger.trace(END_VALIDATION_UNSUCCESSFUL,e.getMessage());
            throw new IncorrectRequestException(INCORRECT_DATE_FORMAT);
        }
    }


    private void queryValidation(HttpServletRequest req) throws IncorrectRequestException {
        logger.trace(START_VALIDATION);
        Map<String, String[]> mapQuery = req.getParameterMap();
        try {
            for (String key : mapQuery.keySet()) {
                String[] queryValue = mapQuery.get(key);
                if (queryValue == null || queryValue.length != 1)
                    throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
                if (!ControlerConstants.STUDENT_REQUEST_PARAMETERS.contains(key))
                    throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
                switch (key) {
                    case RQ_ID -> {
                        if (mapQuery.get(key).length != 1) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
                        else Integer.parseInt(mapQuery.get(key)[0]);
                    }
                    case RQ_BIRTHDAY -> {
                        if (mapQuery.get(key).length != 1) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
                        else LocalDate.parse(mapQuery.get(key)[0]);
                    }
                }
            }
        logger.trace(END_VALIDATION_SUCCESSFUL);
        } catch (NumberFormatException e) {
            logger.trace(END_VALIDATION_UNSUCCESSFUL,e.getMessage());
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        } catch (DateTimeParseException e) {
            logger.trace(END_VALIDATION_UNSUCCESSFUL,e.getMessage());
            throw new IncorrectRequestException(INCORRECT_DATE_FORMAT);
        }
    }


    private void doPutDeleteValidation(HttpServletRequest req) throws IncorrectRequestException {
        logger.trace(START_VALIDATION);
        if (req.getPathInfo() == null || req.getQueryString() != null)
            throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
        try {
            Integer.parseInt(req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY));
        } catch (NumberFormatException e) {
            logger.trace(END_VALIDATION_UNSUCCESSFUL,e.getMessage());
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }

    }

    private void doPostValidation(HttpServletRequest req) throws IncorrectRequestException {
        if (req.getPathInfo() != null || req.getQueryString() != null)
            throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
    }
}