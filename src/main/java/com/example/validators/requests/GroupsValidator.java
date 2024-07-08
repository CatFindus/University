package com.example.validators.requests;

import com.example.exeptions.IncorrectRequestException;
import com.example.validators.Validators;
import com.example.validators.parameters.PathValidator;
import com.example.validators.parameters.QueryValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.example.consts.ControlerConstants.*;
import static com.example.consts.LoggerConstants.*;
import static com.example.consts.ModelConstants.*;

@AllArgsConstructor
public class GroupsValidator extends Validators {
    private static final Logger logger = LoggerFactory.getLogger(GroupsValidator.class);
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
        if (req.getPathInfo() != null) {
            try {
                new PathValidator(req.getPathInfo()).validate();
            } catch (NumberFormatException e) {
                logger.trace(END_VALIDATION_UNSUCCESSFUL, e.getMessage());
                throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
            }
        } else {
            for (String key : req.getParameterMap().keySet())
                if (!GROUP_REQUEST_PARAMETERS.contains(key))
                    throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
            new QueryValidator(req).validate();
        }
    }

    private void doPostValidation(HttpServletRequest req) throws IncorrectRequestException {
        boolean haveNoArgs = req.getPathInfo() == null && req.getQueryString() == null;
        if (haveNoArgs) return;
        boolean haveQueryArgs = req.getQueryString() != null;
        String[] paths = req.getPathInfo().replaceFirst(PATH_SEPARATOR, EMPTY).split(PATH_SEPARATOR);
        try {
            if (haveQueryArgs) {
                if (paths.length != 1) throw new IncorrectRequestException();
                Integer.parseInt(paths[0]);
                Map<String, String[]> parameterMap = req.getParameterMap();
                if (parameterMap.size() != 1 || !parameterMap.containsKey(RQ_STUDENT_ID))
                    throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
                for (String value : parameterMap.get(RQ_STUDENT_ID)) Integer.parseInt(value);
            } else {
                if (paths.length <= 1) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
                for (String path : paths) Integer.parseInt(path);
            }
        } catch (NumberFormatException e) {
            logger.trace(END_VALIDATION_UNSUCCESSFUL, e.getMessage());
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }
    }

    private void doPutValidation(HttpServletRequest req) throws IncorrectRequestException {
        if (req.getPathInfo() == null) {
            throw new IncorrectRequestException(INCORRECT_PATH_FORMAT);
        }
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
                if (parameterMap.size() != 1 || !parameterMap.containsKey(RQ_STUDENT_ID))
                    throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
                for (String id:parameterMap.get(RQ_STUDENT_ID)) Integer.parseInt(id);
            } catch (NumberFormatException e) {
                logger.trace(END_VALIDATION_UNSUCCESSFUL, e.getMessage());
                throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
            }
        } else {
            new PathValidator(req.getPathInfo()).validate();

        }
    }
}
