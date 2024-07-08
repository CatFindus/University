package com.example.controller.servlets;


class StudentsServletTest {
    /*
    StudentsServlet servlet = new StudentsServlet();
    @Test
    @SneakyThrows
    void doGet_byId() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Student mockedStudent = Mockito.mock(Student.class);
        StudentService mockedService = Mockito.mock(StudentService.class);
        try(MockedStatic<ServiceFactory> mock = Mockito.mockStatic(ServiceFactory.class)) {
            mock.when(()-> ServiceFactory.getService(StudentService.class)).thenReturn(mockedService);
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            Mockito.when(request.getPathInfo()).thenReturn("/99");
            Mockito.when(request.getMethod()).thenReturn("GET");
            Mockito.when(mockedService.getDataById(Mockito.any())).thenReturn(List.of(mockedStudent));
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
        StudentService mockedService = Mockito.mock(StudentService.class);
        try(MockedStatic<ServiceFactory> mock = Mockito.mockStatic(ServiceFactory.class)) {
            mock.when(()-> ServiceFactory.getService(StudentService.class)).thenReturn(mockedService);
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
    void doPost() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Student mockedStudent = Mockito.mock(Student.class);
        StudentService mockedService = Mockito.mock(StudentService.class);
        try(MockedStatic<ServiceFactory> mock = Mockito.mockStatic(ServiceFactory.class)) {
            mock.when(()-> ServiceFactory.getService(StudentService.class)).thenReturn(mockedService);
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            Mockito.when(request.getPathInfo()).thenReturn(null);
            Mockito.when(request.getQueryString()).thenReturn(null);
            Mockito.when(request.getReader()).thenReturn(new BufferedReader(new FileReader(("src/test/resources/studentServletPost.json"))));
            Mockito.when(request.getMethod()).thenReturn("POST");
            Mockito.when(mockedService.getDataById(Mockito.any())).thenReturn(List.of(mockedStudent));
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
        Student mockedStudent = Mockito.mock(Student.class);
        StudentService mockedService = Mockito.mock(StudentService.class);
        try(MockedStatic<ServiceFactory> mock = Mockito.mockStatic(ServiceFactory.class)) {
            mock.when(()-> ServiceFactory.getService(StudentService.class)).thenReturn(mockedService);
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            Mockito.when(request.getPathInfo()).thenReturn("/1");
            Mockito.when(request.getQueryString()).thenReturn(null);
            Mockito.when(request.getReader()).thenReturn(new BufferedReader(new FileReader(("src/test/resources/studentServletPost.json"))));
            Mockito.when(request.getMethod()).thenReturn("PUT");
            Mockito.when(mockedService.getDataById(Mockito.any())).thenReturn(List.of(mockedStudent));
            Mockito.when(mockedService.mappingVoToDto(Mockito.anyList())).thenReturn(new ArrayList<>());
            Mockito.when(response.getWriter()).thenReturn(writer);
            servlet.doPut(request, response);
            Mockito.verify(mockedService, Mockito.atMostOnce()).update(Mockito.any(), Mockito.any());
        }
    }

    @Test
    @SneakyThrows
    void doDelete() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Student mockedStudent = Mockito.mock(Student.class);
        StudentService mockedService = Mockito.mock(StudentService.class);
        try(MockedStatic<ServiceFactory> mock = Mockito.mockStatic(ServiceFactory.class)) {
            mock.when(()-> ServiceFactory.getService(StudentService.class)).thenReturn(mockedService);
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            Mockito.when(request.getPathInfo()).thenReturn("/1");
            Mockito.when(request.getQueryString()).thenReturn(null);
            Mockito.when(request.getReader()).thenReturn(new BufferedReader(new FileReader(("src/test/resources/studentServletPost.json"))));
            Mockito.when(request.getMethod()).thenReturn("DELETE");
            Mockito.when(mockedService.getDataById(Mockito.any())).thenReturn(List.of(mockedStudent));
            Mockito.when(response.getWriter()).thenReturn(writer);
            servlet.doDelete(request, response);
            Mockito.verify(mockedService, Mockito.atMostOnce()).delete(Mockito.any());
        }
    }

     */
}