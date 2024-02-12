package com.example.consts;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LoggerConstants {
    public final static String SUCCESS = "successfully";
    public final static String UNSUCCESSFUL = "unsuccessfully";
    public final static String POJO_CREATED = "Object is created: {}";
    public final static String ADDING_TO_OBJECT = "Adding to object : {}";
    public final static String REMOVE_FROM_OBJECT = "Adding to object : {}";
    public final static String SERVICE_GETDATABYID_BEGIN = "Service beginning to get data by id {}";
    public final static String SERVICE_GETDATABYID_END = "Service finished to get data by id with result id:{}";
    public final static String SERVICE_GETDATABYPARAMS_BEGIN = "Service beginning to get data by params: {}";
    public final static String SERVICE_GETDATABYPARAMS_END = "Service finished to get data by id with result id:{}";
    public final static String SERVICE_MAP_DTO_BEGIN = "Service beginning mapping to DTO list size: {}";
    public final static String SERVICE_MAP_DTO_END = "Service finished mapping to DTO with list size:{}";
    public final static String SERVICE_CREATE = "Service adding model unit in repository with id: {}";
    public final static String SERVICE_UPDATE_BEGIN = "Service beginning to update data with path:'{}' and request:'{}'";
    public final static String SERVICE_UPDATE_END = "Service ended to update data";
    public final static String SERVICE_DELETE_BEGIN = "Service beginning to delete data with path:'{}'";
    public final static String SERVICE_DELETE_END = "Service ended to delete data with result '{}'";
    public static final String LOGGER_ERROR_JSON_STRING = "{} - Internal error to write JSON string";
    public static final String REPO_GET_STUDENT_BY_ID = "Repository used 'getStudent' with arg:'{}'. Return result: '{}'" ;
    public static final String REPO_GET_STUDENT_BY_PREDICATES = "Repository used 'getStudent' with predicate size:'{}'. Return students with ids: '{}'" ;
    public static final String REPO_ADD_STUDENT = "Repository used 'addStudent' with arg:'{}'. Return : '{}'" ;
    public static final String REPO_REMOVE_STUDENT = "Repository used 'removeStudent' with arg:'{}'. Return : '{}'" ;
    public static final String REPO_GET_GROUP = "Repository used 'getGroup' with arg:'{}'. Return : '{}'" ;
    public static final String REPO_GET_GROUPS = "Repository used 'getGroups' with arg:'{}'. Return : '{}' groups" ;
    public static final String REPO_ADD_GROUP = "Repository used 'addGroup' with arg:'{}'. Return : '{}'" ;
    public static final String REPO_REMOVE_GROUP = "Repository used 'removeGroup' with arg:'{}'. Return : '{}'" ;
    public static final String REPO_GET_TEACHER = "Repository used 'getTeacher' with arg:'{}'. Return : '{}'" ;
    public static final String REPO_GET_TEACHERS_BY_PREDICATES = "Repository used 'getTeachers' with predicate size:'{}'. Return students with ids: '{}'" ;
    public static final String REPO_REMOVE_TEACHER = "Repository used 'removeTeacher' with arg:'{}'. Return : '{}'" ;
    public static final String REPO_GET_GROUP_ID = "Repository used 'getGroupIdByStudent' with arg:'{}'. Return : '{}'" ;
    public static final String REPO_ADD_TEACHER = "Repository used 'addTeacher' with arg:'{}'. Return : '{}'" ;
    public static final String REPO_REMOVE_STUDENT_FROM_GROUP = "Repository used 'removeStudentFromGroup' with args:'studentID:{},groupID:{}'. Return : '{}'" ;
    public static final String REPO_ADD_STUDENT_TO_GROUP = "Repository used 'addStudentToGroup' with args:'studentID:{},groupID:{}'. Return : '{}'" ;
    public static final String REPO_GET_SCHEDULES = "Repository used 'getSchedules'. Return : '{} schedules" ;
    public static final String REPO_ADD_SCHEDULES = "Repository used 'addSchedule' with on date: {}" ;
    public static final String REPO_ADD_SCHEDULES_UNIT = "Repository used 'addScheduleUnit' with on begin time: {}" ;
    public static final String REPO_GET_SCHEDULE_BY_UNIT = "Repository used 'getScheduleByUnit'. Return schedule with date: '{} by ScheduleUnit with begin time: '{}'" ;
    public static final String DO_GET_BEGIN = "DoGet starting method";
    public static final String DO_GET_END = "DoGet finished method" ;
    public static final String DO_POST_BEGIN = "DoPost starting method";
    public static final String DO_POST_END = "DoPost finished method" ;
    public static final String DO_PUT_BEGIN = "DoPut starting method";
    public static final String DO_PUT_END = "DoPut finished method";
    public static final String DO_DELETE_BEGIN = "DoDelete starting method";
    public static final String DO_DELETE_END = "DoDelete finished method" ;
    public static final String START_VALIDATION = "Start validation method";
    public static final String END_VALIDATION_SUCCESSFUL = "End validation method successful" ;
    public static final String END_VALIDATION_UNSUCCESSFUL = "End validation method with problem: {}" ;
    public static final String ERROR_PROPERTY = "I/O error to read property file" ;
}
