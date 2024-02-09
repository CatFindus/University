package com.example.validators.parameters;

import com.example.exeptions.IncorrectRequestException;
import com.example.model.vo.Subject;
import com.example.validators.Validators;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.example.consts.ControlerConstants.INCORRECT_REQUEST_ARGS;
import static com.example.consts.LoggerConstants.*;

@AllArgsConstructor
public class SubjectValidator extends Validators {
    private static final Logger logger = LoggerFactory.getLogger(SubjectValidator.class);
    private String subject;

    @Override
    protected void validation() throws IncorrectRequestException {
        logger.trace(START_VALIDATION);
        if (subject != null && !Subject.containRequestName(subject)) {
            logger.trace(END_VALIDATION_UNSUCCESSFUL, subject);
            throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
        }
        logger.trace(END_VALIDATION_SUCCESSFUL);

    }
}
