package com.example.validators.requests;

import com.example.consts.ControlerConstants;
import com.example.consts.ModelConstants;
import com.example.exeptions.IncorrectRequestException;
import com.example.model.vo.Group;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.example.consts.ModelConstants.*;
import static org.junit.jupiter.api.Assertions.*;

class SchedulesValidatorTest {
    HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
    static String[] goodBegin = {"2023-10-20-10-00"};
    static String[] badBegin = {"2023-10-20-11-00"};
    static String[] end = {"2023-10-20-10-30"};
    static Map<String, String[]> goodMap = new HashMap<>();
    static Map<String, String[]> badMap = new HashMap<>();
    static Map<String, String[]> badMap2 = new HashMap<>();
    static Map<String, String[]> badMap3 = new HashMap<>();
    static String[] goodParam = {"10"};
    static String[] badParam = {"10q"};

    @BeforeAll
    static void init() {
        goodMap.put(RQ_BEGIN_DATE_TIME, goodBegin);
        goodMap.put(RQ_END_DATE_TIME, end);
        goodMap.put(RQ_GROUP_ID, goodParam);

        badMap.put(RQ_BEGIN_DATE_TIME, badBegin);
        badMap.put(RQ_END_DATE_TIME, end);
        badMap.put(RQ_GROUP_ID, goodParam);

        badMap2.put(RQ_BEGIN_DATE_TIME, goodBegin);
        badMap2.put(RQ_END_DATE_TIME, end);
        badMap2.put(RQ_GROUP_ID, badParam);

        badMap3.put(RQ_BEGIN_DATE_TIME, goodBegin);
        badMap3.put(RQ_GROUP_ID, goodParam);
    }

    @Test
    void test_reqGetPutDelete_Validation() {
        Mockito.when(mockReq.getMethod()).thenReturn(ControlerConstants.GET);
        Mockito.when(mockReq.getQueryString()).thenReturn(null);
        assertThrows(IncorrectRequestException.class, ()-> new SchedulesValidator(mockReq).validate());
        Mockito.when(mockReq.getParameterMap()).thenReturn(goodMap);
        Mockito.when(mockReq.getQueryString()).thenReturn("someQuery");
        Mockito.when(mockReq.getPathInfo()).thenReturn("somePath");
        assertThrows(IncorrectRequestException.class, ()-> new SchedulesValidator(mockReq).validate());

        Mockito.when(mockReq.getParameterMap()).thenReturn(goodMap);
        Mockito.when(mockReq.getQueryString()).thenReturn("someQuery");
        Mockito.when(mockReq.getPathInfo()).thenReturn(null);
        assertDoesNotThrow(()-> new SchedulesValidator(mockReq).validate());

        Mockito.when(mockReq.getParameterMap()).thenReturn(badMap);
        assertThrows(IncorrectRequestException.class, ()-> new SchedulesValidator(mockReq).validate());

        Mockito.when(mockReq.getParameterMap()).thenReturn(badMap2);
        assertThrows(IncorrectRequestException.class, ()-> new SchedulesValidator(mockReq).validate());

        Mockito.when(mockReq.getParameterMap()).thenReturn(badMap3);
        assertThrows(IncorrectRequestException.class, ()-> new SchedulesValidator(mockReq).validate());
    }
    @Test
    void test_reqPost_Validation() {
        Mockito.when(mockReq.getMethod()).thenReturn(ControlerConstants.POST);
        Mockito.when(mockReq.getQueryString()).thenReturn(null);
        Mockito.when(mockReq.getPathInfo()).thenReturn(null);
        assertDoesNotThrow(()-> new SchedulesValidator(mockReq).validate());

        Mockito.when(mockReq.getQueryString()).thenReturn("someArgs");
        assertThrows(IncorrectRequestException.class, ()-> new SchedulesValidator(mockReq).validate());

        Mockito.when(mockReq.getQueryString()).thenReturn(null);
        Mockito.when(mockReq.getPathInfo()).thenReturn("someArgs");
        assertThrows(IncorrectRequestException.class, ()-> new SchedulesValidator(mockReq).validate());
    }
}