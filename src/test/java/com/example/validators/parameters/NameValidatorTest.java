package com.example.validators.parameters;

import com.example.exeptions.IncorrectRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NameValidatorTest {

    @Test
    void validation() throws IncorrectRequestException {
        String goodFN = "Anton";
        String goodMN = "Ivanovitch";
        String goodSN = "Pupkin";
        String badFN = "qwe21-3d";
        String badMN = "@4Dgfd";
        String badSN = "!#RFSx_1";
        assertDoesNotThrow(() -> new NameValidator(goodFN, goodMN, goodSN).validate());
        assertThrows(IncorrectRequestException.class, () -> new NameValidator(badFN, badMN, badSN).validate());
    }
}