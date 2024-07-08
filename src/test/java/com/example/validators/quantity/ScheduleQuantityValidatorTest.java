package com.example.validators.quantity;

import com.example.model.entities.Schedule;

class ScheduleQuantityValidatorTest {
    /*
    HttpServletRequest mockPostReq = Mockito.mock(HttpServletRequest.class);
    HttpServletRequest mockDeleteReq = Mockito.mock(HttpServletRequest.class);
    Schedule mockedSchedule = Mockito.mock(Schedule.class);
    List<ScheduleUnit> mockedList = Mockito.mock(List.class);
    static int max, min;
    static Properties properties = new Properties();
    @BeforeAll
    static void init() {
        try (InputStream is = Objects.requireNonNull(Student.class.getClassLoader().getResource(MODEL_PROPERTY)).openStream()) {
            properties.load(is);
        } catch (IOException ignored) {}
        max = Integer.parseInt(properties.getProperty(MAX_CLASSES_FOR_GROUP));
        min = Integer.parseInt(properties.getProperty(MIN_CLASSES_FOR_GROUP));
    }
    @Test
    void testMaxCountValidation() {
        Mockito.when(mockPostReq.getMethod()).thenReturn(ControlerConstants.POST);
        Mockito.when(mockedSchedule.getClassesByGroup(Mockito.any())).thenReturn(mockedList);
        Mockito.when(mockedList.size()).thenReturn(max);
        assertThrows(IncorrectRequestException.class, ()-> new ScheduleQuantityValidator(mockPostReq, new Group() ,mockedSchedule).validate());
        Mockito.when(mockedList.size()).thenReturn(max-1);
        assertDoesNotThrow(() -> new ScheduleQuantityValidator(mockPostReq, new Group(), mockedSchedule));
    }
    @Test
    void testMinCountValidation() {
        Mockito.when(mockDeleteReq.getMethod()).thenReturn(ControlerConstants.DELETE);
        Mockito.when(mockedSchedule.getClassesByGroup(Mockito.any())).thenReturn(mockedList);
        Mockito.when(mockedList.size()).thenReturn(min);
        assertThrows(IncorrectRequestException.class, ()-> new ScheduleQuantityValidator(mockDeleteReq, new Group() ,mockedSchedule).validate());
        Mockito.when(mockedList.size()).thenReturn(min+1);
        assertDoesNotThrow(() -> new ScheduleQuantityValidator(mockDeleteReq, new Group(), mockedSchedule));
    }

     */
}