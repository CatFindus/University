package com.example.validators.parameters;

import com.example.exeptions.IncorrectRequestException;
import com.example.validators.Validators;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.consts.ControlerConstants.*;
import static com.example.consts.LoggerConstants.*;

@AllArgsConstructor
public class PhoneValidator extends Validators {
    private static final Logger logger = LoggerFactory.getLogger(PhoneValidator.class);

    private String phone;
    @Override
    protected void validation() throws IncorrectRequestException {
        logger.trace(START_VALIDATION);
        Pattern pattern = Pattern.compile(PHONE_REGEXP);
        Matcher matcher;
        if(phone!=null) {
            matcher=pattern.matcher(phone);
            if (!matcher.matches()) {
                logger.trace(END_VALIDATION_UNSUCCESSFUL, PHONE_INCORRECT);
                throw new IncorrectRequestException(PHONE_INCORRECT);
            }
        }
        logger.trace(END_VALIDATION_SUCCESSFUL);
    }
}
