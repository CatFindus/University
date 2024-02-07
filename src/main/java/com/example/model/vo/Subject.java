package com.example.model.vo;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Getter
public enum Subject {
    HIGHTMATH("Hight Math", "hightmath"),
    LANGUAGES("Languages", "languages"),
    HISTORY_RELIGION("Religion History", "religionhistory"),
    CULTURE_STUDIES("Culture Studies", "culturestufies"),
    PROGRAMMING("Programming","programming"),
    PROBABILITY_THEORY("Probability Theory", "probabilitytheory"),
    MATANALYSIS("Matanalysis", "matanalysis"),
    MATERIALS_SCIENCE("Materials Science", "materialsscience"),
    PHYSICS("Physics","physics");

    private final String name;
    private final String requestName;

    private Subject(String name,String requestName) {
        this.name=name;
        this.requestName=requestName;
    }
    public static boolean containRequestName(String requestName) {
        Subject[] subjects = Subject.values();
        List<String> requestNames = Stream.of(subjects).map(Subject::getRequestName).toList();
        return requestNames.contains(requestName);
    }
    public static Subject getSubject(String requestName) {
        Subject[] subjects = Subject.values();
        Optional<Subject> optional = Stream.of(subjects).filter(subject -> subject.requestName.equals(requestName)).findFirst();
        return optional.orElse(null);
    }
}
