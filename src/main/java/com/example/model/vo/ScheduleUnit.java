package com.example.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Builder
public class ScheduleUnit implements Comparable<ScheduleUnit>, ModelUnit {
    private LocalDateTime begin;
    private LocalDateTime end;
    private Teacher teacher;
    private Group group;
    private Subject subject;
public boolean isIntersect(ScheduleUnit otherUnit) {
    if(otherUnit == null) return false;
    boolean isBeginMatches = begin.isBefore(otherUnit.end) && begin.isAfter(otherUnit.begin);
    boolean isEndMatches = begin.isBefore(otherUnit.end) && begin.isAfter(otherUnit.begin);
    return (isBeginMatches || isEndMatches) &&
            (this.teacher.getId()==otherUnit.teacher.getId());
}
@Override
public int compareTo(ScheduleUnit o) {
    if (this.begin.isBefore(o.begin)) return 1;
    else if(this.begin.isAfter(o.begin)) return -1;
    else return 0;
}
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
