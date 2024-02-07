package com.example.model.dto.Response;

import com.example.model.vo.Student;
import lombok.Data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
@Data
public class GroupResponse implements DtoResponse{
    private final Integer id;
    private String number;
    private final List<DtoResponse> students;
}
