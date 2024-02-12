package com.example.validators.requests;

import com.example.exeptions.IncorrectRequestException;
import com.example.validators.Validators;
import com.example.validators.parameters.QueryValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

import static com.example.consts.ControlerConstants.*;
import static com.example.consts.LoggerConstants.*;
import static com.example.consts.LoggerConstants.END_VALIDATION_UNSUCCESSFUL;
import static com.example.consts.ModelConstants.*;

@AllArgsConstructor
public class SchedulesValidator extends Validators {
    private static final Logger logger = LoggerFactory.getLogger(SchedulesValidator.class);
    private HttpServletRequest req;

    @Override
    protected void validation() throws IncorrectRequestException {
        logger.trace(START_VALIDATION);
        String method = req.getMethod();
        switch (method) {
            case GET, DELETE, PUT -> doGetValidation(req);
            case POST -> doPostValidation(req);
            default -> throw new IncorrectRequestException(UNKNOWN_ERROR);
        }
        logger.trace(END_VALIDATION_SUCCESSFUL);
    }

    private void doGetValidation(HttpServletRequest req) throws IncorrectRequestException {
        if (req.getQueryString() == null) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
        Map<String, String[]> parameterMap = req.getParameterMap();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PARAM_PATTERN);
        if (req.getPathInfo() != null || parameterMap.size() < 2)
            throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
        try {
            if (parameterMap.get(RQ_BEGIN_DATE_TIME) == null || parameterMap.get(RQ_END_DATE_TIME) == null)
                throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
            LocalDateTime begin = LocalDateTime.parse(parameterMap.get(RQ_BEGIN_DATE_TIME)[0], formatter);
            LocalDateTime end = LocalDateTime.parse(parameterMap.get(RQ_END_DATE_TIME)[0], formatter);
            if (begin.isAfter(end))
                throw new IncorrectRequestException(INCORRECT_DATE_FORMAT);
            for (String key : parameterMap.keySet())
                if (!SCHEDULE_REQUEST_PARAMETERS.contains(key))
                    throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
            new QueryValidator(req).validate();
        } catch (DateTimeParseException e) {
            logger.trace(END_VALIDATION_UNSUCCESSFUL, e.getMessage());
            throw new IncorrectRequestException(INCORRECT_DATE_FORMAT);
        } catch (NumberFormatException e) {
            logger.trace(END_VALIDATION_UNSUCCESSFUL, e.getMessage());
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }

    }

    private void doPostValidation(HttpServletRequest req) throws IncorrectRequestException {
        logger.trace(START_VALIDATION);
        if (req.getPathInfo() != null || req.getQueryString() != null)
            throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
        logger.trace(END_VALIDATION_SUCCESSFUL);
    }

}
