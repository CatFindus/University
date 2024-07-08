package com.example.controller.servlets;

import com.example.model.entities.Schedule;

class SchedulesServletTest {
    /*
    SchedulesServlet servlet = new SchedulesServlet();

    @Test
    @SneakyThrows
    void doGet() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        ScheduleService mockedService = Mockito.mock(ScheduleService.class);
        Schedule schedule = Mockito.mock(Schedule.class);
        ScheduleUnit scheduleUnit = Mockito.mock(ScheduleUnit.class);
        Map<String, String[]> map = new HashMap<>();
        String[] begin = {"2020-10-11-10-00"};
        String[] end = {"2020-10-11-11-00"};
        String[] id = {"1"};
        String[] subject = {"hightmath"};
        map.put(RQ_BEGIN_DATE_TIME, begin);
        map.put(RQ_END_DATE_TIME, end);
        map.put(RQ_GROUP_ID, id);
        map.put(RQ_TEACHER_ID, id);
        map.put(RQ_SUBJECT, subject);
        try(MockedStatic<ServiceFactory> mock = Mockito.mockStatic(ServiceFactory.class)) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            mock.when(()-> ServiceFactory.getService(ScheduleService.class)).thenReturn(mockedService);
            Mockito.when(request.getPathInfo()).thenReturn(null);
            Mockito.when(request.getQueryString()).thenReturn("someString");
            Mockito.when(request.getMethod()).thenReturn("GET");
            Mockito.when(request.getParameterMap()).thenReturn(map);
            Mockito.when(response.getWriter()).thenReturn(writer);
            servlet.doGet(request,response);
            Mockito.verify(mockedService, Mockito.atMostOnce()).getDataByParameters(Mockito.anyMap());
        }
    }

    @Test
    @SneakyThrows
    void doPost() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        ScheduleService mockedService = Mockito.mock(ScheduleService.class);
        TeacherService tServiceMocked = Mockito.mock(TeacherService.class);
        GroupService gServiceMocked = Mockito.mock(GroupService.class);
        Schedule schedule = Mockito.mock(Schedule.class);
        ScheduleUnit scheduleUnit = Mockito.mock(ScheduleUnit.class);
        Group group = Mockito.mock(Group.class);
        Teacher teacher = Mockito.mock(Teacher.class);
        Map<String, String[]> map = new HashMap<>();
        String[] begin = {"2020-10-11-10-00"};
        String[] end = {"2020-10-11-11-00"};
        String[] id = {"1"};
        String[] subject = {"hightmath"};
        map.put(RQ_BEGIN_DATE_TIME, begin);
        map.put(RQ_END_DATE_TIME, end);
        map.put(RQ_GROUP_ID, id);
        map.put(RQ_TEACHER_ID, id);
        map.put(RQ_SUBJECT, subject);
        try(MockedStatic<ServiceFactory> mock = Mockito.mockStatic(ServiceFactory.class)) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            mock.when(()-> ServiceFactory.getService(ScheduleService.class)).thenReturn(mockedService);
            mock.when(()-> ServiceFactory.getService(TeacherService.class)).thenReturn(tServiceMocked);
            mock.when(()-> ServiceFactory.getService(GroupService.class)).thenReturn(gServiceMocked);
            Mockito.when(request.getReader()).thenReturn(new BufferedReader(new FileReader(("src/test/resources/schedulesServletPost.json"))));
            Mockito.when(mockedService.getScheduleForDate(Mockito.any())).thenReturn(schedule);
            Mockito.when(tServiceMocked.getDataById(Mockito.any())).thenReturn(List.of(teacher));
            Mockito.when(gServiceMocked.getDataById(Mockito.any())).thenReturn(List.of(group));
            Mockito.when(request.getPathInfo()).thenReturn(null);
            Mockito.when(request.getQueryString()).thenReturn(null);
            Mockito.when(request.getMethod()).thenReturn("POST");
            Mockito.when(request.getParameterMap()).thenReturn(map);
            Mockito.when(response.getWriter()).thenReturn(writer);
            servlet.doPost(request,response);
            Mockito.verify(mockedService, Mockito.atMostOnce()).create(Mockito.any());
        }
    }

    @Test
    @SneakyThrows
    void doPut() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        ScheduleService mockedService = Mockito.mock(ScheduleService.class);
        TeacherService tServiceMocked = Mockito.mock(TeacherService.class);
        GroupService gServiceMocked = Mockito.mock(GroupService.class);
        Schedule schedule = Mockito.mock(Schedule.class);
        ScheduleUnit scheduleUnit = Mockito.mock(ScheduleUnit.class);
        Group group = Mockito.mock(Group.class);
        Teacher teacher = Mockito.mock(Teacher.class);
        Map<String, String[]> map = new HashMap<>();
        String[] begin = {"2020-10-11-10-00"};
        String[] end = {"2020-10-11-11-00"};
        String[] id = {"1"};
        String[] subject = {"hightmath"};
        map.put(RQ_BEGIN_DATE_TIME, begin);
        map.put(RQ_END_DATE_TIME, end);
        map.put(RQ_GROUP_ID, id);
        map.put(RQ_TEACHER_ID, id);
        map.put(RQ_SUBJECT, subject);
        try(MockedStatic<ServiceFactory> mock = Mockito.mockStatic(ServiceFactory.class)) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            mock.when(()-> ServiceFactory.getService(ScheduleService.class)).thenReturn(mockedService);
            mock.when(()-> ServiceFactory.getService(TeacherService.class)).thenReturn(tServiceMocked);
            mock.when(()-> ServiceFactory.getService(GroupService.class)).thenReturn(gServiceMocked);
            Mockito.when(request.getReader()).thenReturn(new BufferedReader(new FileReader(("src/test/resources/schedulesServletPost.json"))));
            Mockito.when(mockedService.getScheduleForDate(Mockito.any())).thenReturn(schedule);
            Mockito.when(tServiceMocked.getDataById(Mockito.any())).thenReturn(List.of(teacher));
            Mockito.when(gServiceMocked.getDataById(Mockito.any())).thenReturn(List.of(group));
            Mockito.when(request.getPathInfo()).thenReturn(null);
            Mockito.when(request.getQueryString()).thenReturn("someString");
            Mockito.when(request.getMethod()).thenReturn("PUT");
            Mockito.when(request.getParameterMap()).thenReturn(map);
            Mockito.when(response.getWriter()).thenReturn(writer);
            servlet.doPut(request,response);
            Mockito.verify(mockedService, Mockito.atMostOnce()).update(Mockito.any(), Mockito.any());
        }
    }

    @Test
    @SneakyThrows
    void doDelete() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        ScheduleService mockedService = Mockito.mock(ScheduleService.class);
        TeacherService tServiceMocked = Mockito.mock(TeacherService.class);
        GroupService gServiceMocked = Mockito.mock(GroupService.class);
        Schedule schedule = Mockito.mock(Schedule.class);
        ScheduleUnit scheduleUnit = Mockito.mock(ScheduleUnit.class);
        Group group = Mockito.mock(Group.class);
        Teacher teacher = Mockito.mock(Teacher.class);
        Map<String, String[]> map = new HashMap<>();
        String[] begin = {"2020-10-11-10-00"};
        String[] end = {"2020-10-11-11-00"};
        String[] id = {"1"};
        String[] subject = {"hightmath"};
        map.put(RQ_BEGIN_DATE_TIME, begin);
        map.put(RQ_END_DATE_TIME, end);
        map.put(RQ_GROUP_ID, id);
        map.put(RQ_TEACHER_ID, id);
        map.put(RQ_SUBJECT, subject);
        try(MockedStatic<ServiceFactory> mock = Mockito.mockStatic(ServiceFactory.class)) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            mock.when(()-> ServiceFactory.getService(ScheduleService.class)).thenReturn(mockedService);
            mock.when(()-> ServiceFactory.getService(TeacherService.class)).thenReturn(tServiceMocked);
            mock.when(()-> ServiceFactory.getService(GroupService.class)).thenReturn(gServiceMocked);
            Mockito.when(request.getReader()).thenReturn(new BufferedReader(new FileReader(("src/test/resources/schedulesServletPost.json"))));
            Mockito.when(mockedService.getScheduleForDate(Mockito.any())).thenReturn(schedule);
            Mockito.when(mockedService.getDataByParameters(Mockito.anyMap())).thenReturn(List.of(scheduleUnit));
            Mockito.when(tServiceMocked.getDataById(Mockito.any())).thenReturn(List.of(teacher));
            Mockito.when(gServiceMocked.getDataById(Mockito.any())).thenReturn(List.of(group));
            Mockito.when(request.getPathInfo()).thenReturn(null);
            Mockito.when(request.getQueryString()).thenReturn("someString");
            Mockito.when(request.getMethod()).thenReturn("DELETE");
            Mockito.when(request.getParameterMap()).thenReturn(map);
            Mockito.when(response.getWriter()).thenReturn(writer);
            servlet.doDelete(request,response);
            Mockito.verify(mockedService, Mockito.atMostOnce()).delete(Mockito.any());
        }
    }

     */
}