package com.example.controller.servlets;

import com.example.consts.ControlerConstants;
import com.example.exeptions.IncorrectRequestException;
import com.example.exeptions.NoDataException;
import com.example.mapper.TeacherMapper;
import com.example.mapper.TeacherMapperImpl;
import com.example.mapper.viewmapper.JsonMapper;
import com.example.mapper.viewmapper.ViewMapper;
import com.example.model.dto.Request.TeacherRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.dto.Response.TeacherResponse;
import com.example.model.service.Service;
import com.example.model.service.TeacherService;
import com.example.model.vo.ModelUnit;
import com.example.model.vo.Subject;
import com.example.model.vo.Teacher;
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

import static com.example.consts.ControlerConstants.*;

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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        initialization(resp);
        try {
            checkRequestForGet(req);
            List<ModelUnit> teachers=new ArrayList<>();
            if (req.getPathInfo()!=null) {
                getTeachersByIds(req, teachers);
            }
            else teachers = service.getDataByParameters(req.getParameterMap());
            view.update(service.mappingVoToDto(teachers));
        } catch (IncorrectRequestException e) {
            logger.info(INCORRECT_REQUEST+req.getRequestURI());
            resp.sendError(400, INCORRECT_REQUEST+req.getRequestURI());
        }
    }

    private void getTeachersByIds(HttpServletRequest req, List<ModelUnit> teachers) throws IncorrectRequestException {
        String[] paths = req.getPathInfo().replaceFirst("/","").split("/");
        for(String p:paths) teachers.addAll(service.getDataById(p));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        initialization(resp);
        try {
            checkRequestForPost(req);
            if (req.getPathInfo() == null && req.getQueryString() == null) addTeacher(req, resp);
            else addToTeachersSubjects(req);
        } catch (IncorrectRequestException e) {
            logger.info(NO_DATA_FOUND+ req.getRequestURI());
            resp.sendError(400, INCORRECT_REQUEST+ req.getRequestURI());
        }
    }

    private void addToTeachersSubjects(HttpServletRequest req) throws IncorrectRequestException {
        List<ModelUnit> teachers = new ArrayList<>();
        getTeachersByIds(req, teachers);
        String[] subjectRequest = req.getParameterMap().get(SUBJECT_PARAMETER);
        List<Subject> subjects = new ArrayList<>();
        for (String subjectString: subjectRequest) subjects.add(Subject.getSubject(subjectString));
        for (ModelUnit unit: teachers) {
            Teacher teacher = (Teacher) unit;
            if(teacher!=null) teacher.getSubjects().addAll(subjects);
        }
        List<DtoResponse> teachersResponse = service.mappingVoToDto(teachers);
        view.update(teachersResponse);
    }

    private void addTeacher(HttpServletRequest req, HttpServletResponse resp) throws IOException, IncorrectRequestException {
        try(BufferedReader reader = req.getReader()) {
            TeacherRequest teacherRequest;
            teacherRequest = jsonMapper.getDtoFromRequest(TeacherRequest.class, reader);
            Teacher teacher =  mapper.mapFromRequest(teacherRequest);
            TeacherResponse teacherResponse = mapper.mapToResponse(teacher, Integer.toString(teacher.getExperience()));
            if(service.create(teacher)) {
                resp.setStatus(201);
                view.update(List.of(teacherResponse));
            }
        }
    }

    private void checkRequestForPost(HttpServletRequest req) throws IncorrectRequestException {
        boolean haveAllArgs = req.getPathInfo()!=null && req.getQueryString()!=null;
        boolean haveNoArgs = req.getPathInfo()==null && req.getQueryString()==null;
        if(!(haveAllArgs || haveNoArgs)) {
            throw new IncorrectRequestException();
        } else if(req.getPathInfo()!=null && req.getQueryString()!=null) {
            try {
                String[] paths = req.getPathInfo().replaceFirst("/", "").split("/");
                for (String path: paths) Integer.parseInt(path);
                Map<String, String[]> parameterMap = req.getParameterMap();
                if (parameterMap.size()!=1 || !parameterMap.containsKey(SUBJECT_PARAMETER)) throw new IncorrectRequestException();
                for (String value: req.getParameterMap().get(SUBJECT_PARAMETER)) if (!Subject.containRequestName(value)) throw new IncorrectRequestException();
            } catch (NumberFormatException e) { throw new IncorrectRequestException(); }
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        initialization(resp);
        try {
            checkRequestForPut(req);
            String stringId = req.getPathInfo().replaceFirst("/","");
            TeacherRequest teacherRequest = jsonMapper.getDtoFromRequest(TeacherRequest.class, req.getReader());
            List<DtoResponse> response = service.update(stringId, teacherRequest);
            view.update(response);
        } catch (IncorrectRequestException e) {
            logger.info(ControlerConstants.NO_DATA_FOUND+req.getRequestURI());
            resp.sendError(400, ControlerConstants.INCORRECT_REQUEST+req.getRequestURI());
        } catch (NoDataException e) {
            logger.info(ControlerConstants.NO_DATA_FOUND+req.getRequestURI());
            resp.sendError(404, ControlerConstants.INCORRECT_REQUEST+req.getRequestURI());
        }
    }

    private void checkRequestForPut(HttpServletRequest req) throws IncorrectRequestException {
        if(req.getPathInfo()==null) {
            throw new IncorrectRequestException();
        }
        try {
            Integer.parseInt(req.getPathInfo().replaceFirst("/", ""));
        } catch (NumberFormatException e) { throw new IncorrectRequestException(); }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        initialization(resp);
        try {
            checkRequestForDelete(req);
            if (req.getQueryString() == null) {
                String stringID = req.getPathInfo().replaceFirst("/","");
                service.delete(stringID);
                resp.setStatus(204);
            }
            else removeFromTeachersSubjects(req);
        } catch (IncorrectRequestException e) {
            logger.info(INCORRECT_REQUEST+ req.getRequestURI());
            resp.sendError(400, INCORRECT_REQUEST+ req.getRequestURI());
        } catch (NoDataException e) {
            logger.info(NO_DATA_FOUND+ req.getRequestURI());
            resp.sendError(404, NO_DATA_FOUND+ req.getRequestURI());
        }
    }

    private void removeFromTeachersSubjects(HttpServletRequest req) throws IncorrectRequestException, NoDataException {
        String stringID = req.getPathInfo().replaceFirst("/","");
        Subject subject = Subject.getSubject(req.getParameterMap().get(SUBJECT_PARAMETER)[0]);
        if (subject==null) throw new IncorrectRequestException();
        Teacher teacher = (Teacher) service.getDataById(stringID).get(0);
        if(teacher==null) throw new NoDataException();
        boolean deleted = teacher.getSubjects().remove(subject);
        if(!deleted) throw new NoDataException();
        List<ModelUnit> units = List.of(teacher);
        view.update(service.mappingVoToDto(units));
    }

    private void checkRequestForDelete(HttpServletRequest req) throws IncorrectRequestException {
        if(req.getPathInfo()==null) {
            throw new IncorrectRequestException();
        }
        if(req.getQueryString()!=null) {
            try {
                Integer.parseInt(req.getPathInfo().replaceFirst("/", ""));
                Map<String, String[]> parameterMap = req.getParameterMap();
                if (parameterMap.size()!=1 || !parameterMap.containsKey(SUBJECT_PARAMETER)) throw new IncorrectRequestException();
                if (!Subject.containRequestName(req.getParameterMap().get(SUBJECT_PARAMETER)[0])) throw new IncorrectRequestException();
            } catch (NumberFormatException e) { throw new IncorrectRequestException(); }
        }
    }

    private void checkRequestForGet(HttpServletRequest req) throws IncorrectRequestException {
        String path = req.getPathInfo();
        String query = req.getQueryString();
        if(path!=null && query!=null) throw new IncorrectRequestException();
        else if(path!=null && !isPathCorrect(req)) throw new IncorrectRequestException();
        else if (query!=null && !(isQueryCorrect(req))) throw new IncorrectRequestException();
    }
    boolean isPathCorrect(HttpServletRequest req) {
        try {
            String[] paths = req.getPathInfo().replaceFirst("/","").split("/");
            for(String p:paths) Integer.parseInt(p);
            return true;
        } catch (NumberFormatException e) {
            logger.info(INCORRECT_URL_PATH,req.getRequestURI());
            return false;
        }
    }
    boolean isQueryCorrect(HttpServletRequest req) {
        Map<String,String[]> mapQuery = req.getParameterMap();
        if (mapQuery==null) return false;
        for(String key:mapQuery.keySet()) {
            String[] queryValue = mapQuery.get(key);
            if(queryValue == null || queryValue.length != 1) return false;
            if(!TEACHER_REQUEST_PARAMETERS.contains(key)) return false;
        }
        return true;
    }
}
