package com.example.model.service;

import com.example.consts.ModelConstants;
import com.example.exeptions.IncorrectRequestException;
import com.example.exeptions.NoDataException;
import com.example.mapper.GroupMapperImpl;
import com.example.mapper.StudentMapperImpl;
import com.example.model.dto.Request.GroupRequest;
import com.example.model.dto.Response.DtoResponse;
import com.example.model.dto.Response.GroupResponse;
import com.example.model.vo.Group;
import com.example.repository.RepositoryFacade;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class GroupServiceTest {
    RepositoryFacade repo = Mockito.mock(RepositoryFacade.class);
    Group group1 = new Group("Test1");
    Group group2 = new Group("Test2");
    Service service = new GroupService(repo, new GroupMapperImpl(), new StudentService(repo, new StudentMapperImpl()));

    @Test
    @SneakyThrows
    void getDataById() {
        Mockito.when(repo.getGroup(group1.getId())).thenReturn(group1);
        assertEquals(group1, service.getDataById(group1.getId().toString()).get(0));
        assertThrows(IncorrectRequestException.class, () -> service.getDataById("badId"));
        Mockito.verify(repo, Mockito.times(1)).getGroup(Mockito.anyInt());
    }

    @Test
    @SneakyThrows
    void getDataByParameters() {
        Mockito.when(repo.getGroups(Mockito.any(List.class))).thenReturn(List.of(group1));
        String[] goodValue = {group1.getNumber()};
        Map<String, String[]> map = new HashMap<>();
        map.put(ModelConstants.RQ_NUMBER, goodValue);
        assertEquals(group1, service.getDataByParameters(map).get(0));
        map.put("badKey", goodValue);
        assertThrows(IncorrectRequestException.class, () -> service.getDataByParameters(map));
    }

    @Test
    void mappingVoToDto() {
        List<DtoResponse> responses = service.mappingVoToDto(List.of(group1, group2));
        GroupResponse gr1 = (GroupResponse) responses.get(0);
        GroupResponse gr2 = (GroupResponse) responses.get(1);
        assertEquals(group1.getNumber(), gr1.getNumber());
        assertEquals(group2.getNumber(), gr2.getNumber());
    }

    @Test
    void create() {
        Mockito.when(repo.addGroup(group1)).thenReturn(true);
        Mockito.when(repo.addGroup(group2)).thenReturn(false);

        assertTrue(service.create(group1));
        assertFalse(service.create(group2));
    }

    @Test
    @SneakyThrows
    void update() {
        Mockito.when(repo.getGroup(group1.getId())).thenReturn(group1);
        String updatedName = "updatedName";
        GroupRequest groupRequest = new GroupRequest();
        groupRequest.setNumber(updatedName);
        assertNotEquals(updatedName, group1.getNumber());
        service.update(group1.getId().toString(), groupRequest);
        assertEquals(updatedName, group1.getNumber());
    }

    @Test
    void delete() {
        Mockito.when(repo.removeGroup(group1.getId())).thenReturn(true);
        Mockito.when(repo.removeGroup(group2.getId())).thenReturn(false);
        assertDoesNotThrow(()->service.delete(group1.getId().toString()));
        assertThrows(NoDataException.class, ()->service.delete(group2.getId().toString()));
        assertThrows(IncorrectRequestException.class, ()-> service.delete("badPath"));
    }
}