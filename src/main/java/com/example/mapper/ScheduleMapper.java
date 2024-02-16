package com.example.mapper;

import com.example.consts.ModelConstants;
import com.example.exeptions.IncorrectRequestException;
import com.example.model.dto.Request.ScheduleRequest;
import com.example.model.dto.Request.ScheduleUnitRequest;
import com.example.model.dto.Response.GroupScheduleResponce;
import com.example.model.dto.Response.ScheduleResponse;
import com.example.model.dto.Response.ScheduleUnitResponse;
import com.example.model.dto.Response.TeacherScheduleResponse;
import com.example.model.vo.*;
import com.example.repository.RepositoryFacade;
import org.mapstruct.Mapper;

@Mapper
public interface ScheduleMapper {

    public abstract GroupScheduleResponce mapGroupToDto(Group group);

    public abstract TeacherScheduleResponse mapTeacherToDto(Teacher teacher);

    public abstract ScheduleUnitResponse mapScheduleUnitToDto(ScheduleUnit unit);

    public abstract ScheduleResponse mapScheduleToDto(Schedule schedule);
    @SuppressWarnings("unused")
    public abstract Schedule mapDtoToSchedule(ScheduleRequest request);

    public abstract ScheduleUnit mapDtoToScheduleUnit(ScheduleUnitRequest request, Group group , Teacher teacher);


}
