package com.example.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "schedule_units", schema = "university_sc")
public class ScheduleUnit implements Comparable<ScheduleUnit>, ModelUnit {
    @Id
    @SequenceGenerator(
            name = "schedule_seq",
            sequenceName = "schedule_sequence",
            schema = "university_sc",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "schedule_seq"
    )
    private Long id;
    @Column(name = "begin_ts", nullable = false)
    private LocalDateTime begin;
    @Column(name = "end_ts", nullable = false)
    private LocalDateTime end;
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;



    @SuppressWarnings("unused")
    public boolean isIntersect(ScheduleUnit otherUnit) {
        if (otherUnit == null) return false;
        boolean isBeginMatches = begin.isBefore(otherUnit.end) && (begin.isAfter(otherUnit.begin) || begin.isEqual(otherUnit.begin));
        boolean isEndMatches = (end.isBefore(otherUnit.end) || end.isEqual(otherUnit.end)) && end.isAfter(otherUnit.begin);
        boolean timeIntersect = (isBeginMatches || isEndMatches);
        boolean teacherIntersect = this.teacher.getId().equals(otherUnit.teacher.getId());
        boolean groupIntersect = this.group.getId().equals(otherUnit.group.getId());
        return timeIntersect && (groupIntersect || teacherIntersect);
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
