package com.example.model.service;

import com.example.consts.ModelConstants;
import com.example.exeptions.IncorrectRequestException;
import com.example.exeptions.NoDataException;
import com.example.mapper.ScheduleMapper;
import com.example.mapper.ScheduleMapperImpl;
import com.example.model.dto.Request.ScheduleUnitRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.vo.ModelUnit;
import com.example.model.vo.Schedule;
import com.example.model.vo.ScheduleUnit;
import com.example.repository.RepositoryFacade;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.example.consts.ModelConstants.*;

public class ScheduleService{
    private final RepositoryFacade repo = new RepositoryFacade();
    private final ScheduleMapper mapper = new ScheduleMapperImpl();
    private final Service teacherService = new TeacherService();
    private final Service studentService = new StudentService();
    private final Service groupService = new GroupService();
/*
    begindatetime - обязательный
    enddatetime - обязательный
    studentid - не обязательный
    teacherid - не обязательный
    groupid - не обязательный
    subject - не обязательный
*/
    public List<ModelUnit> getDataByParameters(Map<String, String[]> parameterMap) throws IncorrectRequestException {
        LocalDateTime beginDate=null;
        LocalDateTime endDate=null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PARAM_PATTERN);
        List<Predicate<ScheduleUnit>> predicates = new ArrayList<>();
        try {
            for (String key : parameterMap.keySet()) {
                String value = parameterMap.get(key)[0];
                switch (key) {
                    case RQ_BEGIN_DATE_TIME -> beginDate = LocalDateTime.parse(parameterMap.get(RQ_BEGIN_DATE_TIME)[0], formatter);
                    case RQ_END_DATE_TIME -> endDate = LocalDateTime.parse(parameterMap.get(RQ_END_DATE_TIME)[0], formatter);
                    case RQ_SUBJECT -> predicates.add(unit -> unit.hasSubject(value));
                    case RQ_GROUP_ID -> predicates.add(unit -> unit.hasGroup(Integer.parseInt(value)));
                    case RQ_STUDENT_ID -> predicates.add(unit -> unit.hasStudent(Integer.parseInt(value)));
                    case RQ_TEACHER_ID -> predicates.add(unit -> unit.hasTeacher(Integer.parseInt(value)));
                    default -> throw new IncorrectRequestException();
                }
            }
        } catch (DateTimeParseException | NumberFormatException e) { throw new IncorrectRequestException(); }
        if (beginDate==null || endDate==null || endDate.isBefore(beginDate)) throw new IncorrectRequestException();
            List<ScheduleUnit> schedules = repo.getSchedules(beginDate,endDate, predicates);
            return new ArrayList<>(schedules);
    }
    public List<DtoResponse> mappingVoToDto(List<ModelUnit> modelUnitList) {
        List<DtoResponse> responses = new ArrayList<>();
        if (modelUnitList==null || modelUnitList.isEmpty()) return new ArrayList<>();
        if (modelUnitList.get(0) instanceof ScheduleUnit) for (ModelUnit unit:modelUnitList) responses.add(mapper.mapScheduleUnitToDto((ScheduleUnit) unit));
        if (modelUnitList.get(0) instanceof Schedule) for (ModelUnit unit:modelUnitList) responses.add(mapper.mapScheduleToDto((Schedule) unit));
        return responses;
    }

    public boolean create(ModelUnit modelUnit) {
        if (modelUnit instanceof Schedule schedule) {
            return repo.addSchedule(schedule);
        } else if (modelUnit instanceof ScheduleUnit scheduleUnit) {
            return repo.addScheduleUnit(scheduleUnit);
        }else return false;
    }
    public List<DtoResponse> update(Map<String, String[]> parameterMap, ScheduleUnitRequest request) throws IncorrectRequestException, NoDataException {
        List<ModelUnit> unitList = getDataByParameters(parameterMap);
        if(unitList.size()!=1) throw new NoDataException();
        else {
            ScheduleUnit oldunit = (ScheduleUnit) unitList.get(0);
            Schedule schedule = repo.getScheduleByUnit(oldunit);
            ScheduleUnit newUnit = mapper.mapDtoToScheduleUnit(request);
            create(newUnit);
            schedule.removeUnit(oldunit);
            return List.of(mapper.mapScheduleUnitToDto(newUnit));
        }
    }
    public void delete(Map<String, String[]> parameterMap) throws IncorrectRequestException, NoDataException {
        List<ModelUnit> unitList = getDataByParameters(parameterMap);
        if(unitList.size()!=1) throw new NoDataException();
        else {
            ScheduleUnit oldunit = (ScheduleUnit) unitList.get(0);
            Schedule schedule = repo.getScheduleByUnit(oldunit);
            schedule.removeUnit(oldunit);
        }
    }
}
