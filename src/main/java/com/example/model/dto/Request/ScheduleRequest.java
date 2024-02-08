package com.example.model.dto.Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

import static com.example.consts.LoggerConstants.POJO_CREATED;

@Data
public class ScheduleRequest implements DtoRequest {
    private final static Logger logger = LoggerFactory.getLogger(ScheduleRequest.class);
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;

    public ScheduleRequest(LocalDate date) {
        this.date = date;
        logger.debug(POJO_CREATED, this);
    }
}
