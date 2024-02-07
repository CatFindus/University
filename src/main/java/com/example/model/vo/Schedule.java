package com.example.model.vo;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Data
public class Schedule implements ModelUnit{
    @NonNull
    private LocalDate date;
    private final Set<ScheduleUnit> shedules;

    public Schedule(@NonNull LocalDate date) {
        this.date = date;
        shedules=new ConcurrentSkipListSet<>();
    }
    public boolean isDateBetween(LocalDate begin, LocalDate end) {
        return !(date.isBefore(begin) && date.isAfter(end));
    }
    public boolean addScheduleUnit(ScheduleUnit unit) {
        return shedules.add(unit);
    }
    public boolean removeUnit(ScheduleUnit unit) {
        return shedules.remove(unit);
    }
}

