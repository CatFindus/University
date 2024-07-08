package com.example.validators.quantity;

import com.example.exeptions.IncorrectRequestException;
import com.example.model.vo.Group;
import com.example.model.vo.Student;
import com.example.validators.Validators;
import com.example.validators.requests.StudentsValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import static com.example.consts.ControlerConstants.*;
import static com.example.consts.LoggerConstants.*;
import static com.example.consts.ModelConstants.*;

@AllArgsConstructor
public class GroupQuantityValidator extends Validators {
    private static final Logger logger = LoggerFactory.getLogger(GroupQuantityValidator.class);
    private HttpServletRequest req;
    private Group group;

    @Override
    protected void validation() throws IncorrectRequestException {
        logger.trace(START_VALIDATION);
        int max, min;
        Properties properties = new Properties();
        try (InputStream is = Objects.requireNonNull(Student.class.getClassLoader().getResource(MODEL_PROPERTY)).openStream()) {
            properties.load(is);
        } catch (IOException e) {
            logger.error(ERROR_PROPERTY);
        }
        max = Integer.parseInt(properties.getProperty(MAX_STUDENT_IN_GROUP));
        min = Integer.parseInt(properties.getProperty(MIN_STUDENT_IN_GROUP));
        String method = req.getMethod();
        switch (method) {
            case POST -> doMaxQuantityValidation(max);
            case DELETE -> doMinQuantityValidation(min);
            default -> throw new IncorrectRequestException(UNKNOWN_ERROR);
        }
        logger.trace(END_VALIDATION_SUCCESSFUL);
    }

    private void doMinQuantityValidation(int min) throws IncorrectRequestException {
        if (group.getStudentsQuantity() <= min) {
            logger.error(ERROR_MIN_STUDENT_IN_GROUP);
            throw new IncorrectRequestException(ERROR_MIN_STUDENT_IN_GROUP);
        }
    }

    private void doMaxQuantityValidation(int max) throws IncorrectRequestException {
        if (group.getStudentsQuantity() >= max) {
            logger.error(MAX_STUDENT_IN_GROUP);
            throw new IncorrectRequestException(ERROR_MAX_STUDENT_IN_GROUP);
        }
    }

}
