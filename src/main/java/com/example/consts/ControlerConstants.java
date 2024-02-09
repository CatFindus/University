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
    public static final List<String> SCHEDULE_REQUEST_PARAMETERS = List.of(RQ_BEGIN_DATE_TIME, RQ_END_DATE_TIME , RQ_STUDENT_ID, RQ_GROUP_ID, RQ_TEACHER_ID, RQ_SUBJECT);
    public static final String SUBJECT_PARAMETER = "subject";
    public static final String WARN_MSG = "UUID:{}, message:{}";
    public static final String NAME_REGEXP = "^(?=.{1,40}$)[а-яёА-ЯЁ]+(?:[-' ][а-яёА-ЯЁ]+)*$";
    public static final String PHONE_REGEXP = "/(?:\\+|\\d)[\\d\\-\\(\\) ]{9,}\\d/g";
    public static final String NAME_INCORRECT = "Incorrect firstname/middlename/surname";
    public static final String PHONE_INCORRECT = "Incorrect phone number";
    public static final String BIRTHDAY_INCORRECT = "Birthday no passed validation";
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String UNKNOWN_ERROR = "UNKNOWN REQUEST ERROR";

}
