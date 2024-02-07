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
import com.example.model.dto.Response.StudentResponse;
import com.example.model.service.Service;
import com.example.model.service.StudentService;
import com.example.model.vo.ModelUnit;
import com.example.model.vo.Student;
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        initialization(resp);
        if (isGetRequestCorrect(req)) {
            try {
                List<ModelUnit> students=null;
                if (req.getPathInfo()!=null) students = service.getDataById(req.getPathInfo().replaceFirst("/",""));
                else students = service.getDataByParameters(req.getParameterMap());
                view.update(service.mappingVoToDto(students));
            } catch (IncorrectRequestException e) {
                logger.info(ControlerConstants.INCORRECT_REQUEST+req.getRequestURI());
                resp.sendError(400, ControlerConstants.INCORRECT_REQUEST+req.getRequestURI());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        initialization(resp);
        CheckRequestForPost(req, resp);
        try(BufferedReader reader = req.getReader()) {
            StudentRequest studentRequest = null;
            try {
                studentRequest = jsonMapper.getDtoFromRequest(StudentRequest.class, req.getReader());
            } catch (IOException e) {
                throw new IncorrectRequestException();
            }
            Student student =  mapper.mapFromRequest(studentRequest);
            StudentResponse studentResponse = mapper.mapToResponse(student, null);
            if(service.create(student)) {
                resp.setStatus(201);
                view.update(List.of(studentResponse));
            }
        } catch (IncorrectRequestException e) {
                logger.info(ControlerConstants.NO_DATA_FOUND+req.getRequestURI());
                resp.sendError(400, ControlerConstants.INCORRECT_REQUEST+req.getRequestURI());
        }

    }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        initialization(resp);
        CheckRequestForPutDelete(req, resp);
        try {
            String studentId = req.getPathInfo().replaceFirst("/","");
            StudentRequest studentRequest = jsonMapper.getDtoFromRequest(StudentRequest.class, req.getReader());
            List<DtoResponse> response = service.update(studentId, studentRequest);
            view.update(response);
        } catch (IncorrectRequestException e) {
            logger.info(ControlerConstants.NO_DATA_FOUND+req.getRequestURI());
            resp.sendError(400, ControlerConstants.INCORRECT_REQUEST+req.getRequestURI());
        } catch (NoDataException e) {
            logger.info(ControlerConstants.NO_DATA_FOUND+req.getRequestURI());
            resp.sendError(404, ControlerConstants.INCORRECT_REQUEST+req.getRequestURI());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        initialization(resp);
        CheckRequestForPutDelete(req, resp);
        try {
            String studentId = req.getPathInfo().replaceFirst("/","");
            service.delete(studentId);
            resp.setStatus(204);
            view.update(new ArrayList<>());
        } catch (IncorrectRequestException e) {
            logger.info(ControlerConstants.NO_DATA_FOUND+req.getRequestURI());
            resp.sendError(400, ControlerConstants.INCORRECT_REQUEST+req.getRequestURI());
        } catch (NoDataException e) {
            logger.info(ControlerConstants.NO_DATA_FOUND+req.getRequestURI());
            resp.sendError(404, ControlerConstants.INCORRECT_REQUEST+req.getRequestURI());
        }
    }
    private boolean isGetRequestCorrect(HttpServletRequest req) {
        String path = req.getPathInfo();
        String query = req.getQueryString();
        if(path!=null && query!=null) return false;
        else if(path!=null) {
            return isPathCorrect(req);
        } else if (query!=null) {
            return isQueryCorrect(req);
        }
        return true;
    }
    boolean isPathCorrect(HttpServletRequest req) {
        try {
            Integer.parseInt(req.getPathInfo().replaceFirst("/", ""));
            return true;
        } catch (NumberFormatException e) {
            logger.info(ControlerConstants.INCORRECT_URL_PATH,req.getRequestURI());
            return false;
        }
    }
    boolean isQueryCorrect(HttpServletRequest req) {
        Map<String,String[]> mapQuery = req.getParameterMap();
        if (mapQuery==null) return false;
        for(String key:mapQuery.keySet()) {
            String[] queryValue = mapQuery.get(key);
            if(queryValue == null || queryValue.length != 1) return false;
            if(!ControlerConstants.STUDENT_REQUEST_PARAMETERS.contains(key)) return false;
        }
        return true;
    }
    private void CheckRequestForPutDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if(req.getPathInfo()==null || !isPathCorrect(req)) {
            logger.info(ControlerConstants.INCORRECT_REQUEST+ req.getRequestURI());
            resp.sendError(400, ControlerConstants.INCORRECT_REQUEST+ req.getRequestURI());
        }
    }
    private void CheckRequestForPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if(req.getPathInfo()!=null || req.getQueryString()!=null) {
            logger.info(ControlerConstants.INCORRECT_REQUEST+ req.getRequestURI());
            resp.sendError(400, ControlerConstants.INCORRECT_REQUEST+ req.getRequestURI());
        }
    }
}
