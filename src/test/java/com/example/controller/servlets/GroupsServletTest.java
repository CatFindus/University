package com.example.controller.servlets;

import com.example.controller.ServiceFactory;
import com.example.model.service.GroupService;
import com.example.model.service.StudentService;
import com.example.model.vo.Group;
import com.example.model.vo.Student;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.example.consts.ModelConstants.RQ_ID;

class GroupsServletTest {
HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
GroupService mockedService = Mockito.mock(GroupService.class);
StudentService mockedStService = Mockito.mock(StudentService.class);

GroupsServlet groupsServlet = new GroupsServlet();
    @Test
    @SneakyThrows
    void doGet() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        Mockito.when(request.getPathInfo()).thenReturn("/99");
        Mockito.when(request.getMethod()).thenReturn("GET");
        Mockito.when(mockedService.mappingVoToDto(Mockito.anyList())).thenReturn(new ArrayList<>());
        Mockito.when(response.getWriter()).thenReturn(writer);
        groupsServlet.doGet(request, response);
        Mockito.verify(request, Mockito.times(4)).getPathInfo();
        Mockito.verify(response, Mockito.times(1)).getWriter();
        Mockito.when(request.getPathInfo()).thenReturn(null);
        Map<String,String[]> map = new HashMap<>();
        String[] value = {"99"};
        map.put(RQ_ID, value);
        Mockito.when(request.getParameterMap()).thenReturn(map);
        groupsServlet.doGet(request, response);
        Mockito.verify(request, Mockito.times(6)).getPathInfo();
        Mockito.verify(response, Mockito.times(2)).getWriter();
        Mockito.verify(request, Mockito.times(3)).getParameterMap();
    }

    @Test
    @SneakyThrows
    void doPost_addGroup() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        Mockito.when(request.getPathInfo()).thenReturn(null);
        Mockito.when(request.getMethod()).thenReturn("POST");
        Mockito.when(mockedService.mappingVoToDto(Mockito.anyList())).thenReturn(new ArrayList<>());
        Mockito.when(response.getWriter()).thenReturn(writer);
        Mockito.when(request.getParameterMap()).thenReturn(null);
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new FileReader(("src/test/resources/groupServletPost.json"))));
        groupsServlet.doPost(request,response);
        Mockito.verify(request, Mockito.atMostOnce()).getMethod();
        Mockito.verify(response, Mockito.atMostOnce()).getWriter();
        Mockito.verify(request, Mockito.atMostOnce()).getReader();
        Mockito.verify(request, Mockito.times(2)).getQueryString();
    }
    @Test
    @SneakyThrows
    void doPost_addStudents() {
        try(MockedStatic<ServiceFactory> mock = Mockito.mockStatic(ServiceFactory.class)) {
            mock.when(()-> ServiceFactory.getService(GroupService.class)).thenReturn(mockedService);
            mock.when(()-> ServiceFactory.getService(StudentService.class)).thenReturn(mockedStService);
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            Mockito.when(request.getPathInfo()).thenReturn("/1/2");
            Mockito.when(request.getMethod()).thenReturn("POST");
            Mockito.when(response.getWriter()).thenReturn(writer);
            Group mockedGroup = Mockito.mock(Group.class);
            Student mockedStudent = Mockito.mock(Student.class);
            Mockito.when(mockedGroup.getStudentsQuantity()).thenReturn(0);
            Mockito.when(mockedGroup.getStudents()).thenReturn(new CopyOnWriteArrayList<>());
            Mockito.when(mockedService.getDataById(Mockito.any())).thenReturn(List.of(mockedGroup));
            Mockito.when(mockedStService.getDataById(Mockito.any())).thenReturn(List.of(mockedStudent));
            groupsServlet.doPost(request, response);
            Mockito.verify(mockedGroup, Mockito.atMostOnce()).getStudentsQuantity();
            Mockito.verify(mockedStService, Mockito.atMostOnce()).getDataById(Mockito.any());
            Mockito.verify(mockedService, Mockito.atMostOnce()).getDataById(Mockito.any());
        }
    }

    @Test
    @SneakyThrows
    void doPut() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        Group group = new Group("SomeNumber");
        Mockito.when(request.getPathInfo()).thenReturn("/1");
        Mockito.when(request.getMethod()).thenReturn("PUT");
        Mockito.when(mockedService.mappingVoToDto(Mockito.anyList())).thenReturn(new ArrayList<>());
        Mockito.when(response.getWriter()).thenReturn(writer);
        Mockito.when(request.getParameterMap()).thenReturn(null);
        Mockito.when(request.getReader()).thenReturn(new BufferedReader(new FileReader(("src/test/resources/groupServletPut.json"))));
        groupsServlet.doPut(request, response);
        Mockito.verify(request, Mockito.times(3)).getPathInfo();
        Mockito.verify(response, Mockito.atMostOnce()).getWriter();
        Mockito.verify(request, Mockito.atMostOnce()).getReader();
    }

    @Test
    @SneakyThrows
    void doDelete_deleteGroup() {
        try(MockedStatic<ServiceFactory> mock = Mockito.mockStatic(ServiceFactory.class)) {
            mock.when(()-> ServiceFactory.getService(GroupService.class)).thenReturn(mockedService);
            Mockito.when(request.getPathInfo()).thenReturn("/1");
            Mockito.when(request.getMethod()).thenReturn("DELETE");
            groupsServlet.doDelete(request, response);
            Mockito.verify(mockedService, Mockito.atMostOnce()).delete(Mockito.any());
            Mockito.when(request.getPathInfo()).thenReturn("/1/2/3");
        }
    }
    @Test
    @SneakyThrows
    void doDelete_deleteStudents() {
        try(MockedStatic<ServiceFactory> mock = Mockito.mockStatic(ServiceFactory.class)) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            Mockito.when(response.getWriter()).thenReturn(writer);
            mock.when(()-> ServiceFactory.getService(GroupService.class)).thenReturn(mockedService);
            mock.when(()-> ServiceFactory.getService(StudentService.class)).thenReturn(mockedStService);
            Group mockedGroup = Mockito.mock(Group.class);
            Student mockedStudent = Mockito.mock(Student.class);
            Mockito.when(mockedGroup.getStudentsQuantity()).thenReturn(10);
            Mockito.when(mockedGroup.getStudents()).thenReturn(new CopyOnWriteArrayList<>(List.of(mockedStudent)));
            Mockito.when(mockedService.getDataById(Mockito.any())).thenReturn(List.of(mockedGroup));
            Mockito.when(mockedStService.getDataById(Mockito.any())).thenReturn(List.of(mockedStudent));
            Mockito.when(request.getPathInfo()).thenReturn("/1/2/3");
            Mockito.when(request.getMethod()).thenReturn("DELETE");
            groupsServlet.doDelete(request, response);
            Mockito.verify(mockedService, Mockito.atMostOnce()).delete(Mockito.any());
        }
    }
}