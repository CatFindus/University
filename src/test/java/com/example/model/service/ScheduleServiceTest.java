package com.example.model.service;

import com.example.mapper.ScheduleMapperImpl;

class ScheduleServiceTest {
    /*
    static LocalDateTime begin = LocalDateTime.parse("2023-10-21T10:00:00");
    static LocalDateTime end = LocalDateTime.parse("2023-10-21T11:00:00");
    static Student student = new Student("Ivan", "Vasil'evich", "Groznij", LocalDate.now(), "+79123456789");
    static Teacher teacher = new Teacher("Fedor", "Mikhailovich", "Sumkin", LocalDate.now(), LocalDate.now(), "+79876543210");
    static Group group = new Group("GroupNumber-1");
    static ScheduleUnit scheduleUnit = new ScheduleUnit(begin,end, teacher,group, Subject.CULTURE_STUDIES);
    static Schedule schedule = new Schedule(begin.toLocalDate());
    RepositoryFacade repo = Mockito.mock(RepositoryFacade.class);
    TeacherService teacherService = Mockito.mock(TeacherService.class);
    GroupService groupService = Mockito.mock(GroupService.class);
    ScheduleService service = new ScheduleService(repo, new ScheduleMapperImpl(), groupService, teacherService);
    static Map<String,String[]> map = new HashMap<>();
    static String[] rqBegin = {"2023-10-21-10-00"};
    static String[] rqEnd = {"2023-10-21-11-00"};
    @BeforeAll
    static void init() {
        group.addStudent(student);
        teacher.getSubjects().add(Subject.CULTURE_STUDIES);
        map.put(RQ_BEGIN_DATE_TIME, rqEnd);
        map.put(RQ_END_DATE_TIME, rqBegin);
        schedule.addScheduleUnit(scheduleUnit);
    }
    @Test
    @SneakyThrows
    void getDataByParameters() {
        Mockito.when(repo.getSchedules(Mockito.any(),Mockito.any(), Mockito.any())).thenReturn(List.of(scheduleUnit));
        map=new HashMap<>();
        map.put(RQ_BEGIN_DATE_TIME, rqEnd);
        map.put(RQ_END_DATE_TIME, rqBegin);
        assertThrows(IncorrectRequestException.class, ()-> service.getDataByParameters(map));
        map = new HashMap<>();
        map.put(RQ_BEGIN_DATE_TIME, rqBegin);
        map.put(RQ_END_DATE_TIME, rqEnd);
        assertEquals(scheduleUnit, service.getDataByParameters(map).get(0));
    }

    @Test
    void mappingVoToDto() {
        List<DtoResponse> responses = service.mappingVoToDto(List.of(scheduleUnit));
        ScheduleUnitResponse response = (ScheduleUnitResponse) responses.get(0);
        assertEquals(scheduleUnit.getBegin(), response.getBegin());
        assertEquals(scheduleUnit.getEnd(), response.getEnd());
    }

    @Test
    void create() {
        Mockito.when(repo.addSchedule(schedule)).thenReturn(true);
        Mockito.when(repo.addScheduleUnit(scheduleUnit)).thenReturn(false);
        assertTrue(service.create(schedule));
        assertFalse(service.create(scheduleUnit));
    }

    @Test
    @SneakyThrows
    void update() {
        ScheduleUnitRequest sur = new ScheduleUnitRequest();
        sur.setEnd(end.plusHours(1));
        sur.setBegin(begin);
        sur.setGroupId(group.getId());
        sur.setTeacherId(teacher.getId());
        Mockito.when(repo.getSchedules(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new ArrayList<>());
        map = new HashMap<>();
        map.put(RQ_BEGIN_DATE_TIME, rqEnd);
        map.put(RQ_END_DATE_TIME, rqBegin);
        assertThrows(IncorrectRequestException.class, ()-> service.update(map, sur));
        map = new HashMap<>();
        map.put(RQ_BEGIN_DATE_TIME, rqBegin);
        map.put(RQ_END_DATE_TIME, rqEnd);
        assertThrows(NoDataException.class, ()-> service.update(map, sur));
        Mockito.when(repo.getSchedules(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(List.of(scheduleUnit));
        Mockito.when(repo.getScheduleByUnit(scheduleUnit)).thenReturn(schedule);
        Mockito.when(groupService.getDataById(Mockito.any())).thenReturn(List.of(group));
        Mockito.when(teacherService.getDataById(Mockito.any())).thenReturn(List.of(teacher));
        Mockito.when(repo.addScheduleUnit(Mockito.any())).thenReturn(true);
        schedule.addScheduleUnit(scheduleUnit);
        ScheduleUnitResponse unit = (ScheduleUnitResponse) service.update(map, sur).get(0);
        assertEquals(scheduleUnit.getBegin(), unit.getBegin());
        assertEquals(scheduleUnit.getEnd().plusHours(1), unit.getEnd());
    }

    @Test
    void delete() {
        Mockito.when(repo.getSchedules(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new ArrayList<>());
        assertThrows(IncorrectRequestException.class, ()-> service.delete(map));
        Mockito.when(repo.getSchedules(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(List.of(scheduleUnit));
        schedule.addScheduleUnit(scheduleUnit);
        Mockito.when(repo.getScheduleByUnit(scheduleUnit)).thenReturn(schedule);
        map = new HashMap<>();
        map.put(RQ_BEGIN_DATE_TIME, rqBegin);
        map.put(RQ_END_DATE_TIME, rqEnd);
        assertDoesNotThrow(()-> service.delete(map));
        assertTrue(schedule.getSchedules().isEmpty());
    }

    @Test
    void getScheduleForDate() {
        schedule.addScheduleUnit(scheduleUnit);
        Mockito.when(repo.getSchedule(Mockito.any())).thenReturn(schedule);
        assertEquals(schedule, service.getScheduleForDate(LocalDate.now()));
    }

     */
}