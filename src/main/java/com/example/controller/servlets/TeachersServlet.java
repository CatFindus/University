package com.example.controller.servlets;

import com.example.controller.ServiceFactory;
import com.example.exeptions.IncorrectRequestException;
import com.example.exeptions.NoDataException;
import com.example.mapper.viewmapper.JsonMapper;
import com.example.mapper.viewmapper.ViewMapper;
import com.example.model.dto.Request.TeacherRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.dto.Response.ErrorResponse;
import com.example.model.dto.Response.NoDataResponse;
import com.example.model.service.Service;
import com.example.model.service.TeacherService;
import com.example.validators.parameters.NameValidator;
import com.example.validators.parameters.PhoneValidator;
import com.example.validators.requests.TeachersValidator;
import com.example.view.JsonView;
import com.example.view.View;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.consts.ControlerConstants.*;
import static com.example.consts.LoggerConstants.*;
import static com.example.consts.ModelConstants.*;

@WebServlet(name = "TeachersServlet", urlPatterns = "/teachers/*")
public class TeachersServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(TeachersServlet.class);
    private transient View view;
    private transient Service service;
    private transient ViewMapper jsonMapper;

    private void initialization(HttpServletResponse resp) {
        view = new JsonView(resp);
        jsonMapper = new JsonMapper();
        service = ServiceFactory.getService(TeacherService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        logger.trace(DO_GET_BEGIN);
        initialization(resp);
        try {
            new TeachersValidator(req).validate();
            List<DtoResponse> teachers = new ArrayList<>();
            if (req.getPathInfo() != null) {
                DtoResponse response = service.getDataById(req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY));
                if (!(response instanceof NoDataResponse)) teachers.add(response);
            } else teachers = service.getDataByParameters(req.getParameterMap());
            view.update(teachers);
            logger.trace(DO_GET_END);
        } catch (IncorrectRequestException | NoDataException e) {
            ErrorResponse response = new ErrorResponse(e.getMessage());
            logger.warn(WARN_MSG, response.getErrorID(), e.getMessage());
            view.update(List.of(response));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.trace(DO_POST_BEGIN);
        initialization(resp);
        try {
            new TeachersValidator(req).validate();
            TeacherRequest request = null;
            if (req.getPathInfo()!=null) {
                String[] strings = req.getParameterMap().get(RQ_SUBJECT);
                String subject = strings!=null ? strings[0] : null;
                request = new TeacherRequest();
                request.setRqSubject(subject);
            } else {
                request = jsonMapper.getDtoFromRequest(TeacherRequest.class, req.getReader());
                new NameValidator(request.getFirstName(), request.getMiddleName(), request.getSurName()).
                        then(new PhoneValidator(request.getPhoneNumber())).validate();
            }
            String path = req.getPathInfo()==null ? null : req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY);
            DtoResponse response = service.create(path, request);
            view.update(List.of(response));
            logger.trace(DO_POST_END);
        } catch (IncorrectRequestException | NoDataException e) {
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
            new TeachersValidator(req).validate();
            String stringId = req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY);
            TeacherRequest teacherRequest = jsonMapper.getDtoFromRequest(TeacherRequest.class, req.getReader());
            DtoResponse response = service.update(stringId, teacherRequest);
            view.update(List.of(response));
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
            new TeachersValidator(req).validate();
            TeacherRequest request = null;
            if (req.getQueryString()!=null) {
                request = new TeacherRequest();
                String[] strings = req.getParameterMap().get(RQ_SUBJECT);
                String subject = strings!=null ? strings[0] : null;
                request.setRqSubject(subject);
            }
            DtoResponse response = service.delete(req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY), request);
            List<DtoResponse> responses = new ArrayList<>();
            if (!(response instanceof  NoDataResponse)) {
                responses.add(response);
            } else resp.setStatus(204);
            view.update(responses);
            logger.trace(DO_DELETE_END);
        } catch (IncorrectRequestException | NoDataException e) {
            ErrorResponse response = new ErrorResponse(e.getMessage());
            logger.warn(WARN_MSG, response.getErrorID(), e.getMessage());
            view.update(List.of(response));
        }
    }
}
