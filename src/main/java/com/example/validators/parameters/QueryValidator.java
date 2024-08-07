package com.example.validators.parameters;

import com.example.exeptions.IncorrectRequestException;
import com.example.validators.Validators;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

import static com.example.consts.ControlerConstants.BIRTHDAY_INCORRECT;
import static com.example.consts.ControlerConstants.INCORRECT_REQUEST_ARGS;
import static com.example.consts.LoggerConstants.*;
import static com.example.consts.LoggerConstants.START_VALIDATION;
import static com.example.consts.ModelConstants.*;

@AllArgsConstructor
public class QueryValidator extends Validators {
    private static final Logger logger = LoggerFactory.getLogger(QueryValidator.class);
    private final HttpServletRequest req;

    @Override
    protected void validation() throws IncorrectRequestException {
        logger.trace(START_VALIDATION);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PARAM_PATTERN);
        Map<String, String[]> parameterMap = req.getParameterMap();
        try {
            LocalDateTime begin = null, end = null;
            for (String key : parameterMap.keySet()) {
                String value = parameterMap.get(key)[0];
                switch (key) {
                    case RQ_BEGIN_DATE_TIME -> begin = LocalDateTime.parse(value, formatter);
                    case RQ_END_DATE_TIME -> end = LocalDateTime.parse(value, formatter);
                    case RQ_SUBJECT, RQ_NAME, RQ_REQUEST_NAME -> subjectValidation(key, value);
                    case RQ_STUDENT_ID, RQ_GROUP_ID, RQ_TEACHER_ID, RQ_EXPERIENCE, RQ_ID, RQ_LIMIT, RQ_OFFSET ->
                            idValidation(parameterMap.get(key));
                    case RQ_BIRTHDAY -> birthdayValidation(value);
                    case RQ_FIRST_NAME, RQ_MIDDLE_NAME, RQ_SURNAME -> {
                        if (value == null) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
                    }
                    case RQ_PHONE_NUMBER -> new PhoneValidator(value);
                    case RQ_NUMBER, RQ_GROUP_NUMBER -> groupNumberValidation(parameterMap.get(key));
                    default -> throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
                }
            }
            if (begin != null && end != null && begin.isAfter(end))
                throw new IncorrectRequestException(INCORRECT_DATE_FORMAT);
            logger.trace(END_VALIDATION_SUCCESSFUL);
        } catch (DateTimeParseException e) {
            logger.trace(END_VALIDATION_UNSUCCESSFUL, e.getMessage());
            throw new IncorrectRequestException(INCORRECT_DATE_FORMAT);
        } catch (NumberFormatException e) {
            logger.trace(END_VALIDATION_UNSUCCESSFUL, e.getMessage());
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }
    }

    private void groupNumberValidation(String[] strings) throws IncorrectRequestException {
        if (strings == null || strings.length != 1) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
    }

    private void birthdayValidation(String value) throws IncorrectRequestException {
        LocalDate birthday = LocalDate.parse(value);
        Period period = Period.between(birthday, LocalDate.now());
        if (period.getYears() < 18 || period.getYears() > 90) throw new IncorrectRequestException(BIRTHDAY_INCORRECT);
    }

    private void idValidation(String[] parameters) throws IncorrectRequestException {
        if (parameters.length != 1) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
        try {
            Long.parseLong(parameters[0]);
        } catch (NumberFormatException e) {
            logger.trace(END_VALIDATION_UNSUCCESSFUL, e.getMessage());
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }

    }

    private void subjectValidation(String key, String value) throws IncorrectRequestException {
        new SubjectValidator(key, value).validate();
    }
}
