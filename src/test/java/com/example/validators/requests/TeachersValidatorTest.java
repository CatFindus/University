package com.example.validators.requests;

import com.example.consts.ControlerConstants;
import com.example.consts.ModelConstants;
import com.example.exeptions.IncorrectRequestException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static com.example.consts.ModelConstants.RQ_ID;
import static com.example.consts.ModelConstants.RQ_SUBJECT;
import static org.junit.jupiter.api.Assertions.*;

class TeachersValidatorTest {
    /*

    HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
    String emptyPath = ModelConstants.EMPTY;
    String goodPath = "/1";
    String badPath = "/badPath";
    static Map<String, String[]> goodMap = new HashMap<>();
    static Map<String, String[]> badMap = new HashMap<>();
    static String[] goodParam = {"hightmath"};
    static String[] badParam = {"theology"};

    @BeforeAll
    static void init() {
        goodMap.put(RQ_SUBJECT, goodParam);
        badMap.put(RQ_SUBJECT, badParam);
    }

    @Test
    void test_GetReq_Validation_Path() {
        Mockito.when(mockReq.getMethod()).thenReturn(ControlerConstants.GET);
        Mockito.when(mockReq.getPathInfo()).thenReturn(badPath);
        assertThrows(IncorrectRequestException.class, () -> new TeachersValidator(mockReq).validate());
        Mockito.when(mockReq.getPathInfo()).thenReturn(goodPath);
        assertDoesNotThrow(() -> new TeachersValidator(mockReq).validate());
    }

    @Test
    void test_GetReq_Validation_Query() {
        Mockito.when(mockReq.getMethod()).thenReturn(ControlerConstants.GET);
        Mockito.when(mockReq.getPathInfo()).thenReturn(null);
        Mockito.when(mockReq.getQueryString()).thenReturn("someQuery");
        Mockito.when(mockReq.getParameterMap()).thenReturn(badMap);
        assertThrows(IncorrectRequestException.class, () -> new TeachersValidator(mockReq).validate());
        Mockito.when(mockReq.getParameterMap()).thenReturn(goodMap);
        assertDoesNotThrow(() -> new TeachersValidator(mockReq).validate());
    }

    @Test
    void test_PostReq_Validation() {
        Mockito.when(mockReq.getMethod()).thenReturn(ControlerConstants.POST);
        Mockito.when(mockReq.getPathInfo()).thenReturn(null);
        Mockito.when(mockReq.getQueryString()).thenReturn(null);
        assertDoesNotThrow(() -> new TeachersValidator(mockReq).validate());
        Mockito.when(mockReq.getQueryString()).thenReturn("SomeQuery");
        assertThrows(IncorrectRequestException.class, () -> new TeachersValidator(mockReq).validate());
        Mockito.when(mockReq.getQueryString()).thenReturn(null);
        Mockito.when(mockReq.getPathInfo()).thenReturn(goodPath);
        assertThrows(IncorrectRequestException.class, () -> new TeachersValidator(mockReq).validate());
        Mockito.when(mockReq.getQueryString()).thenReturn("SomeQuery");
        Mockito.when(mockReq.getPathInfo()).thenReturn(goodPath);
        Mockito.when(mockReq.getParameterMap()).thenReturn(badMap);
        assertThrows(IncorrectRequestException.class, () -> new TeachersValidator(mockReq).validate());
        Mockito.when(mockReq.getParameterMap()).thenReturn(goodMap);
        assertDoesNotThrow(() -> new TeachersValidator(mockReq).validate());
    }

    @Test
    void test_PutReq_Validation() {
        Mockito.when(mockReq.getMethod()).thenReturn(ControlerConstants.PUT);
        Mockito.when(mockReq.getPathInfo()).thenReturn(goodPath);
        Mockito.when(mockReq.getQueryString()).thenReturn(null);
        assertDoesNotThrow(() -> new GroupsValidator(mockReq).validate());
        Mockito.when(mockReq.getPathInfo()).thenReturn(badPath);
        assertThrows(IncorrectRequestException.class, () -> new TeachersValidator(mockReq).validate());
    }

    @Test
    void test_DeleteReq_Validation() {
        Mockito.when(mockReq.getMethod()).thenReturn(ControlerConstants.PUT);
        Mockito.when(mockReq.getPathInfo()).thenReturn(goodPath);
        Mockito.when(mockReq.getQueryString()).thenReturn(null);
        assertDoesNotThrow(() -> new GroupsValidator(mockReq).validate());

        Mockito.when(mockReq.getPathInfo()).thenReturn(badPath);
        assertThrows(IncorrectRequestException.class, () -> new TeachersValidator(mockReq).validate());

        Mockito.when(mockReq.getQueryString()).thenReturn("SomeQuery");
        Mockito.when(mockReq.getPathInfo()).thenReturn(badPath);
        Mockito.when(mockReq.getParameterMap()).thenReturn(badMap);
        assertThrows(IncorrectRequestException.class, () -> new TeachersValidator(mockReq).validate());
        Mockito.when(mockReq.getPathInfo()).thenReturn(goodPath);
        Mockito.when(mockReq.getParameterMap()).thenReturn(goodMap);
        assertDoesNotThrow(() -> new GroupsValidator(mockReq).validate());
    }

     */
}