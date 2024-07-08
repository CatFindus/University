package com.example.controller.servlets;

import com.example.controller.ServiceFactory;
import com.example.exeptions.IncorrectRequestException;
import com.example.exeptions.NoDataException;
import com.example.mapper.viewmapper.JsonMapper;
import com.example.mapper.viewmapper.ViewMapper;
import com.example.model.dto.Request.GroupRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.dto.Response.ErrorResponse;
import com.example.model.dto.Response.NoDataResponse;
import com.example.model.service.GroupService;
import com.example.model.service.Service;
import com.example.validators.requests.GroupsValidator;
import com.example.view.JsonView;
import com.example.view.View;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.consts.ControlerConstants.*;
import static com.example.consts.LoggerConstants.*;
import static com.example.consts.ModelConstants.*;


@WebServlet(name = "GroupsServlet", urlPatterns = "/groups/*")
public class GroupsServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(GroupsServlet.class);
    private View view;
    private Service service;
    private ViewMapper jsonMapper;


    private void initialization(HttpServletResponse resp) {
        view = new JsonView(resp);
        jsonMapper = new JsonMapper();
        service = ServiceFactory.getService(GroupService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        logger.trace(DO_GET_BEGIN);
        initialization(resp);
        try {
            new GroupsValidator(req).validate();
            if (req.getPathInfo() != null) {
                String path = req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY);
                List<DtoResponse> responseList = new ArrayList<>();
                DtoResponse response = service.getDataById(path);
                if (!(response instanceof NoDataResponse)) responseList.add(response);
                view.update(responseList);
                logger.trace(DO_GET_END);
            } else {
                List<DtoResponse> responseList = service.getDataByParameters(req.getParameterMap());
                view.update(responseList);
                logger.trace(DO_GET_END);
            }
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
            new GroupsValidator(req).validate();
            if (req.getPathInfo() == null) addGroup(req, resp);
            else addStudentsToGroup(req);
            logger.trace(DO_POST_END);
        } catch (IncorrectRequestException | NoDataException e) {
            ErrorResponse response = new ErrorResponse(e.getMessage());
            logger.warn(WARN_MSG, response.getErrorID(), e.getMessage());
            view.update(List.of(response));
        }
    }

    private void addStudentsToGroup(HttpServletRequest req) throws IncorrectRequestException, NoDataException {
        String paths = req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY);
        if (req.getQueryString()==null || req.getParameterMap().get(RQ_STUDENT_ID)==null) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
        GroupRequest request = GroupRequest.builder().studentId(req.getParameterMap().get(RQ_STUDENT_ID)[0]).build();
        DtoResponse result = service.create(paths, request);
        view.update(List.of(result));
    }

    private void addGroup(HttpServletRequest req, HttpServletResponse resp) throws IOException, IncorrectRequestException, NoDataException {
        try (BufferedReader reader = req.getReader()) {
            GroupRequest groupRequest;
            groupRequest = jsonMapper.getDtoFromRequest(GroupRequest.class, reader);
            DtoResponse response = service.create(null, groupRequest);
            resp.setStatus(201);
            view.update(List.of(response));
        }
    }



    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.trace(DO_PUT_BEGIN);
        initialization(resp);
        try {
            new GroupsValidator(req).validate();
            String stringId = req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY);
            GroupRequest groupRequest = jsonMapper.getDtoFromRequest(GroupRequest.class, req.getReader());
            DtoResponse response = service.update(stringId, groupRequest);
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
            new GroupsValidator(req).validate();
            String studentId=null;
            Map<String, String[]> parameterMap = req.getParameterMap();
            GroupRequest request = req.getQueryString()!=null ? new GroupRequest() : null;
            if(req.getQueryString()!=null) {
                String[] strings = parameterMap.get(RQ_STUDENT_ID);
                if (strings!=null && strings.length>=1) studentId = strings[0];
                if (request!=null) request.setStudentId(studentId);
            }
            DtoResponse response = service.delete(req.getPathInfo().replaceFirst(PATH_SEPARATOR,EMPTY), request);
            List<DtoResponse> responses = new ArrayList<>();
            if (!(response instanceof NoDataResponse)) {
                resp.setStatus(200);
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
