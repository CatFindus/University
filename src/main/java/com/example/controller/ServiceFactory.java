package com.example.controller;

import com.example.mapper.EntityMapperImpl;
import com.example.model.service.*;
import com.example.repository.RepositoryFacade;
import com.example.utils.SessionManager;
import org.hibernate.Session;

public class ServiceFactory {
    public static synchronized  <T> T getService(Class<T> clazz) {
        Session session = SessionManager.getInstance().open();
        RepositoryFacade repo = new RepositoryFacade(session);
        if (clazz == StudentService.class) return (T) new StudentService(repo, session, new EntityMapperImpl());
        else if (clazz == TeacherService.class) return (T) new TeacherService(repo, session, new EntityMapperImpl());
        else if (clazz == GroupService.class)
            return (T) new GroupService(repo, new EntityMapperImpl(), session, new StudentService(repo, session, new EntityMapperImpl()));
        else if (clazz == ScheduleService.class) {
            //GroupService groupService = new GroupService(repo, new GroupMapperImpl(), session, new StudentService(repo, session, new StudentMapperImpl()));
            //TeacherService teacherService = new TeacherService(repo, session, new TeacherMapperImpl());
            ScheduleService service = new ScheduleService(repo, new EntityMapperImpl(), session);
            return (T) service;
        } else return null;
    }


}
