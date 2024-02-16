package com.example.validators.requests;

import com.example.exeptions.IncorrectRequestException;
import com.example.model.vo.Subject;
import com.example.validators.Validators;
import com.example.validators.parameters.PathValidator;
import com.example.validators.parameters.QueryValidator;
import com.example.validators.parameters.SubjectValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.example.consts.ControlerConstants.*;
import static com.example.consts.LoggerConstants.*;
import static com.example.consts.ModelConstants.*;

@AllArgsConstructor
public class TeachersValidator extends Validators {
    private static final Logger logger = LoggerFactory.getLogger(TeachersValidator.class);
    private HttpServletRequest req;

    @Override
    protected void validation() throws IncorrectRequestException {
        logger.trace(START_VALIDATION);
        String method = req.getMethod();
        switch (method) {
            case GET -> doGetValidation(req);
            case POST -> doPostValidation(req);
            case PUT -> doPutValidation(req);
            case DELETE -> doDeleteValidation(req);
            default -> throw new IncorrectRequestException(UNKNOWN_ERROR);
        }
        logger.trace(END_VALIDATION_SUCCESSFUL);
    }

    private void doGetValidation(HttpServletRequest req) throws IncorrectRequestException {
        String path = req.getPathInfo();
        String query = req.getQueryString();
        if (path != null && query != null) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
        else if (path != null) new PathValidator(path).validate();
        else if (query != null) {
            for (String key : req.getParameterMap().keySet())
                if (!TEACHER_REQUEST_PARAMETERS.contains(key))
                    throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
            new QueryValidator(req).validate();
        }
    }

    private void doPostValidation(HttpServletRequest req) throws IncorrectRequestException {
        boolean haveAllArgs = req.getPathInfo() != null && req.getQueryString() != null;
        boolean haveNoArgs = req.getPathInfo() == null && req.getQueryString() == null;
        if (!(haveAllArgs || haveNoArgs)) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
        else if (req.getPathInfo() != null && req.getQueryString() != null) {
            try {
                new PathValidator(req.getPathInfo()).validate();
                Map<String, String[]> parameterMap = req.getParameterMap();
                if (parameterMap.size() != 1 || !parameterMap.containsKey(SUBJECT_PARAMETER))
                    throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
                for (String value : req.getParameterMap().get(SUBJECT_PARAMETER))
                    new SubjectValidator(value).validate();
            } catch (NumberFormatException e) {
                logger.trace(END_VALIDATION_UNSUCCESSFUL, e.getMessage());
                throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
            }
        }
    }

    private void doPutValidation(HttpServletRequest req) throws IncorrectRequestException {
        if (req.getPathInfo() == null) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
        try {
            Integer.parseInt(req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY));
        } catch (NumberFormatException e) {
            logger.trace(END_VALIDATION_UNSUCCESSFUL, e.getMessage());
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }
    }

    private void doDeleteValidation(HttpServletRequest req) throws IncorrectRequestException {
        if (req.getPathInfo() == null) throw new IncorrectRequestException(INCORRECT_PATH_FORMAT);
        if (req.getQueryString() != null) {
            try {
                Integer.parseInt(req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY));
                Map<String, String[]> parameterMap = req.getParameterMap();
                if (parameterMap.size() != 1 || !parameterMap.containsKey(SUBJECT_PARAMETER))
                    throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
                if (!Subject.containRequestName(req.getParameterMap().get(SUBJECT_PARAMETER)[0]))
                    throw new IncorrectRequestException(INCORRECT_BODY_OF_REQUEST);
            } catch (NumberFormatException e) {
                logger.trace(END_VALIDATION_UNSUCCESSFUL, e.getMessage());
                throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
            }
        }
    }
}
