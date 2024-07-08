package com.example.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Teachers", schema = "university_sc")
public class Teacher implements ModelUnit {
    @Id
    @SequenceGenerator(
            name = "teacher_seq",
            sequenceName = "teacher_sequence",
            schema = "university_sc",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "teacher_seq"
    )
    @Column(name = "id")
    private Integer id;
    @Column(name = "first_name", length = 32, nullable = false)
    private String firstName;
    @Column(name = "middle_name", length = 32)
    private String middleName;
    @Column(name = "sur_name", length = 32, nullable = false)
    private String surName;
    @Column(name = "birth_date")
    private LocalDate birthDay;
    @Column(name = "experience_begin")
    private LocalDate experienceBegin;
    @Column(name = "phone_number", length = 16, nullable = false)
    private String phoneNumber;

    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany
    @JoinTable(
            name = "teachers_subjects",
            schema = "university_sc",
            joinColumns = @JoinColumn(name = "teacher_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id", referencedColumnName = "id")
    )
    private Set<Subject> subjects = new HashSet<>();


    public void addSubject(Subject subject) {
        this.subjects.add(subject);
        subject.getTeachers().add(this);
    }

    public void removeSubject(Subject subject) {
        this.subjects.remove(subject);
        subject.getTeachers().remove(this);
    }

    public int getExperience() {
        if (experienceBegin == null) return 0;
        Period period = Period.between(this.experienceBegin, LocalDate.now());
        return period.getYears();
    }
}


