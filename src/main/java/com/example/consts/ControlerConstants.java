package com.example.consts;

import lombok.experimental.UtilityClass;

import java.util.List;
@UtilityClass
public class ControlerConstants {
    public static final String INCORRECT_URL_PATH = "URL path not recognised: {}";
    public static final String NO_DATA_FOUND = "No data found for request: ";
    public static final String INCORRECT_REQUEST = "Request not recognised: ";
    public static final String BAD_BODY = "Body not recognised: ";
    public static final List<String> STUDENT_REQUEST_PARAMETERS = List.of("firstname", "middlename", "surname", "groupid", "groupnumber", "phonenumber", "birthday", "id");
    public static final List<String> TEACHER_REQUEST_PARAMETERS = List.of("firstname", "middlename", "surname", "phonenumber", "birthday", "experience", "subject", "id");
    public static final List<String> GROUP_REQUEST_PARAMETERS = List.of("number", "id");
    public static final List<String> SHEDULE_REQUEST_PARAMETERS = List.of("begindatetime", "enddatetime" , "student", "group", "teacher", "subject");
    public static final String SUBJECT_PARAMETER = "subject";
    public static final String ID_PARAMETER = "id";


}
