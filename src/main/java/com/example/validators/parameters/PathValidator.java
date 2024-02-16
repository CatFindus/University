package com.example.validators.parameters;

import com.example.exeptions.IncorrectRequestException;
import com.example.validators.Validators;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.example.consts.LoggerConstants.*;
import static com.example.consts.ModelConstants.*;

@AllArgsConstructor
public class PathValidator extends Validators {
    private static final Logger logger = LoggerFactory.getLogger(PathValidator.class);
    private final String path;

    @Override
    protected void validation() throws IncorrectRequestException {
        logger.trace(START_VALIDATION);
        if (path == null) {
            logger.trace(END_VALIDATION_UNSUCCESSFUL, INCORRECT_PATH_FORMAT);
            throw new IncorrectRequestException(INCORRECT_PATH_FORMAT);
        }
        String[] params = path.replaceFirst(PATH_SEPARATOR, EMPTY).split(PATH_SEPARATOR);
        try {
            for (String param : params) Integer.parseInt(param);
        } catch (NumberFormatException e) {
            logger.trace(END_VALIDATION_UNSUCCESSFUL, e.getMessage());
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }

        logger.trace(END_VALIDATION_SUCCESSFUL);
    }
}
