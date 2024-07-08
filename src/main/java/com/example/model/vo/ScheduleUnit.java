package com.example.model.vo;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

import static com.example.consts.LoggerConstants.POJO_CREATED;

@Data
@Builder
public class ScheduleUnit implements Comparable<ScheduleUnit>, ModelUnit {
    @Getter(AccessLevel.NONE)
    private final static Logger logger = LoggerFactory.getLogger(ScheduleUnit.class);
    private LocalDateTime begin;
    private LocalDateTime end;
    private Teacher teacher;
    private Group group;
    private Subject subject;

    public ScheduleUnit(LocalDateTime begin, LocalDateTime end, Teacher teacher, Group group, Subject subject) {
        this.begin = begin;
        this.end = end;
        this.teacher = teacher;
        this.group = group;
        this.subject = subject;
        logger.debug(POJO_CREATED, this);
    }

    @SuppressWarnings("unused")
    public boolean isIntersect(ScheduleUnit otherUnit) {
        if (otherUnit == null) return false;
        boolean isBeginMatches = begin.isBefore(otherUnit.end) && (begin.isAfter(otherUnit.begin) || begin.isEqual(otherUnit.begin));
        boolean isEndMatches = (end.isBefore(otherUnit.end) || end.isEqual(otherUnit.end)) && end.isAfter(otherUnit.begin);
        boolean timeIntersect = (isBeginMatches || isEndMatches);
        boolean teacherIntersect = this.teacher.getId().equals(otherUnit.teacher.getId());
        boolean groupIntersect = this.group.getId().equals(otherUnit.group.getId());
        boolean intersect = timeIntersect && (groupIntersect || teacherIntersect);
        return intersect;
    }

    @Override
    public int compareTo(ScheduleUnit o) {
        if (this.isIntersect(o)) return 0;
        if (this.begin.isBefore(o.begin) || this.end.isBefore(o.end) ||
                this.teacher.getId() < o.teacher.getId() ||
                this.group.getId() < o.group.getId() ||
                this.subject.getRequestName().compareTo(o.subject.getRequestName()) > 0) {
            return 1;
        } else return -1;
    }

    @SuppressWarnings("unused")
    public boolean isTimeContainInGap(LocalDateTime begin, LocalDateTime end) {
        return !this.begin.isBefore(begin) && !this.end.isAfter(end);
    }

    public boolean hasSubject(String rqSubject) {
        return subject.getRequestName().equalsIgnoreCase(rqSubject);
    }

    public boolean hasTeacher(Integer id) {
        return teacher.getId().equals(id);
    }

    public boolean hasGroup(Integer id) {
        return group.getId().equals(id);
    }

    public boolean hasStudent(Integer id) {
        return group.hasStudent(id);
    }
}
