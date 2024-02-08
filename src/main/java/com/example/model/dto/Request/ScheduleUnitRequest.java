package com.example.model.dto.Request;

import com.example.consts.LoggerConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
@Data
public class ScheduleUnitRequest implements DtoRequest {
    private final static Logger logger = LoggerFactory.getLogger(ScheduleUnitRequest.class);
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime begin;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime end;
    private Integer studentId;
    private Integer groupId;
    private Integer teacherId;
    private String subject;

    public ScheduleUnitRequest(LocalDateTime begin, LocalDateTime end, Integer studentId, Integer groupId, Integer teacherId, String subject) {
        this.begin = begin;
        this.end = end;
        this.studentId = studentId;
        this.groupId = groupId;
        this.teacherId = teacherId;
        this.subject = subject;
        logger.debug(LoggerConstants.POJO_CREATED, this);
    }
}
