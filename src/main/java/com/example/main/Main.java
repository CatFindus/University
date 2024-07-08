package com.example.main;

import com.example.repository.RepositoryFacade;
import com.example.utils.SessionManager;
import org.hibernate.Session;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Session session = SessionManager.getInstance().open();
        RepositoryFacade facade = new RepositoryFacade(session);
        System.out.println(facade.getSchedulesCountByGroupPerDay(35, LocalDate.of(2023,10,11)));
        session.close();
    }
}
