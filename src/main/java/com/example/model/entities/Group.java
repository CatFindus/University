package com.example.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "groups", schema = "university_sc")
public class Group implements ModelUnit {

    @Id
    @SequenceGenerator(
            name = "group_seq",
            sequenceName = "group_sequence",
            schema = "university_sc",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "group_seq"
    )
    @Column(name = "id")
    private Integer id;
    @Column(name = "group_number", length = 32, nullable = false)
    private String number;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "group")
    @Builder.Default
    private Set<Student> students = new HashSet<>();

    public Group(String number) {
        this.number = number;
    }

    public boolean hasStudent(Integer id) {
        if (id == null) return false;
        return students.stream().anyMatch(s -> s.hasId(id));
    }

    //@SuppressWarnings("UnusedReturnValue")
    public boolean addStudent(Student student) {
        boolean added;
        if (students==null) students = new HashSet<>();
        if(this.equals(student.getGroup())) added = false;
        else {
            students.add(student);
            student.setGroup(this);
            added = true;
        }
        return added;
    }
    public void removeStudent(Student student) {
        boolean removed = students.remove(student);
        if (removed) student.setGroup(null);
    }
}
