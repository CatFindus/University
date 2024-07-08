package com.example.controller.servlets;

import com.example.controller.ServiceFactory;
import com.example.exeptions.IncorrectRequestException;
import com.example.exeptions.NoDataException;
import com.example.mapper.viewmapper.JsonMapper;
import com.example.mapper.viewmapper.ViewMapper;
import com.example.model.dto.Request.SubjectRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.dto.Response.ErrorResponse;
import com.example.model.service.Service;
import com.example.model.service.SubjectService;
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

@WebServlet(name = "SubjectsServlet", urlPatterns = "/subjects/*")
public class SubjectServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(StudentsServlet.class);
    private View view;
    private Service service;
    private ViewMapper jsonMapper;

    private void initialization(HttpServletResponse resp) {
        view = new JsonView(resp);
        jsonMapper = new JsonMapper();
        service = ServiceFactory.getService(SubjectService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        logger.trace(DO_GET_BEGIN);
        initialization(resp);
        try {
            List<DtoResponse> responses = new ArrayList<>();
            if (req.getPathInfo() != null)
                responses.add(service.getDataById(req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY)));
            else responses = service.getDataByParameters(req.getParameterMap());
            view.update(responses);
            logger.trace(DO_GET_END);
        } catch (IncorrectRequestException | NoDataException e) {
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
            SubjectRequest sr;
            try {
                sr = jsonMapper.getDtoFromRequest(SubjectRequest.class, req.getReader());
            } catch (IOException e) {
                throw new IncorrectRequestException(INCORRECT_BODY_OF_REQUEST);
            }
            DtoResponse response = service.create(req.getPathInfo(), sr);
            resp.setStatus(201);
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
            String subjectId = req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY);
            SubjectRequest subjectRequest = jsonMapper.getDtoFromRequest(SubjectRequest.class, req.getReader());
            DtoResponse response = service.update(subjectId, subjectRequest);
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
            String subjectId = req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY);
            service.delete(subjectId, null);
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
