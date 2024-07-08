package com.example.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "students", schema = "university_sc")
public class Student implements ModelUnit {
    @Id
    @SequenceGenerator(
            name = "student_seq",
            sequenceName = "student_sequence",
            schema = "university_sc",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "student_seq"
    )
    @Column(name = "id")
    private Integer id;
    @Column(name = "first_name", length = 32, nullable = false)
    private String firstName;
    @Column(name = "middle_name", length = 32)
    private String middleName;
    @Column(name = "sur_name", length = 32, nullable = false)
    private String surName;
    @Column(name = "birth_day", nullable = false)
    private LocalDate birthDay;
    @Column(name = "phone_number", length = 16)
    private String phoneNumber;
    @ManyToOne
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "group_id")
    private Group group;


    public boolean hasFirstName(String firstName) {
        if (firstName == null) return false;
        return this.firstName.equalsIgnoreCase(firstName);
    }

    public boolean hasMiddleName(String middleName) {
        if (middleName == null) return false;
        return this.middleName.equalsIgnoreCase(middleName);
    }

    public boolean hasSurName(String surName) {
        if (surName == null) return false;
        return this.surName.equalsIgnoreCase(surName);
    }

    public boolean hasPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return false;
        return this.phoneNumber.equals(phoneNumber);
    }

    public boolean hasBirthDay(LocalDate birthDay) {
        if (birthDay == null) return false;
        return this.birthDay.equals(birthDay);
    }

    public boolean hasId(Integer id) {
        return this.id.equals(id);
    }
}
