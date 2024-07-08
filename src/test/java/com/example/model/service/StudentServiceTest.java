package com.example.model.service;

import com.example.consts.ModelConstants;
import com.example.exeptions.IncorrectRequestException;
import com.example.exeptions.NoDataException;
import com.example.mapper.StudentMapperImpl;
import com.example.model.dto.Request.StudentRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.dto.Response.StudentResponse;
import com.example.model.vo.Student;
import com.example.repository.RepositoryFacade;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StudentServiceTest {

    RepositoryFacade repo = Mockito.mock(RepositoryFacade.class);
    Student student1 = new Student("std1fn", "stdmn", "std1sn", LocalDate.now(), "+71234567890");
    Student student2 = new Student("std2fn", "stdmn", "std2sn", LocalDate.now(), "+71234567890");
    static Map<String, String[]> map = new HashMap<>();
    Service service = new StudentService(repo, new StudentMapperImpl());

    @BeforeAll
    static void init() {
        String[] strings = {"stdmn"};
        map.put(ModelConstants.RQ_MIDDLE_NAME, strings);
    }

    @Test
    @SneakyThrows
    void getDataById() {
        Mockito.when(repo.getStudent(student1.getId())).thenReturn(student1);
        Mockito.when(repo.getStudent(student2.getId())).thenReturn(student2);

        assertEquals(student1, service.getDataById(Integer.toString(student1.getId())).get(0));
        assertEquals(1, service.getDataById(Integer.toString(student1.getId())).size());
        assertEquals(student2, service.getDataById(Integer.toString(student2.getId())).get(0));
        assertEquals(1, service.getDataById(Integer.toString(student2.getId())).size());

        assertThrows(IncorrectRequestException.class, () -> service.getDataById("badId"));
    }

    @Test
    @SneakyThrows
    void getDataByParameters() {
        Mockito.when(repo.getStudents(Mockito.any())).thenReturn(List.of(student1,student2));
        assertEquals(student1, service.getDataByParameters(map).get(0));
        assertEquals(student2, service.getDataByParameters(map).get(1));
        assertEquals(2, service.getDataByParameters(map).size());
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
        Mockito.when(repo.addStudent(student1)).thenReturn(true);
        Mockito.when(repo.addStudent(student2)).thenReturn(false);

        assertTrue(service.create(student1));
        assertFalse(service.create(student2));
    }

    @Test
    @SneakyThrows
    void update() {
        Mockito.when(repo.getStudent(student1.getId())).thenReturn(student1);
        String updatedName = "updatedName";
        StudentRequest studentRequest = new StudentRequest();
        studentRequest.setFirstName(updatedName);

        assertNotEquals(updatedName, student1.getFirstName());

        service.update(Integer.toString(student1.getId()), studentRequest);
        assertEquals(updatedName, student1.getFirstName());

        Mockito.when(repo.getStudent(student1.getId())).thenReturn(null);
        assertThrows(NoDataException.class, () -> service.update(Integer.toString(student1.getId()), studentRequest));

    }

    @Test
    void delete() {
        Mockito.when(repo.getStudent(student1.getId())).thenReturn(student1);
        Mockito.when(repo.removeStudent(student1.getId())).thenReturn(true);
        Mockito.when(repo.getGroupIdByStudent(student1)).thenReturn(null);
        assertDoesNotThrow(() -> service.delete(Integer.toString(student1.getId())));

        Mockito.when(repo.getStudent(student2.getId())).thenReturn(student2);
        Mockito.when(repo.removeStudent(student2.getId())).thenReturn(false);
        Mockito.when(repo.getGroupIdByStudent(student2)).thenReturn(null);
        assertThrows(NoDataException.class, () -> service.delete(Integer.toString(student2.getId())));
    }
}