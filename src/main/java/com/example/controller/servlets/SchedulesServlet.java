package com.example.controller.servlets;

import com.example.exeptions.IncorrectRequestException;
import com.example.exeptions.NoDataException;
import com.example.mapper.ScheduleMapper;
import com.example.mapper.ScheduleMapperImpl;
import com.example.mapper.viewmapper.JsonMapper;
import com.example.mapper.viewmapper.ViewMapper;
import com.example.model.dto.Request.ScheduleUnitRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.dto.Response.ErrorResponse;
import com.example.model.service.ScheduleService;
import com.example.model.vo.ModelUnit;
import com.example.model.vo.ScheduleUnit;
import com.example.validators.requests.SchedulesValidator;
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        logger.trace(DO_GET_BEGIN);
        initialization(resp);
        try {
            new SchedulesValidator(req).validate();
            List<ModelUnit> units = service.getDataByParameters(req.getParameterMap());
            if (units.isEmpty()) view.update(new ArrayList<>());
            else {
                view.update(service.mappingVoToDto(units));
            }
            logger.trace(DO_GET_END);
        } catch (IncorrectRequestException e) {
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
            new SchedulesValidator(req).validate();
            ScheduleUnitRequest request = jsonMapper.getDtoFromRequest(ScheduleUnitRequest.class, req.getReader());
            ScheduleUnit unit = mapper.mapDtoToScheduleUnit(request);
            if (unit != null && service.create(unit)) {
                resp.setStatus(201);
                view.update(List.of(mapper.mapScheduleUnitToDto(unit)));
            } else {
                resp.setStatus(200);
                view.update(new ArrayList<>());

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
            new SchedulesValidator(req).validate();
            ScheduleUnitRequest request = jsonMapper.getDtoFromRequest(ScheduleUnitRequest.class, req.getReader());
            List<DtoResponse> responses = service.update(req.getParameterMap(), request);
            view.update(responses);
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
            new SchedulesValidator(req).validate();
            service.delete(req.getParameterMap());
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
