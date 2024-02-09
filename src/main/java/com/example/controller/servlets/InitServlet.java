package com.example.controller.servlets;

import com.example.model.vo.Group;
import com.example.model.vo.Student;
import com.example.model.vo.Teacher;
import com.example.repository.RepositoryFacade;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet(name = "InitServlet", urlPatterns = "/init/*")
public class InitServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        RepositoryFacade repo = new RepositoryFacade();
        for (int i = 1; i < 20; i++) {
            String stdfn = String.format("Student FN %d", i);
            String stdmn = String.format("Student MN %d", i);
            String stdsn = String.format("Student SN %d", i);
            String phone = String.format("+799916%d", 54421 + i);
            LocalDate bd = LocalDate.of(2000 + (int) (Math.random() * 20), 1 + (int) (Math.random() * 10), 1 + (int) (Math.random() * 27));
            repo.addStudent(new Student(stdfn, stdmn, stdsn, bd, phone));
        }
        for (int i = 1; i < 6; i++) {
            String tfn = String.format("Teacher FN %d", i);
            String tmn = String.format("Teacher MN %d", i);
            String tsn = String.format("Teacher SN %d", i);
            String phone = String.format("+799916%d", 52425 + i);
            LocalDate exp = LocalDate.of(2000 + (int) (Math.random() * 20), 1 + (int) (Math.random() * 10), 1 + (int) (Math.random() * 27));
            LocalDate bd = LocalDate.of(1964 + (int) (Math.random() * 20), 1 + (int) (Math.random() * 10), 1 + (int) (Math.random() * 27));
            repo.addTeacher(new Teacher(tfn, tmn, tsn, bd, exp, phone));
            repo.addGroup(new Group(String.format("Best-Group %d", i)));
        }
    }
}
