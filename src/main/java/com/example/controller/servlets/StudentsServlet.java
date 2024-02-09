package com.example.controller.servlets;

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
import com.example.validators.requests.StudentsValidator;
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
            new StudentsValidator(req).validate();
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
            new StudentsValidator(req).validate();
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
            new StudentsValidator(req).validate();
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

            new StudentsValidator(req).validate();
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


}