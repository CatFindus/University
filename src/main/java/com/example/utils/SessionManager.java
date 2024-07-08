package com.example.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class SessionManager {
    private static final SessionManager INSTANCE = new SessionManager();
    private SessionFactory factory;

    private SessionManager() {
        factory = new Configuration().configure().buildSessionFactory();
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public Session open() {
        return factory.openSession();
    }
    public Session getCurrent() { return factory.getCurrentSession(); }

    public void  closeFactory() {
        if (!factory.isClosed()) factory.close();
    }

}
