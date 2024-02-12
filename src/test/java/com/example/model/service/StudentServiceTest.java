package com.example.model.service;

import com.example.consts.ModelConstants;
import com.example.exeptions.IncorrectRequestException;
import com.example.exeptions.NoDataException;
import com.example.model.dto.Request.StudentRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.dto.Response.StudentResponse;
import com.example.model.vo.Student;
import com.example.repository.RepositoryFacade;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.consts.ModelConstants.PATH_SEPARATOR;
import static org.junit.jupiter.api.Assertions.*;

class StudentServiceTest {
    static RepositoryFacade repo = new RepositoryFacade();
    static Student student1 = new Student("std1fn","stdmn", "std1sn", LocalDate.now(), "+71234567890");
    static Student student2 = new Student("std2fn","stdmn", "std2sn", LocalDate.now(), "+71234567890");
    static Map<String,String[]> map = new HashMap<>();
    static Service service = new StudentService();
    @BeforeAll
    static void init() {
        repo.addStudent(student1);
        repo.addStudent(student2);
        String[] strings = {"stdmn"};
        map.put(ModelConstants.RQ_MIDDLE_NAME, strings);
    }
    @Test
    void getDataById() {
        try {
            assertEquals(student1, service.getDataById(Integer.toString(student1.getId())).get(0));
            assertEquals(1, service.getDataById(Integer.toString(student1.getId())).size());
            assertEquals(student2, service.getDataById(Integer.toString(student2.getId())).get(0));
            assertEquals(1, service.getDataById(Integer.toString(student2.getId())).size());
        } catch (IncorrectRequestException ignored) {}

    }

    @Test
    void getDataByParameters() {
        try {
           assertEquals(student1, service.getDataByParameters(map).get(0));
           assertEquals(student2, service.getDataByParameters(map).get(1));
           assertEquals(2, service.getDataByParameters(map).size());
        } catch (IncorrectRequestException ignored) {}
    }

    @Test
    void mappingVoToDto() {
        List<DtoResponse> responses = service.mappingVoToDto(List.of(student1));
        StudentResponse sr = (StudentResponse) responses.get(0);
        assertEquals(student1.getFirstName(), sr.getFirstName());
        assertEquals(student1.getMiddleName(), sr.getMiddleName());
        assertEquals(student1.getSurName(), sr.getSurName());
        assertEquals(student1.getBirthDay(), sr.getBirthDay());
        assertEquals(student1.getPhoneNumber(), sr.getPhoneNumber());
    }

    @Test
    void create() {
        Student student3 = new Student("std3fn","sdsftdmn", "std3sn", LocalDate.now(), "+71234567890");
        assertTrue(service.create(student3));
        try{
            assertEquals(student3, service.getDataById(Integer.toString(student3.getId())).get(0));
        } catch (IncorrectRequestException ignored) {}
    }

    @Test
    void update() {
        String updatedName = "updatedName";
        StudentRequest sr = new StudentRequest();
        sr.setFirstName(updatedName);
        assertNotEquals(updatedName, student1.getFirstName());
        try {
            service.update(Integer.toString(student1.getId()), sr);
            assertEquals(updatedName, student1.getFirstName());
        } catch (IncorrectRequestException | NoDataException ignored) {}
    }

    @Test
    void delete() {
        try {
        assertFalse(service.getDataById(Integer.toString(student1.getId())).isEmpty());
        service.delete(PATH_SEPARATOR+student1.getId());
        assertTrue(service.getDataById(Integer.toString(student1.getId())).isEmpty());
        } catch (IncorrectRequestException | NoDataException ignored) {}
    }
}