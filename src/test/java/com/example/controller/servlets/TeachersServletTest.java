package com.example.controller.servlets;

import com.example.controller.ServiceFactory;
import com.example.model.service.StudentService;
import com.example.model.service.TeacherService;
import com.example.model.vo.Student;
import com.example.model.vo.Teacher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TeachersServletTest {
    TeachersServlet servlet = new TeachersServlet();
    @Test
    @SneakyThrows
    void doGet_byId() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Teacher mockedTeacher = Mockito.mock(Teacher.class);
        TeacherService mockedService = Mockito.mock(TeacherService.class);
        try(MockedStatic<ServiceFactory> mock = Mockito.mockStatic(ServiceFactory.class)) {
            mock.when(()-> ServiceFactory.getService(TeacherService.class)).thenReturn(mockedService);
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            Mockito.when(request.getPathInfo()).thenReturn("/99");
            Mockito.when(request.getMethod()).thenReturn("GET");
            Mockito.when(mockedService.getDataById(Mockito.any())).thenReturn(List.of(mockedTeacher));
            Mockito.when(mockedService.mappingVoToDto(Mockito.anyList())).thenReturn(new ArrayList<>());
            Mockito.when(response.getWriter()).thenReturn(writer);
            servlet.doGet(request,response);
            Mockito.verify(mockedService, Mockito.atMostOnce()).getDataById(Mockito.any());
        }
    }

    @Test
    @SneakyThrows
    void doGet_byParams() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        TeacherService mockedService = Mockito.mock(TeacherService.class);
        try(MockedStatic<ServiceFactory> mock = Mockito.mockStatic(ServiceFactory.class)) {
            mock.when(()-> ServiceFactory.getService(TeacherService.class)).thenReturn(mockedService);
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            Mockito.when(request.getPathInfo()).thenReturn(null);
            Mockito.when(request.getQueryString()).thenReturn("someQuery");
            Mockito.when(request.getMethod()).thenReturn("GET");
            Mockito.when(mockedService.mappingVoToDto(Mockito.anyList())).thenReturn(new ArrayList<>());
            Mockito.when(response.getWriter()).thenReturn(writer);
            servlet.doGet(request,response);
            Mockito.verify(mockedService, Mockito.atMostOnce()).getDataByParameters(Mockito.any());
        }
    }

    @Test
    @SneakyThrows
    void doPost_createTeacher() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Teacher mockedTeacher = Mockito.mock(Teacher.class);
        TeacherService mockedService = Mockito.mock(TeacherService.class);
        try(MockedStatic<ServiceFactory> mock = Mockito.mockStatic(ServiceFactory.class)) {
            mock.when(()-> ServiceFactory.getService(TeacherService.class)).thenReturn(mockedService);
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            Mockito.when(request.getPathInfo()).thenReturn(null);
            Mockito.when(request.getQueryString()).thenReturn(null);
            Mockito.when(request.getReader()).thenReturn(new BufferedReader(new FileReader(("src/test/resources/teacherServletPost.json"))));
            Mockito.when(request.getMethod()).thenReturn("POST");
            Mockito.when(mockedService.getDataById(Mockito.any())).thenReturn(List.of(mockedTeacher));
            Mockito.when(mockedService.mappingVoToDto(Mockito.anyList())).thenReturn(new ArrayList<>());
            Mockito.when(response.getWriter()).thenReturn(writer);
            Mockito.when(mockedService.create(Mockito.any())).thenReturn(true);
            servlet.doPost(request, response);
            Mockito.verify(mockedService, Mockito.atMostOnce()).create(Mockito.any());
        }
    }

    @Test
    @SneakyThrows
    void doPut() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Teacher mockedTeacher = Mockito.mock(Teacher.class);
        TeacherService mockedService = Mockito.mock(TeacherService.class);
        try(MockedStatic<ServiceFactory> mock = Mockito.mockStatic(ServiceFactory.class)) {
            mock.when(()-> ServiceFactory.getService(TeacherService.class)).thenReturn(mockedService);
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            Mockito.when(request.getPathInfo()).thenReturn("/1");
            Mockito.when(request.getQueryString()).thenReturn(null);
            Mockito.when(request.getReader()).thenReturn(new BufferedReader(new FileReader(("src/test/resources/teacherServletPost.json"))));
            Mockito.when(request.getMethod()).thenReturn("PUT");
            Mockito.when(mockedService.getDataById(Mockito.any())).thenReturn(List.of(mockedTeacher));
            Mockito.when(mockedService.mappingVoToDto(Mockito.anyList())).thenReturn(new ArrayList<>());
            Mockito.when(response.getWriter()).thenReturn(writer);
            servlet.doPut(request, response);
            Mockito.verify(mockedService, Mockito.atMostOnce()).update(Mockito.any(), Mockito.any());
        }
    }

    @Test
    @SneakyThrows
    void doDelete_deleteTeacher() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Teacher mockedTeacher = Mockito.mock(Teacher.class);
        TeacherService mockedService = Mockito.mock(TeacherService.class);
        try(MockedStatic<ServiceFactory> mock = Mockito.mockStatic(ServiceFactory.class)) {
            mock.when(()-> ServiceFactory.getService(TeacherService.class)).thenReturn(mockedService);
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            Mockito.when(request.getPathInfo()).thenReturn("/1");
            Mockito.when(request.getQueryString()).thenReturn(null);
            Mockito.when(request.getReader()).thenReturn(new BufferedReader(new FileReader(("src/test/resources/teacherServletPost.json"))));
            Mockito.when(request.getMethod()).thenReturn("DELETE");
            Mockito.when(mockedService.getDataById(Mockito.any())).thenReturn(List.of(mockedTeacher));
            Mockito.when(response.getWriter()).thenReturn(writer);
            servlet.doDelete(request, response);
            Mockito.verify(mockedService, Mockito.atMostOnce()).delete(Mockito.any());
        }
    }
}