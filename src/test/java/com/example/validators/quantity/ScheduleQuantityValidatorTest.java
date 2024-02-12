package com.example.validators.quantity;

import com.example.consts.ControlerConstants;
import com.example.exeptions.IncorrectRequestException;
import com.example.model.vo.Group;
import com.example.model.vo.Schedule;
import com.example.model.vo.ScheduleUnit;
import com.example.model.vo.Student;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import static com.example.consts.ModelConstants.*;
import static org.junit.jupiter.api.Assertions.*;

class ScheduleQuantityValidatorTest {
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
}