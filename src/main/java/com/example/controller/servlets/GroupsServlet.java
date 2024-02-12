package com.example.controller.servlets;

import com.example.exeptions.IncorrectRequestException;
import com.example.exeptions.NoDataException;
import com.example.mapper.GroupMapper;
import com.example.mapper.GroupMapperImpl;
import com.example.mapper.viewmapper.JsonMapper;
import com.example.mapper.viewmapper.ViewMapper;
import com.example.model.dto.Request.GroupRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.dto.Response.ErrorResponse;
import com.example.model.dto.Response.GroupResponse;
import com.example.model.service.GroupService;
import com.example.model.service.Service;
import com.example.model.service.StudentService;
import com.example.model.vo.*;
import com.example.validators.quantity.GroupQuantityValidator;
import com.example.validators.requests.GroupsValidator;
import com.example.view.JsonView;
import com.example.view.View;
import jakarta.servlet.ServletException;
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
import java.util.concurrent.CopyOnWriteArrayList;

import static com.example.consts.ControlerConstants.*;
import static com.example.consts.LoggerConstants.*;
import static com.example.consts.ModelConstants.*;


@WebServlet(name = "GroupsServlet", urlPatterns = "/groups/*")
public class GroupsServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(GroupsServlet.class);
    private transient View view;
    private transient Service service;
    private transient Service studentService;
    private transient ViewMapper jsonMapper;
    private transient GroupMapper mapper;

    private void initialization(HttpServletResponse resp) {
        view = new JsonView(resp);
        jsonMapper = new JsonMapper();
        service = new GroupService();
        studentService = new StudentService();
        mapper = new GroupMapperImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.trace(DO_GET_BEGIN);
        initialization(resp);
        try {

            new GroupsValidator(req).validate();
            if (req.getPathInfo() != null) {
                String[] paths = req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY).split(PATH_SEPARATOR);
                List<DtoResponse> responseList = new ArrayList<>();
                for (String path : paths) responseList.addAll(service.mappingVoToDto(service.getDataById(path)));
                view.update(responseList);
                logger.trace(DO_GET_END);
            } else {
                List<ModelUnit> units = service.getDataByParameters(req.getParameterMap());
                List<DtoResponse> responseList = service.mappingVoToDto(units);
                view.update(responseList);
                logger.trace(DO_GET_END);
            }
        } catch (IncorrectRequestException e) {
            ErrorResponse response = new ErrorResponse(e.getMessage());
            logger.warn(WARN_MSG, response.getErrorID(), e.getMessage());
            view.update(List.of(response));
        }
        super.doGet(req, resp);
    }



    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.trace(DO_POST_BEGIN);
        initialization(resp);
        try {
            new GroupsValidator(req).validate();
            if (req.getPathInfo() == null && req.getQueryString() == null) addGroup(req, resp);
            else addStudentsToGroup(req);
            logger.trace(DO_POST_END);
        } catch (IncorrectRequestException | NoDataException e) {
            ErrorResponse response = new ErrorResponse(e.getMessage());
            logger.warn(WARN_MSG, response.getErrorID(), e.getMessage());
            view.update(List.of(response));
        }
    }

    private void addStudentsToGroup(HttpServletRequest req) throws IncorrectRequestException, NoDataException {
        String[] paths = req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY).split(PATH_SEPARATOR);
        List<ModelUnit> list = service.getDataById(paths[0]);
        if (list.isEmpty()) throw  new NoDataException(NO_DATA_FOUND);
        Group group = (Group) list.get(0);
        new GroupQuantityValidator(req, group).validate();
        if (paths.length > 1) {
            for (int i = 1; i < paths.length; i++) {
                Student student = (Student) studentService.getDataById(paths[i]).get(0);
                if (student != null) group.addStudent(student);
            }
            List<ModelUnit> students = new ArrayList<>(group.getStudents());
            GroupResponse groupResponse = mapper.mapToResponse(group, studentService.mappingVoToDto(students));
            view.update(List.of(groupResponse));
        } else {
            Map<String, String[]> parameterMap = req.getParameterMap();
            for (String value : parameterMap.get(RQ_ID)) {
                Student student = (Student) studentService.getDataById(value).get(0);
                if (student != null) group.getStudents().add(student);
            }
            List<ModelUnit> students = new ArrayList<>(group.getStudents());
            GroupResponse groupResponse = mapper.mapToResponse(group, studentService.mappingVoToDto(students));
            view.update(List.of(groupResponse));
        }

    }

    private void addGroup(HttpServletRequest req, HttpServletResponse resp) throws IOException, IncorrectRequestException {
        try (BufferedReader reader = req.getReader()) {
            GroupRequest groupRequest;
            groupRequest = jsonMapper.getDtoFromRequest(GroupRequest.class, reader);
            Group group = mapper.mapFromRequest(groupRequest);
            List<ModelUnit> students = new ArrayList<>(group.getStudents());
            GroupResponse groupResponse = mapper.mapToResponse(group, studentService.mappingVoToDto(students));
            if (service.create(group)) {
                resp.setStatus(201);
                view.update(List.of(groupResponse));
            }
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
            List<DtoResponse> response = service.update(stringId, groupRequest);
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
            new GroupsValidator(req).validate();
            if (req.getQueryString() == null) {
                String[] paths = req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY).split(PATH_SEPARATOR);
                if (paths.length == 1) {
                    String stringID = paths[0];
                    service.delete(stringID);
                    resp.setStatus(204);
                } else removeStudentsFormGroup(req, resp);
            } else removeStudentsFormGroup(req, resp);
            logger.trace(DO_DELETE_END);
        } catch (IncorrectRequestException | NoDataException e) {
            ErrorResponse response = new ErrorResponse(e.getMessage());
            logger.warn(WARN_MSG, response.getErrorID(), e.getMessage());
            view.update(List.of(response));
        }
    }

    private void removeStudentsFormGroup(HttpServletRequest req, HttpServletResponse resp) throws IncorrectRequestException, NoDataException {
        String[] paths = req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY).split(PATH_SEPARATOR);
        List<ModelUnit> list = service.getDataById(paths[0]);
        if (list.isEmpty()) throw new NoDataException(NO_DATA_FOUND);
        Group group = (Group) list.get(0);
        new GroupQuantityValidator(req,group).validate();
        List<Student> studentList = new ArrayList<>();
        if (paths.length != 1) {
            for (int i = 1; i < paths.length; i++) {
                Student student = (Student) studentService.getDataById(paths[i]).get(0);
                if (!group.getStudents().contains(student)) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
                studentList.add(student);
            }
        } else {
            for (String id : req.getParameterMap().get(RQ_ID)) {
                Student student = (Student) studentService.getDataById(id);
                if (!group.getStudents().contains(student)) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
                studentList.add(student);
            }
        }
        CopyOnWriteArrayList<Student> students = group.getStudents();
        for (Student student : studentList) {
            new GroupQuantityValidator(req,group).validate();
            students.remove(student);
        }
        resp.setStatus(200);
        view.update(service.mappingVoToDto(List.of(group)));
    }


}
