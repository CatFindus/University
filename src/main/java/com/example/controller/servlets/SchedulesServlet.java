package com.example.controller.servlets;

import com.example.consts.ControlerConstants;
import com.example.consts.ModelConstants;
import com.example.exeptions.IncorrectRequestException;
import com.example.exeptions.NoDataException;
import com.example.mapper.ScheduleMapper;
import com.example.mapper.ScheduleMapperImpl;
import com.example.mapper.viewmapper.JsonMapper;
import com.example.mapper.viewmapper.ViewMapper;
import com.example.model.dto.Request.ScheduleUnitRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.service.ScheduleService;
import com.example.model.vo.ModelUnit;
import com.example.model.vo.ScheduleUnit;
import com.example.model.vo.Subject;
import com.example.view.JsonView;
import com.example.view.View;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.consts.ModelConstants.*;

@WebServlet(name = "ScheduleServlet", urlPatterns = "/schedules/*")
public class SchedulesServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(TeachersServlet.class);
    private transient View view;
    private transient ScheduleService service;
    private transient ViewMapper jsonMapper;
    private transient ScheduleMapper mapper;
    private void initialization(HttpServletResponse resp) {
        view = new JsonView(resp);
        jsonMapper = new JsonMapper();
        service = new ScheduleService();
        mapper = new ScheduleMapperImpl();
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        initialization(resp);
        try {
            doGetRequestValidation(req);
            List<ModelUnit> units = service.getDataByParameters(req.getParameterMap());
            if(units.isEmpty()) view.update(new ArrayList<>());
            else {
                view.update(service.mappingVoToDto(units));
            }
        } catch (IncorrectRequestException e) {
            logger.info(ControlerConstants.INCORRECT_REQUEST+req.getRequestURI());
            resp.sendError(400, ControlerConstants.INCORRECT_REQUEST+req.getRequestURI());
        }
    }

    private void doGetRequestValidation(HttpServletRequest req) throws IncorrectRequestException {
        Map<String,String[]> parameterMap = req.getParameterMap();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ModelConstants.DATE_TIME_PARAM_PATTERN);
        if (req.getPathInfo()!=null || parameterMap.size()<2) throw new IncorrectRequestException();
        try {
            LocalDateTime begin = null, end = null;
            for(String key:parameterMap.keySet()) {
                switch (key) {
                    case RQ_BEGIN_DATE_TIME -> begin = LocalDateTime.parse(parameterMap.get(RQ_BEGIN_DATE_TIME)[0], formatter);
                    case RQ_END_DATE_TIME -> end = LocalDateTime.parse(parameterMap.get(RQ_END_DATE_TIME)[0], formatter);
                    case RQ_SUBJECT -> checkRqSubject(parameterMap);
                    case RQ_STUDENT_ID -> checkRqStudentId(parameterMap);
                    case RQ_GROUP_ID -> checkRqGroupId(parameterMap);
                    case RQ_TEACHER_ID -> checkTeacherId(parameterMap);
                    default -> throw new IncorrectRequestException();
                }
            }
            if (begin==null || end==null || begin.isAfter(end)) throw new IncorrectRequestException();
        } catch (DateTimeParseException | NumberFormatException e) { throw new IncorrectRequestException();
        }

    }

    private void checkTeacherId(Map<String, String[]> parameterMap) throws IncorrectRequestException {
        if(parameterMap.get(RQ_TEACHER_ID).length!=1) throw new IncorrectRequestException();
        Integer.parseInt(parameterMap.get(RQ_TEACHER_ID)[0]);
    }

    private void checkRqGroupId(Map<String, String[]> parameterMap) throws IncorrectRequestException {
        if(parameterMap.get(RQ_GROUP_ID).length!=1) throw new IncorrectRequestException();
        Integer.parseInt(parameterMap.get(RQ_GROUP_ID)[0]);
    }

    private void checkRqStudentId(Map<String, String[]> parameterMap) throws IncorrectRequestException {
        if(parameterMap.get(RQ_STUDENT_ID).length!=1) throw new IncorrectRequestException();
        Integer.parseInt(parameterMap.get(RQ_STUDENT_ID)[0]);
    }

    private void checkRqSubject(Map<String, String[]> parameterMap) throws IncorrectRequestException {
        for (String value: parameterMap.get(RQ_SUBJECT))
            if(!Subject.containRequestName(value)) throw new IncorrectRequestException();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        initialization(resp);
        try {
            doPostRequestValidation(req);
            ScheduleUnitRequest request = jsonMapper.getDtoFromRequest(ScheduleUnitRequest.class, req.getReader());
            ScheduleUnit unit = mapper.mapDtoToScheduleUnit(request);
            if (unit!=null && service.create(unit)) {
                resp.setStatus(201);
                view.update(List.of(mapper.mapScheduleUnitToDto(unit)));
            } else {
                resp.setStatus(200);
                view.update(new ArrayList<>());

            }
        } catch (IncorrectRequestException e) {
            logger.info(ControlerConstants.INCORRECT_REQUEST+req.getRequestURI());
            resp.sendError(400, ControlerConstants.INCORRECT_REQUEST+req.getRequestURI());
        }
    }

    private void doPostRequestValidation(HttpServletRequest req) throws IncorrectRequestException {
        if (req.getPathInfo()!=null || req.getQueryString()!=null) throw new IncorrectRequestException();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        initialization(resp);
        try {
            doGetRequestValidation(req);
            ScheduleUnitRequest request = jsonMapper.getDtoFromRequest(ScheduleUnitRequest.class, req.getReader());
            List<DtoResponse> responses = service.update(req.getParameterMap(), request);
            view.update(responses);
        } catch (IncorrectRequestException e) {
            logger.info(ControlerConstants.INCORRECT_REQUEST+req.getRequestURI());
            resp.sendError(400, ControlerConstants.INCORRECT_REQUEST+req.getRequestURI());
        } catch (NoDataException e) {
            logger.info(ControlerConstants.NO_DATA_FOUND+req.getRequestURI());
            resp.sendError(400, ControlerConstants.NO_DATA_FOUND+req.getRequestURI());
        }
    }
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        initialization(resp);
        try {
            doGetRequestValidation(req);
            service.delete(req.getParameterMap());
            resp.setStatus(204);
            view.update(new ArrayList<>());
        } catch (IncorrectRequestException e) {
            logger.info(ControlerConstants.INCORRECT_REQUEST+req.getRequestURI());
            resp.sendError(400, ControlerConstants.INCORRECT_REQUEST+req.getRequestURI());
        } catch (NoDataException e) {
            logger.info(ControlerConstants.NO_DATA_FOUND+req.getRequestURI());
            resp.sendError(400, ControlerConstants.NO_DATA_FOUND+req.getRequestURI());
        }
    }
}
