package com.example.model.service;

import com.example.consts.ControlerConstants;
import com.example.exeptions.IncorrectRequestException;
import com.example.exeptions.NoDataException;
import com.example.mapper.EntityMapper;
import com.example.model.dto.Request.DtoRequest;
import com.example.model.dto.Request.ScheduleUnitRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.dto.Response.NoDataResponse;
import com.example.model.dto.Response.ScheduleUnitResponse;
import com.example.model.entities.*;
import com.example.repository.RepositoryFacade;
import com.example.validators.quantity.ScheduleQuantityValidator;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static com.example.consts.ControlerConstants.*;
import static com.example.consts.LoggerConstants.*;
import static com.example.consts.ModelConstants.*;
@AllArgsConstructor
public class ScheduleService implements Service {
    private final static Logger logger = LoggerFactory.getLogger(ScheduleService.class);
    private final RepositoryFacade repo;
    private final EntityMapper mapper;
    private final Session session;


    @Override
    public DtoResponse getDataById(String idString) throws IncorrectRequestException {
        logger.trace(SERVICE_GETDATABYID_BEGIN, idString);
        long id;
        try {
            id = Long.parseLong(idString);
        } catch (NumberFormatException e) {
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }
        Transaction transaction = session.beginTransaction();
        Optional<ScheduleUnit> mayBeSchedule = repo.getScheduleById(id);
        logger.trace(SERVICE_GETDATABYID_END, mayBeSchedule.map(ScheduleUnit::getId).orElse(null));
        if (mayBeSchedule.isEmpty()) {
            transaction.commit();
            return new NoDataResponse();
        }
        DtoResponse response = mappingVoToDto(mayBeSchedule.get());
        transaction.commit();
        return response;
    }

    public List<DtoResponse> getDataByParameters(Map<String, String[]> parameterMap) throws IncorrectRequestException {
        logger.trace(SERVICE_GETDATABYPARAMS_BEGIN, parameterMap.keySet());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PARAM_PATTERN);
        Map<String, Object> requestMap = new HashMap<>();
        int limit = 100, offset = 0;
        Transaction transaction = session.beginTransaction();
        try {
            for (String key : parameterMap.keySet()) {
                String value = parameterMap.get(key)[0];
                switch (key) {
                    case RQ_BEGIN_DATE_TIME -> requestMap.put(RQ_BEGIN_DATE_TIME, LocalDateTime.parse(value, formatter));
                    case RQ_END_DATE_TIME -> requestMap.put(RQ_END_DATE_TIME, LocalDateTime.parse(value, formatter));
                    case RQ_SUBJECT -> requestMap.put(RQ_SUBJECT, value);
                    case RQ_ID -> requestMap.put(RQ_ID, Long.parseLong(value));
                    case RQ_GROUP_ID -> requestMap.put(RQ_GROUP_ID, Integer.parseInt(value));
                    case RQ_STUDENT_ID -> requestMap.put(RQ_GROUP_ID, getGroupIdByStudentID(Integer.parseInt(value)));
                    case RQ_TEACHER_ID -> requestMap.put(RQ_TEACHER_ID, Integer.parseInt(value));
                    case RQ_LIMIT -> limit = Integer.parseInt(value);
                    case RQ_OFFSET -> offset = Integer.parseInt(value);
                    default -> throw new IncorrectRequestException(PARAM_NOT_RECOGNISED);
                }
            }
        } catch (DateTimeParseException e) {
            throw new IncorrectRequestException(INCORRECT_DATE_FORMAT);
        } catch (NumberFormatException e) {
            throw new IncorrectRequestException(INCORRECT_NUMBER_FORMAT);
        }
        List<ScheduleUnit> schedules = repo.getSchedules(requestMap, limit, offset);
        logger.trace(SERVICE_GETDATABYID_END, schedules != null ? schedules.stream().map(ScheduleUnit::getBegin) : null);
        if (schedules==null) {
            transaction.commit();
            return new ArrayList<>();
        }
        List<DtoResponse> responses = mappingVoToDto(new ArrayList<>(schedules));
        transaction.commit();
        return responses;
    }

    private Integer getGroupIdByStudentID(int studentId) throws IncorrectRequestException {
        Optional<Student> mayBeStudent = repo.getStudent(studentId);
        if (mayBeStudent.isEmpty()) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
        else {
            Group group = mayBeStudent.get().getGroup();
            if (group==null) throw new IncorrectRequestException(NO_DATA_FOUND);
            return group.getId();
        }
    }

    public List<DtoResponse> mappingVoToDto(List<ModelUnit> modelUnitList) {
        logger.trace(SERVICE_MAP_DTO_BEGIN, modelUnitList.size());
        List<DtoResponse> responses = new ArrayList<>();
        if (modelUnitList.isEmpty()) return new ArrayList<>();
        if (modelUnitList.get(0) instanceof ScheduleUnit)
            for (ModelUnit unit : modelUnitList) responses.add(mapper.mapScheduleUnitToDto((ScheduleUnit) unit));
        if (modelUnitList.get(0) instanceof ScheduleUnit)
            for (ModelUnit unit : modelUnitList) responses.add(mapper.mapScheduleUnitToDto((ScheduleUnit) unit));
        logger.trace(SERVICE_MAP_DTO_END, responses.size());
        return responses;
    }

    @Override
    public DtoResponse mappingVoToDto(ModelUnit modelUnit) {
        ScheduleUnit unit = (ScheduleUnit) modelUnit;
        return mapper.mapScheduleUnitToDto(unit);
    }
    @Override
    public DtoResponse create(String path, DtoRequest request) throws NoDataException, IncorrectRequestException {
        Transaction transaction = session.beginTransaction();
        ScheduleUnitRequest unitRequest = (ScheduleUnitRequest) request;
        Optional<Teacher> mayBeTeacher = repo.getTeacher(unitRequest.getTeacherId());
        Optional<Group> mayBeGroup = repo.getGroup(unitRequest.getGroupId());
        Optional<Subject> mayBeSubject = repo.getSubjectByRequestName(unitRequest.getRequestNameSubject());
        if (mayBeSubject.isEmpty() || mayBeGroup.isEmpty() || mayBeTeacher.isEmpty()) throw  new NoDataException(NO_DATA_FOUND);
        if (unitRequest.getBegin()==null) throw new IncorrectRequestException(INCORRECT_REQUEST_ARGS);
        new ScheduleQuantityValidator(
                POST,
                repo.getSchedulesCountByGroupPerDay(
                        mayBeGroup.get().getId(),
                        unitRequest.getBegin().toLocalDate()))
                .validate();
        ScheduleUnit unit = mapper.mapDtoToScheduleUnit(unitRequest, mayBeGroup.get(), mayBeTeacher.get(), mayBeSubject.get());
        logger.trace(SERVICE_CREATE, unit.getBegin());
        repo.addSchedule(unit);
        DtoResponse response = mappingVoToDto(unit);
        transaction.commit();
        return response;
    }

    @Override
    public DtoResponse update(String id, DtoRequest dtoRequest) throws IncorrectRequestException, NoDataException {
        try {
            Long longId = Long.parseLong(id);
            Transaction transaction = session.beginTransaction();
            Optional<ScheduleUnit> mayBeSchedule = repo.getScheduleById(longId);
            if (mayBeSchedule.isEmpty()) throw new NoDataException(NO_DATA_FOUND);
            ScheduleUnitRequest request = (ScheduleUnitRequest) dtoRequest;
            doValidate(mayBeSchedule.get(), request);
            ScheduleUnit unit = updateFields(mayBeSchedule.get(), request);
            unit = repo.update(unit);
            ScheduleUnitResponse unitResponse = mapper.mapScheduleUnitToDto(unit);
            transaction.commit();
            return unitResponse;
        } catch (NumberFormatException e) { throw  new IncorrectRequestException(INCORRECT_NUMBER_FORMAT); }
    }

    private void doValidate(ScheduleUnit scheduleUnit, ScheduleUnitRequest request) throws IncorrectRequestException {
        if (request.getBegin()!=null) {
            LocalDate requestDate = request.getBegin().toLocalDate();
            LocalDate unitDate = scheduleUnit.getBegin().toLocalDate();
            Integer groupId = scheduleUnit.getGroup().getId();
            if (!requestDate.equals(unitDate)) {
                Integer countForRequestdate = repo.getSchedulesCountByGroupPerDay(groupId, requestDate);
                Integer countForUnitdate = repo.getSchedulesCountByGroupPerDay(groupId, unitDate);
                new ScheduleQuantityValidator(POST, countForRequestdate)
                        .then(new ScheduleQuantityValidator(DELETE, countForUnitdate))
                        .validate();
            }
        }
    }

    private ScheduleUnit updateFields(ScheduleUnit unit, ScheduleUnitRequest request) {
        if (request.getGroupId() != null) {
            Optional<Group> mayBeGroup = repo.getGroup(request.getGroupId());
            mayBeGroup.ifPresent(unit::setGroup);
        }
        if (request.getTeacherId()!= null) {
            Optional<Teacher> mayBeTeacher = repo.getTeacher(request.getTeacherId());
            mayBeTeacher.ifPresent(unit::setTeacher);
        }
        if (request.getBegin() != null) unit.setBegin(request.getBegin());
        if (request.getEnd() != null) unit.setEnd(request.getEnd());
        if (request.getRequestNameSubject() != null) {
            Optional<Subject> mayBeSubject = repo.getSubjectByRequestName(request.getRequestNameSubject());
            mayBeSubject.ifPresent(unit::setSubject);
        }
        return unit;
    }

    @Override
    public DtoResponse delete(String path, DtoRequest request) throws IncorrectRequestException, NoDataException {
        try {
            Long id = Long.parseLong(path);
            Transaction transaction = session.beginTransaction();
            Optional<ScheduleUnit> mayBeSchedule = repo.getScheduleById(id);
            if (mayBeSchedule.isEmpty()) throw new NoDataException(NO_DATA_FOUND);
            new ScheduleQuantityValidator(
                    ControlerConstants.DELETE,
                    repo.getSchedulesCountByGroupPerDay(
                            mayBeSchedule.get().getGroup().getId(),
                            mayBeSchedule.get().getBegin().toLocalDate()))
                    .validate();
            boolean deleted = repo.deleteSchedule(id);
            if(!deleted) throw new NoDataException(NO_DATA_FOUND);
            transaction.commit();
            return new NoDataResponse();
        } catch (NumberFormatException e) { throw  new IncorrectRequestException(INCORRECT_NUMBER_FORMAT); }
    }
}
