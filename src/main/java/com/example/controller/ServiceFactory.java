package com.example.controller;

import com.example.mapper.GroupMapperImpl;
import com.example.mapper.ScheduleMapperImpl;
import com.example.mapper.StudentMapperImpl;
import com.example.mapper.TeacherMapperImpl;
import com.example.model.service.*;
import com.example.repository.RepositoryFacade;

public class ServiceFactory {
    public static synchronized  <T> T getService(Class<T> clazz) {
        RepositoryFacade repo = new RepositoryFacade();
        if (clazz == StudentService.class) return (T) new StudentService(repo, new StudentMapperImpl());
        else if (clazz == TeacherService.class) return (T) new TeacherService(repo, new TeacherMapperImpl());
        else if (clazz == GroupService.class)
            return (T) new GroupService(repo, new GroupMapperImpl(), new StudentService(repo, new StudentMapperImpl()));
        else if (clazz == ScheduleService.class) {
            GroupService groupService = new GroupService(repo, new GroupMapperImpl(), new StudentService(repo, new StudentMapperImpl()));
            TeacherService teacherService = new TeacherService(repo, new TeacherMapperImpl());
            ScheduleService service = new ScheduleService(repo, new ScheduleMapperImpl(), groupService, teacherService);
            return (T) service;
        } else return null;
    }
}
