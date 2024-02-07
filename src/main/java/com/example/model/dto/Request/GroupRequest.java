package com.example.model.dto.Request;

import com.example.model.vo.Student;
import lombok.Data;

import java.util.concurrent.CopyOnWriteArrayList;
@Data
public class GroupRequest implements DtoRequest{
    private String number;
    //private final CopyOnWriteArrayList<Student> students;
}
