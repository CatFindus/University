package com.example.model.dto.Request;

import com.example.consts.LoggerConstants;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class GroupRequest implements DtoRequest {
    private final static Logger logger = LoggerFactory.getLogger(GroupRequest.class);
    private String number;

    public GroupRequest(String number) {
        this.number = number;
        logger.debug(LoggerConstants.POJO_CREATED, this);
    }
}
