package com.example.model.dto.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter
public class ErrorResponse implements DtoResponse{
    private final String msg;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private final LocalDateTime dateTime;
    private final UUID errorID;

    public ErrorResponse(String msg) {
        this.msg = msg;
        dateTime = LocalDateTime.now();
        this.errorID = UUID.randomUUID();
    }
}
