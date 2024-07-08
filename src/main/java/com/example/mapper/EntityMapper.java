package com.example.mapper;

import com.example.model.dto.Request.*;
import com.example.model.dto.Response.*;
import com.example.model.entities.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface EntityMapper {

    ScheduleUnitResponse mapScheduleUnitToDto(ScheduleUnit unit);
    @Mapping(ignore = true, target = "id")
    ScheduleUnit mapDtoToScheduleUnit(ScheduleUnitRequest request, Group group , Teacher teacher, Subject subject);

    GroupResponse mapGroupToResponse(Group group);

    @Mapping(target = "id", ignore = true)
    Group mapGroupFromRequest(GroupRequest groupRequest);

    StudentResponse mapStudentToResponse(Student student);
    @Mapping(target = "id", ignore = true)
    Student mapStudentFromRequest(StudentRequest studentDto);

    TeacherResponse mapTeacherToResponse(Teacher teacher);

    @Mapping(target = "id", ignore = true)
    Teacher mapTeacherFromRequest(TeacherRequest studentDto);
    @Mapping(target = "id", ignore = true)
    Subject mapSubjectFromRequest(SubjectRequest subjectDto);

    SubjectResponse mapSubjectToResponse(Subject subject);
}
