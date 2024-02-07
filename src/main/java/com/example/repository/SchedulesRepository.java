package com.example.repository;

import com.example.model.vo.Schedule;
import com.example.model.vo.ScheduleUnit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class SchedulesRepository {
    private static final SchedulesRepository instance = new SchedulesRepository();
    private final ConcurrentSkipListMap<LocalDate, Schedule> schedules;
    private SchedulesRepository() {
        schedules = new ConcurrentSkipListMap<>();
    }
    static SchedulesRepository getInstance() {
        return instance;
    }
    List<ScheduleUnit> getSchedules(LocalDateTime begin, LocalDateTime end, List<Predicate<ScheduleUnit>> predicates) {
        List<ScheduleUnit> result = new ArrayList<>();
        Map<LocalDate,Schedule> subMap = schedules.subMap(begin.toLocalDate(),true,end.toLocalDate(),true);
        for (LocalDate key: subMap.keySet()) {
            Stream<ScheduleUnit> stream = subMap.get(key).getShedules().stream();
            for (Predicate<ScheduleUnit> predicate : predicates) stream = stream.filter(predicate);
            result.addAll(stream.toList());
        }
        return result;
    }

    Schedule getSchedule(LocalDate date) {
       return schedules.get(date);
    }

    boolean addSchedule(Schedule schedule) {
        Schedule returned = schedules.putIfAbsent(schedule.getDate(), schedule);
        return returned == null;
    }
    boolean addSchedule(ScheduleUnit unit) {
        LocalDate date = unit.getBegin().toLocalDate();
        if (schedules.containsKey(date)) return schedules.get(date).addScheduleUnit(unit);
        else {
            Schedule schedule = new Schedule(date);
            schedule.addScheduleUnit(unit);
            return null == schedules.put(date, schedule);
        }
    }
    Schedule getSheduleByUnit(ScheduleUnit unit) {
        Stream<Schedule> stream = schedules.subMap(unit.getBegin().toLocalDate(), true, unit.getEnd().toLocalDate(),true)
                .values().stream().filter(schedule -> schedule.getShedules().contains(unit));
        return stream.findFirst().orElse(null);
    }

}
