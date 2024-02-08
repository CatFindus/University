package com.example.consts;

import lombok.experimental.UtilityClass;

import java.util.List;

import static com.example.consts.ModelConstants.*;

@UtilityClass
public class ControlerConstants {
    public static final String NO_DATA_FOUND = "No data found for request";
    public static final String INCORRECT_REQUEST_ARGS = "Incorrect request args. No pass validation";
    public static final List<String> STUDENT_REQUEST_PARAMETERS = List.of(RQ_FIRST_NAME, RQ_MIDDLE_NAME, RQ_SURNAME, RQ_GROUP_ID, RQ_GROUP_NUMBER, RQ_PHONE_NUMBER, RQ_BIRTHDAY, RQ_ID);
    public static final List<String> TEACHER_REQUEST_PARAMETERS = List.of(RQ_FIRST_NAME, RQ_MIDDLE_NAME, RQ_SURNAME, RQ_PHONE_NUMBER, RQ_BIRTHDAY, RQ_EXPERIENCE, RQ_SUBJECT, RQ_ID);
    public static final List<String> GROUP_REQUEST_PARAMETERS = List.of(RQ_NUMBER, RQ_ID);
    public static final List<String> SHEDULE_REQUEST_PARAMETERS = List.of(RQ_BEGIN_DATE_TIME, RQ_END_DATE_TIME , RQ_STUDENT_ID, RQ_GROUP_ID, RQ_TEACHER_ID, RQ_SUBJECT);
    public static final String SUBJECT_PARAMETER = "subject";
    public static final String WARN_MSG = "UUID:{}, message:{}";

}
