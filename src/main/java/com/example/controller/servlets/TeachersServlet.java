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
            new TeachersValidator(req).validate();
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
            new TeachersValidator(req).validate();
            if (req.getPathInfo() == null && req.getQueryString() == null) addTeacher(req, resp);
            else addToTeachersSubjects(req);
            logger.trace(DO_POST_END);
        } catch (IncorrectRequestException | NoDataException e) {
            ErrorResponse response = new ErrorResponse(e.getMessage());
            logger.warn(WARN_MSG, response.getErrorID(), e.getMessage());
            view.update(List.of(response));
        }
    }

    private void addToTeachersSubjects(HttpServletRequest req) throws IncorrectRequestException, NoDataException {
        List<ModelUnit> teachers = new ArrayList<>();
        getTeachersByIds(req, teachers);
        if (teachers.isEmpty()) throw new NoDataException(NO_DATA_FOUND);
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

        TeacherRequest tr;
        tr = jsonMapper.getDtoFromRequest(TeacherRequest.class, req.getReader());
        new NameValidator(tr.getFirstName(), tr.getMiddleName(), tr.getSurName()).
                then(new PhoneValidator(tr.getPhoneNumber())).validate();
        Teacher teacher = mapper.mapFromRequest(tr);
        TeacherResponse teacherResponse = mapper.mapToResponse(teacher, Integer.toString(teacher.getExperience()));
        if (service.create(teacher)) {
            resp.setStatus(201);
            view.update(List.of(teacherResponse));
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
            List<DtoResponse> response = service.update(stringId, teacherRequest);
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
            new TeachersValidator(req).validate();
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


}
