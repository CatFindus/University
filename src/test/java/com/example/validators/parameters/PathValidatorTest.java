package com.example.validators.parameters;

import com.example.exeptions.IncorrectRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PathValidatorTest {

    @Test
    void validation() {
        String goodPath = "/1/2/3";
        String badPath = "/ss";
        assertThrows(IncorrectRequestException.class, ()-> new PathValidator(badPath).validate());
        assertDoesNotThrow(()-> new PathValidator(goodPath).validate());
    }
}