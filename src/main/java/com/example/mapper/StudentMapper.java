package com.example.mapper;

import com.example.model.dto.Request.StudentRequest;
import com.example.model.dto.Response.StudentResponse;
import com.example.model.vo.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface StudentMapper {
    @Mapping(target = "groupId", expression = "java(groupId)")
    StudentResponse mapToResponse(Student student, Integer groupId);
    @Mapping(target = "id", ignore = true)
    Student mapFromRequest(StudentRequest studentDto);
}
