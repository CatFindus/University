package com.example.model.vo;

import lombok.Data;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import static com.example.consts.LoggerConstants.*;

@Data
public class Schedule implements ModelUnit {
    private final static Logger logger = LoggerFactory.getLogger(Schedule.class);
    @NonNull
    private LocalDate date;
    private final Set<ScheduleUnit> schedules;

    public Schedule(@NonNull LocalDate date) {
        this.date = date;
        schedules = new ConcurrentSkipListSet<>();
        logger.debug(POJO_CREATED, this);
    }

    public boolean addScheduleUnit(ScheduleUnit unit) {
        boolean added = schedules.add(unit);
        logger.trace(ADDING_TO_OBJECT, added ? SUCCESS : UNSUCCESSFUL);
        return added;
    }

    public boolean removeUnit(ScheduleUnit unit) {
        boolean removed = schedules.remove(unit);
        logger.trace(REMOVE_FROM_OBJECT, removed ? SUCCESS : UNSUCCESSFUL);
        return removed;
    }
}

