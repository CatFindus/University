package com.example.validators.parameters;

import com.example.exeptions.IncorrectRequestException;
import com.example.validators.Validators;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.consts.ControlerConstants.INCORRECT_REQUEST_ARGS;
import static com.example.consts.ControlerConstants.NAME_REGEXP;
import static com.example.consts.LoggerConstants.*;
import static com.example.consts.ModelConstants.RQ_NAME;

@AllArgsConstructor
public class SubjectValidator extends Validators {
    private static final Logger logger = LoggerFactory.getLogger(SubjectValidator.class);
    private String key;
    private String value;

    @Override
    protected void validation() throws IncorrectRequestException {
        logger.trace(START_VALIDATION);
        if (value==null) {
            logger.trace(END_VALIDATION_UNSUCCESSFUL, key);
            throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
        }
        Pattern pattern = Pattern.compile(NAME_REGEXP, Pattern.UNICODE_CHARACTER_CLASS);
        Matcher matcher;
        switch (key) {
            case RQ_NAME ->
        }
        logger.trace(END_VALIDATION_SUCCESSFUL);

    }
}
