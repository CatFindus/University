package com.example.controller.servlets;

import com.example.exeptions.IncorrectRequestException;
import com.example.exeptions.NoDataException;
import com.example.mapper.TeacherMapper;
import com.example.mapper.TeacherMapperImpl;
import com.example.mapper.viewmapper.JsonMapper;
import com.example.mapper.viewmapper.ViewMapper;
import com.example.model.dto.Request.TeacherRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.dto.Response.ErrorResponse;
import com.example.model.dto.Response.TeacherResponse;
import com.example.model.service.Service;
import com.example.model.service.TeacherService;
import com.example.model.vo.ModelUnit;
import com.example.model.vo.Subject;
import com.example.model.vo.Teacher;
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
import java.util.Map;

import static com.example.consts.ControlerConstants.*;
import static com.example.consts.LoggerConstants.*;
import static com.example.consts.ModelConstants.*;

@WebServlet(name = "TeachersServlet", urlPatterns = "/teachers/*")
public class TeachersServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(TeachersServlet.class);
    private transient View view;
    private transient Service service;
    private transient ViewMapper jsonMapper;
    private transient TeacherMapper mapper;

    private void initialization(HttpServletResponse resp) {
        view = new JsonView(resp);
        jsonMapper = new JsonMapper();
        service = new TeacherService();
        mapper = new TeacherMapperImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        logger.trace(DO_GET_BEGIN);
        initialization(resp);
        try {
            doGetValidation(req);
            List<ModelUnit> teachers = new ArrayList<>();
            if (req.getPathInfo() != null) {
                getTeachersByIds(req, teachers);
            } else teachers = service.getDataByParameters(req.getParameterMap());
            view.update(service.mappingVoToDto(teachers));
            logger.trace(DO_GET_END);
        } catch (IncorrectRequestException e) {
            ErrorResponse response = new ErrorResponse(e.getMessage());
            logger.warn(WARN_MSG, response.getErrorID(), e.getMessage());
            view.update(List.of(response));
        }
    }

    private void getTeachersByIds(HttpServletRequest req, List<ModelUnit> teachers) throws IncorrectRequestException {
        String[] paths = req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY).split(PATH_SEPARATOR);
        for (String p : paths) teachers.addAll(service.getDataById(p));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.trace(DO_POST_BEGIN);
        initialization(resp);
        try {
            doPostValidation(req);
            if (req.getPathInfo() == null && req.getQueryString() == null) addTeacher(req, resp);
            else addToTeachersSubjects(req);
            logger.trace(DO_POST_END);
        } catch (IncorrectRequestException e) {
            ErrorResponse response = new ErrorResponse(e.getMessage());
            logger.warn(WARN_MSG, response.getErrorID(), e.getMessage());
            view.update(List.of(response));
        }
    }

    private void addToTeachersSubjects(HttpServletRequest req) throws IncorrectRequestException {
        List<ModelUnit> teachers = new ArrayList<>();
        getTeachersByIds(req, teachers);
        String[] subjectRequest = req.getParameterMap().get(SUBJECT_PARAMETER);
        List<Subject> subjects = new ArrayList<>();
        for (String subjectString : subjectRequest) subjects.add(Subject.getSubject(subjectString));
        for (ModelUnit unit : teachers) {
            Teacher teacher = (Teacher) unit;
            if (teacher != null) teacher.getSubjects().addAll(subjects);
        }
        List<DtoResponse> teachersResponse = service.mappingVoToDto(teachers);
        view.update(teachersResponse);
    }

    private void addTeacher(HttpServletRequest req, HttpServletResponse resp) throws IOException, IncorrectRequestException {

        TeacherRequest teacherRequest;
        teacherRequest = jsonMapper.getDtoFromRequest(TeacherRequest.class, req.getReader());
        Teacher teacher = mapper.mapFromRequest(teacherRequest);
        TeacherResponse teacherResponse = mapper.mapToResponse(teacher, Integer.toString(teacher.getExperience()));
        if (service.create(teacher)) {
            resp.setStatus(201);
            view.update(List.of(teacherResponse));
        }

    }

    private void doPostValidation(HttpServletRequest req) throws IncorrectRequestException {
        logger.trace(START_VALIDATION);
        boolean haveAllArgs = req.getPathInfo() != null && req.getQueryString() != null;
        boolean haveNoArgs = req.getPathInfo() == null && req.getQueryString() == null;
        if (!(haveAllArgs || haveNoArgs)) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
        else if (req.getPathInfo() != null && req.getQueryString() != null) {
            try {
                String[] paths = req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY).split(PATH_SEPARATOR);
                for (String path : paths) Integer.parseInt(path);
                Map<String, String[]> parameterMap = req.getParameterMap();
                if (parameterMap.size() != 1 || !parameterMap.containsKey(SUBJECT_PARAMETER))
                    throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
                for (String value : req.getParameterMap().get(SUBJECT_PARAMETER))
                    if (!Subject.containRequestName(value)) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
                logger.trace(END_VALIDATION_SUCCESSFUL);
            } catch (NumberFormatException e) {
                logger.trace(END_VALIDATION_UNSUCCESSFUL, e.getMessage());
                throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.trace(DO_PUT_BEGIN);
        initialization(resp);
        try {
            checkRequestForPut(req);
            String stringId = req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY);
            TeacherRequest teacherRequest = jsonMapper.getDtoFromRequest(TeacherRequest.class, req.getReader());
            List<DtoResponse> response = service.update(stringId, teacherRequest);
            view.update(response);
            logger.trace(DO_PUT_END);
        } catch (IncorrectRequestException | NoDataException e) {
            ErrorResponse response = new ErrorResponse(e.getMessage());
            logger.warn(WARN_MSG, response.getErrorID(), e.getMessage());
            view.update(List.of(response));
        }
    }

    private void checkRequestForPut(HttpServletRequest req) throws IncorrectRequestException {
        logger.trace(START_VALIDATION);
        if (req.getPathInfo() == null) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
        try {
            Integer.parseInt(req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY));
            logger.trace(END_VALIDATION_SUCCESSFUL);
        } catch (NumberFormatException e) {
            logger.trace(END_VALIDATION_UNSUCCESSFUL, e.getMessage());
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        logger.trace(DO_DELETE_BEGIN);
        initialization(resp);
        try {
            doDeleteValidator(req);
            if (req.getQueryString() == null) {
                String stringID = req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY);
                service.delete(stringID);
                resp.setStatus(204);
            } else removeFromTeachersSubjects(req);
            logger.trace(DO_DELETE_END);
        } catch (IncorrectRequestException | NoDataException e) {
            ErrorResponse response = new ErrorResponse(e.getMessage());
            logger.warn(WARN_MSG, response.getErrorID(), e.getMessage());
            view.update(List.of(response));
        }
    }

    private void removeFromTeachersSubjects(HttpServletRequest req) throws IncorrectRequestException, NoDataException {
        String stringID = req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY);
        Subject subject = Subject.getSubject(req.getParameterMap().get(SUBJECT_PARAMETER)[0]);
        if (subject == null) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
        Teacher teacher = (Teacher) service.getDataById(stringID).get(0);
        if (teacher == null) throw new NoDataException(NO_DATA_FOUND);
        boolean deleted = teacher.getSubjects().remove(subject);
        if (!deleted) throw new NoDataException(NO_DATA_FOUND);
        List<ModelUnit> units = List.of(teacher);
        view.update(service.mappingVoToDto(units));
    }

    private void doDeleteValidator(HttpServletRequest req) throws IncorrectRequestException {
        logger.trace(START_VALIDATION);
        if (req.getPathInfo() == null) throw new IncorrectRequestException(INCORRECT_PATH_FORMAT);
        if (req.getQueryString() != null) {
            try {
                Integer.parseInt(req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY));
                Map<String, String[]> parameterMap = req.getParameterMap();
                if (parameterMap.size() != 1 || !parameterMap.containsKey(SUBJECT_PARAMETER))
                    throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
                if (!Subject.containRequestName(req.getParameterMap().get(SUBJECT_PARAMETER)[0]))
                    throw new IncorrectRequestException(INCORRECT_BODY_OF_REQUEST);
                logger.trace(END_VALIDATION_SUCCESSFUL);
            } catch (NumberFormatException e) {
                logger.trace(END_VALIDATION_UNSUCCESSFUL, e.getMessage());
                throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
            }
        }
    }

    private void doGetValidation(HttpServletRequest req) throws IncorrectRequestException {
        String path = req.getPathInfo();
        String query = req.getQueryString();
        if (path != null && query != null) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
        else if (path != null) pathValidation(req);
        else if (query != null) queryValidation(req);
    }

    private void pathValidation(HttpServletRequest req) throws IncorrectRequestException {
        try {
            String[] paths = req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY).split(PATH_SEPARATOR);
            for (String p : paths) Integer.parseInt(p);
        } catch (NumberFormatException e) {
            logger.info(INCORRECT_URL_PATH, req.getRequestURI());
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }
    }

    private void queryValidation(HttpServletRequest req) throws IncorrectRequestException {
        Map<String, String[]> mapQuery = req.getParameterMap();
        if (mapQuery == null) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
        for (String key : mapQuery.keySet()) {
            String[] queryValue = mapQuery.get(key);
            if (queryValue == null || queryValue.length != 1)
                throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
            if (!TEACHER_REQUEST_PARAMETERS.contains(key)) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
        }
    }
}
