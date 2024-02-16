package com.example.model.service;

import com.example.consts.ControlerConstants;
import com.example.consts.ModelConstants;
import com.example.exeptions.IncorrectRequestException;
import com.example.exeptions.NoDataException;
import com.example.mapper.ScheduleMapper;
import com.example.model.dto.Request.ScheduleUnitRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.vo.*;
import com.example.repository.RepositoryFacade;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.example.consts.ControlerConstants.INCORRECT_REQUEST_ARGS;
import static com.example.consts.ControlerConstants.NO_DATA_FOUND;
import static com.example.consts.LoggerConstants.*;
import static com.example.consts.ModelConstants.*;
@AllArgsConstructor
public class ScheduleService {
    private final static Logger logger = LoggerFactory.getLogger(ScheduleService.class);
    private final RepositoryFacade repo;
    private final ScheduleMapper mapper;
    private final Service groupService;
    private final Service teacherService;

    public List<ModelUnit> getDataByParameters(Map<String, String[]> parameterMap) throws IncorrectRequestException {
        logger.trace(SERVICE_GETDATABYPARAMS_BEGIN, parameterMap.keySet());
        LocalDateTime beginDate = null;
        LocalDateTime endDate = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PARAM_PATTERN);
        List<Predicate<ScheduleUnit>> predicates = new ArrayList<>();
        try {
            for (String key : parameterMap.keySet()) {
                String value = parameterMap.get(key)[0];
                switch (key) {
                    case RQ_BEGIN_DATE_TIME ->
                            beginDate = LocalDateTime.parse(parameterMap.get(RQ_BEGIN_DATE_TIME)[0], formatter);
                    case RQ_END_DATE_TIME ->
                            endDate = LocalDateTime.parse(parameterMap.get(RQ_END_DATE_TIME)[0], formatter);
                    case RQ_SUBJECT -> predicates.add(unit -> unit.hasSubject(value));
                    case RQ_GROUP_ID -> predicates.add(unit -> unit.hasGroup(Integer.parseInt(value)));
                    case RQ_STUDENT_ID -> predicates.add(unit -> unit.hasStudent(Integer.parseInt(value)));
                    case RQ_TEACHER_ID -> predicates.add(unit -> unit.hasTeacher(Integer.parseInt(value)));
                    default -> throw new IncorrectRequestException(PARAM_NOT_RECOGNISED);
                }
            }
        } catch (DateTimeParseException e) {
            throw new IncorrectRequestException(INCORRECT_DATE_FORMAT);
        } catch (NumberFormatException e) {
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }
        if (beginDate == null || endDate == null || endDate.isBefore(beginDate))
            throw new IncorrectRequestException(INCORRECT_DATE_FORMAT);
        List<ScheduleUnit> schedules = repo.getSchedules(beginDate, endDate, predicates);
        logger.trace(SERVICE_GETDATABYID_END, schedules != null ? schedules.stream().map(ScheduleUnit::getBegin) : null);
        if (schedules == null) return new ArrayList<>();
        return new ArrayList<>(schedules);
    }

    public List<DtoResponse> mappingVoToDto(List<ModelUnit> modelUnitList) {
        logger.trace(SERVICE_MAP_DTO_BEGIN, modelUnitList.size());
        List<DtoResponse> responses = new ArrayList<>();
        if (modelUnitList.isEmpty()) return new ArrayList<>();
        if (modelUnitList.get(0) instanceof ScheduleUnit)
            for (ModelUnit unit : modelUnitList) responses.add(mapper.mapScheduleUnitToDto((ScheduleUnit) unit));
        if (modelUnitList.get(0) instanceof Schedule)
            for (ModelUnit unit : modelUnitList) responses.add(mapper.mapScheduleToDto((Schedule) unit));
        logger.trace(SERVICE_MAP_DTO_END, responses.size());
        return responses;
    }

    public boolean create(ModelUnit modelUnit) {
        if (modelUnit instanceof Schedule schedule) {
            logger.trace(SERVICE_CREATE, ((Schedule) modelUnit).getDate());
            return repo.addSchedule(schedule);
        } else if (modelUnit instanceof ScheduleUnit scheduleUnit) {
            logger.trace(SERVICE_CREATE, ((ScheduleUnit) modelUnit).getBegin());
            return repo.addScheduleUnit(scheduleUnit);
        } else return false;
    }

    public List<DtoResponse> update(Map<String, String[]> parameterMap, ScheduleUnitRequest request) throws IncorrectRequestException, NoDataException {
        logger.trace(SERVICE_UPDATE_BEGIN, parameterMap.keySet(), request);
        List<ModelUnit> unitList = getDataByParameters(parameterMap);
        if (unitList.isEmpty()) throw new NoDataException(DATA_NOT_FOUND);
        else if (unitList.size() > 1) throw new NoDataException(SCHEDULE_NOT_UNIQUE);
        else {
            ScheduleUnit oldUnit = (ScheduleUnit) unitList.get(0);
            Schedule schedule = repo.getScheduleByUnit(oldUnit);
            Group group = (Group) groupService.getDataById(request.getGroupId().toString()).get(0);
            Teacher teacher = (Teacher) teacherService.getDataById(request.getTeacherId().toString()).get(0);
            if (group==null || teacher==null) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
            ScheduleUnit newUnit = mapper.mapDtoToScheduleUnit(request, group, teacher);
            boolean removed = schedule.removeUnit(oldUnit);
            if (!removed) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
            boolean created = create(newUnit);
            if(!created) {
                create(oldUnit);
                throw new NoDataException(ERROR_TO_CHANGE_SCHEDULE);
            }
            logger.trace(SERVICE_UPDATE_END);
            return List.of(mapper.mapScheduleUnitToDto(newUnit));
        }
    }

    public void delete(Map<String, String[]> parameterMap) throws IncorrectRequestException, NoDataException {
        List<ModelUnit> unitList = getDataByParameters(parameterMap);
        if (unitList.isEmpty()) throw new NoDataException(DATA_NOT_FOUND);
        else if (unitList.size() > 1) throw new NoDataException(SCHEDULE_NOT_UNIQUE);
        else {
            ScheduleUnit oldUnit = (ScheduleUnit) unitList.get(0);
            Schedule schedule = repo.getScheduleByUnit(oldUnit);
            if (schedule==null) throw new NoDataException(NO_DATA_FOUND);
            boolean removed =  schedule.removeUnit(oldUnit);
            if (!removed) throw new NoDataException(NO_DATA_FOUND);
        }
    }

    public Schedule getScheduleForDate(LocalDate date) {
        return repo.getSchedule(date);
    }
}
