package com.example.controller.servlets;

import com.example.consts.ControlerConstants;
import com.example.exeptions.IncorrectRequestException;
import com.example.exeptions.NoDataException;
import com.example.mapper.GroupMapper;
import com.example.mapper.GroupMapperImpl;
import com.example.mapper.viewmapper.JsonMapper;
import com.example.mapper.viewmapper.ViewMapper;
import com.example.model.dto.Request.GroupRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.dto.Response.GroupResponse;
import com.example.model.service.GroupService;
import com.example.model.service.Service;
import com.example.model.service.StudentService;
import com.example.model.vo.*;
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
import static com.example.consts.ControlerConstants.NO_DATA_FOUND;


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
        initialization(resp);
        try {
            checkRequestForGet(req);
            if(req.getPathInfo()!=null) {
                String[] paths = req.getPathInfo().replaceFirst("/","").split("/");
                List<DtoResponse> responseList = new ArrayList<>();
                for(String path:paths) responseList.addAll(service.mappingVoToDto(service.getDataById(path)));
                view.update(responseList);
            } else {
                List<ModelUnit> units = service.getDataByParameters(req.getParameterMap());
                List<DtoResponse> responseList = service.mappingVoToDto(units);
                view.update(responseList);
            }
        } catch (IncorrectRequestException e) {
            logger.info(ControlerConstants.INCORRECT_REQUEST, req.getRequestURI());
            resp.sendError(400, ControlerConstants.INCORRECT_REQUEST+req.getRequestURI());
        }
        super.doGet(req, resp);
    }

    private void checkRequestForGet(HttpServletRequest req) throws IncorrectRequestException {
        if(req.getPathInfo()!=null) {
            try {
                String[] paths = req.getPathInfo().replaceFirst("/","").split("/");
                for (String path : paths) Integer.parseInt(path);
            } catch (NumberFormatException e) { throw new IncorrectRequestException(); }
        } else {
          for(String key:req.getParameterMap().keySet()) if(!ControlerConstants.GROUP_REQUEST_PARAMETERS.contains(key)) throw new IncorrectRequestException();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        initialization(resp);
        try {
            checkRequestForPost(req);
            if (req.getPathInfo() == null && req.getQueryString() == null) addGroup(req, resp);
            else addStudentsToGroup(req);
        } catch (IncorrectRequestException e) {
            logger.info(ControlerConstants.INCORRECT_REQUEST, req.getRequestURI());
            resp.sendError(400, ControlerConstants.INCORRECT_REQUEST+req.getRequestURI());
        }
    }

    private void addStudentsToGroup(HttpServletRequest req) throws IncorrectRequestException {
        String[] paths = req.getPathInfo().replaceFirst("/","").split("/");
        Group group = (Group) service.getDataById(paths[0]).get(0);
        if(paths.length>1) {
            for (int i = 1; i < paths.length; i++) {
                Student student = (Student) studentService.getDataById(paths[i]).get(0);
                if(student!=null) group.addStudent(student);
            }
            List<ModelUnit> students = new ArrayList<>(group.getStudents());
            GroupResponse groupResponse = mapper.mapToResponse(group, studentService.mappingVoToDto(students));
            view.update(List.of(groupResponse));
        } else {
            Map<String, String[]> parameterMap = req.getParameterMap();
            for (String value: parameterMap.get(ControlerConstants.ID_PARAMETER)) {
                Student student = (Student) studentService.getDataById(value).get(0);
                if(student!=null) group.getStudents().add(student);
            }
            List<ModelUnit> students = new ArrayList<>(group.getStudents());
            GroupResponse groupResponse = mapper.mapToResponse(group,studentService.mappingVoToDto(students));
            view.update(List.of(groupResponse));
        }

    }

    private void addGroup(HttpServletRequest req, HttpServletResponse resp) throws IOException, IncorrectRequestException {
        try(BufferedReader reader = req.getReader()) {
            GroupRequest groupRequest;
            groupRequest = jsonMapper.getDtoFromRequest(GroupRequest.class, reader);
            Group group =  mapper.mapFromRequest(groupRequest);
            List<ModelUnit> students = new ArrayList<>(group.getStudents());
            GroupResponse groupResponse = mapper.mapToResponse(group, studentService.mappingVoToDto(students));
            if(service.create(group)) {
                resp.setStatus(201);
                view.update(List.of(groupResponse));
            }
        }
    }

    private void checkRequestForPost(HttpServletRequest req) throws IncorrectRequestException {
        boolean haveNoArgs = req.getPathInfo()==null && req.getQueryString()==null;
        if (haveNoArgs) return;
        boolean haveQueryArgs = req.getQueryString()!=null;
        String[] paths = req.getPathInfo().replaceFirst("/","").split("/");
        try {
            if(haveQueryArgs) {
                if (paths.length != 1) throw new IncorrectRequestException();
                Integer.parseInt(paths[0]);
                Map<String, String[]> parameterMap = req.getParameterMap();
                if (parameterMap.size()!=1 &&!parameterMap.containsKey(ControlerConstants.ID_PARAMETER)) throw new IncorrectRequestException();
                for (String value:parameterMap.get(ID_PARAMETER)) Integer.parseInt(value);
            } else {
                if (paths.length <= 1) throw new IncorrectRequestException();
                for (String path : paths) Integer.parseInt(path);
            }
        } catch (NumberFormatException e) { throw new IncorrectRequestException(); }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        initialization(resp);
        try {
            checkRequestForPut(req);
            String stringId = req.getPathInfo().replaceFirst("/","");
            GroupRequest groupRequest = jsonMapper.getDtoFromRequest(GroupRequest.class, req.getReader());
            List<DtoResponse> response = service.update(stringId, groupRequest);
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
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        initialization(resp);
        try {
            checkRequestForDelete(req);
            if (req.getQueryString() == null) {
                String[] paths = req.getPathInfo().replaceFirst("/","").split("/");
                if(paths.length==1) {
                    String stringID = paths[0];
                    service.delete(stringID);
                    resp.setStatus(204);
                }
                else removeStudentsFormGroup(req,resp);
            }
            else removeStudentsFormGroup(req,resp);
        } catch (IncorrectRequestException e) {
            logger.info(INCORRECT_REQUEST+ req.getRequestURI());
            resp.sendError(400, INCORRECT_REQUEST+ req.getRequestURI());
        } catch (NoDataException e) {
            logger.info(NO_DATA_FOUND+ req.getRequestURI());
            resp.sendError(404, NO_DATA_FOUND+ req.getRequestURI());
        }
    }

    private void removeStudentsFormGroup(HttpServletRequest req, HttpServletResponse resp) throws IncorrectRequestException {
        String[] paths = req.getPathInfo().replaceFirst("/","").split("/");
        Group group = (Group) service.getDataById(paths[0]).get(0);
        if (group==null) throw new IncorrectRequestException();
        if(paths.length!=1) {
            List<Student> studentList = new ArrayList<>();
            for (int i = 1; i < paths.length; i++) {
                Student student = (Student) studentService.getDataById(paths[i]).get(0);
                if (!group.getStudents().contains(student)) throw new IncorrectRequestException();
                studentList.add(student);
            }
            studentList.forEach(group.getStudents()::remove);
        } else {
            List<Student> studentList = new ArrayList<>();
            for (String id: req.getParameterMap().get(ID_PARAMETER)) {
                Student student = (Student) studentService.getDataById(id);
                if (!group.getStudents().contains(student)) throw new IncorrectRequestException();
                studentList.add(student);
            }
            studentList.forEach(group.getStudents()::remove);
        }
        resp.setStatus(200);
        view.update(service.mappingVoToDto(List.of(group)));
    }

    private void checkRequestForDelete(HttpServletRequest req) throws IncorrectRequestException {
        if(req.getPathInfo()==null) {
            throw new IncorrectRequestException();
        }
        if(req.getQueryString()!=null) {
            try {
                Integer.parseInt(req.getPathInfo().replaceFirst("/", ""));
                Map<String, String[]> parameterMap = req.getParameterMap();
                if (parameterMap.size()!=1 || !parameterMap.containsKey(ID_PARAMETER)) throw new IncorrectRequestException();
                if (!Subject.containRequestName(req.getParameterMap().get(ID_PARAMETER)[0])) throw new IncorrectRequestException();
            } catch (NumberFormatException e) { throw new IncorrectRequestException(); }
        } else {
            String[] paths = req.getPathInfo().replaceFirst("/","").split("/");
            if(paths.length<1) throw new IncorrectRequestException();
            try {
                for(String path:paths) Integer.parseInt(path);
            } catch (NumberFormatException e) { throw new IncorrectRequestException(); }
        }

    }
}
