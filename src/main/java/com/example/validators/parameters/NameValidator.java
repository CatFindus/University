package com.example.validators.parameters;

import com.example.exeptions.IncorrectRequestException;
import com.example.validators.Validators;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.consts.ControlerConstants.NAME_INCORRECT;
import static com.example.consts.ControlerConstants.NAME_REGEXP;
import static com.example.consts.LoggerConstants.*;

@AllArgsConstructor
public class NameValidator extends Validators {
    private static final Logger logger = LoggerFactory.getLogger(NameValidator.class);
    private String firstName, middleName, surName;
    @Override
    protected void validation() throws IncorrectRequestException {
        logger.trace(START_VALIDATION);
        Pattern pattern = Pattern.compile(NAME_REGEXP);
        Matcher matcher;
        if(firstName!=null) {
            matcher=pattern.matcher(firstName);
            if (!matcher.matches()) {
                logger.trace(END_VALIDATION_UNSUCCESSFUL, firstName);
                throw new IncorrectRequestException(NAME_INCORRECT);
            }
        }
        if (middleName!=null) {
            matcher=pattern.matcher(middleName);
            if (!matcher.matches()) {
                logger.trace(END_VALIDATION_UNSUCCESSFUL, middleName);
                throw new IncorrectRequestException(NAME_INCORRECT);
            }
        }
        if (surName!=null) {
            matcher=pattern.matcher(surName);
            if (!matcher.matches()) {
                logger.trace(END_VALIDATION_UNSUCCESSFUL, surName);
                throw new IncorrectRequestException(NAME_INCORRECT);
            }
        }
        logger.trace(END_VALIDATION_SUCCESSFUL);
    }
}
