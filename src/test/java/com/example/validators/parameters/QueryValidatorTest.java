package com.example.validators.parameters;

import com.example.exeptions.IncorrectRequestException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static com.example.consts.ModelConstants.RQ_ID;
import static org.junit.jupiter.api.Assertions.*;

class QueryValidatorTest {
    HttpServletRequest goodMockedRequest = Mockito.mock(HttpServletRequest.class);
    HttpServletRequest badMockedRequest = Mockito.mock(HttpServletRequest.class);

    @Test
    void validation() {
        Map<String, String[]> goodMap = new HashMap<>();
        String[] value = {"10"};
        Map<String, String[]> badMap = new HashMap<>();
        goodMap.put(RQ_ID, value);
        badMap.put("BadId", value);
        Mockito.when(goodMockedRequest.getParameterMap()).thenReturn(goodMap);
        Mockito.when(badMockedRequest.getParameterMap()).thenReturn(badMap);

        assertThrows(IncorrectRequestException.class, () -> new QueryValidator(badMockedRequest).validate());
        assertDoesNotThrow(() -> new QueryValidator(goodMockedRequest).validate());
    }
}