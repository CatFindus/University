package com.example.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "subjects", schema = "university_sc")
public class Subject implements ModelUnit{
    @Id
    @SequenceGenerator(
            name = "subject_seq",
            sequenceName = "subject_sequence",
            schema = "university_sc",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "subject_seq"
    )
    private Integer id;
    @Column(name = "name", nullable = false, unique = true, length = 64)
    private String name;
    @Column(name = "request_name", nullable = false, unique = true, length = 32)
    private String requestName;
    @ManyToMany(mappedBy = "subjects")
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Teacher> teachers = new HashSet<>();

}
