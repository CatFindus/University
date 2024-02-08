package com.example.mapper;

import com.example.model.dto.Request.GroupRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.dto.Response.GroupResponse;
import com.example.model.vo.Group;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface GroupMapper {
    @Mapping(target = "students", expression = "java(students)")
    GroupResponse mapToResponse(Group group, List<DtoResponse> students);

    @Mapping(target = "id", ignore = true)
    Group mapFromRequest(GroupRequest groupRequest);
}
