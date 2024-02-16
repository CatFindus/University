package com.example.validators.requests;

import com.example.exeptions.IncorrectRequestException;
import com.example.validators.Validators;
import com.example.validators.parameters.PathValidator;
import com.example.validators.parameters.QueryValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeParseException;

import static com.example.consts.ControlerConstants.*;
import static com.example.consts.LoggerConstants.*;
import static com.example.consts.LoggerConstants.END_VALIDATION_UNSUCCESSFUL;
import static com.example.consts.ModelConstants.*;
import static com.example.consts.ModelConstants.INCORRECT_DATE_FORMAT;

@AllArgsConstructor
public class StudentsValidator extends Validators {
    private static final Logger logger = LoggerFactory.getLogger(StudentsValidator.class);
    private HttpServletRequest req;

    @Override
    protected void validation() throws IncorrectRequestException {
        logger.trace(START_VALIDATION);
        String method = req.getMethod();
        switch (method) {
            case GET -> doGetValidation(req);
            case POST -> doPostValidation(req);
            case PUT, DELETE -> doPutDeleteValidation(req);
            default -> throw new IncorrectRequestException(UNKNOWN_ERROR);
        }
        logger.trace(END_VALIDATION_SUCCESSFUL);
    }

    private void doGetValidation(HttpServletRequest req) throws IncorrectRequestException {
        String path = req.getPathInfo();
        String query = req.getQueryString();
        try {
            if (path != null && query != null) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
            else if (path != null) new PathValidator(path).validate();
            else if (query != null) {
                for (String key : req.getParameterMap().keySet())
                    if (!STUDENT_REQUEST_PARAMETERS.contains(key))
                        throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
                new QueryValidator(req).validate();
            }
        } catch (NumberFormatException e) {
            logger.trace(END_VALIDATION_UNSUCCESSFUL, e.getMessage());
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        } catch (DateTimeParseException e) {
            logger.trace(END_VALIDATION_UNSUCCESSFUL, e.getMessage());
            throw new IncorrectRequestException(INCORRECT_DATE_FORMAT);
        }
    }

    private void doPostValidation(HttpServletRequest req) throws IncorrectRequestException {
        if (req.getPathInfo() != null || req.getQueryString() != null)
            throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
    }

    private void doPutDeleteValidation(HttpServletRequest req) throws IncorrectRequestException {
        if (req.getPathInfo() == null || req.getQueryString() != null)
            throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
        try {
            new PathValidator(req.getPathInfo()).validate();
        } catch (NumberFormatException e) {
            logger.trace(END_VALIDATION_UNSUCCESSFUL, e.getMessage());
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }

    }
}
