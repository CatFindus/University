package com.example.mapper;

import com.example.model.dto.Request.TeacherRequest;
import com.example.model.dto.Response.TeacherResponse;
import com.example.model.vo.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TeacherMapper {

    @Mapping(target = "experience", expression = "java(experience)")
    TeacherResponse mapToResponse(Teacher teacher, String experience);
    @Mapping(target = "id", ignore = true)
    Teacher mapFromRequest(TeacherRequest studentDto);
}
