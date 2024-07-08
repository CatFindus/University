package com.example.model.service;

import com.example.mapper.TeacherMapperImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {
    /*
    RepositoryFacade repo = Mockito.mock(RepositoryFacade.class);
    Teacher testTeacher = new Teacher("Тест", "Тестович", "Тестов", LocalDate.now(), LocalDate.now(), "+79991234567");
    Teacher testTeacher2 = new Teacher("Тест Второй", "Второтестович", "Второтестов", LocalDate.now(), LocalDate.now(), "+79991234567");
    Service service = new TeacherService(repo, new TeacherMapperImpl());

    @Test
    @SneakyThrows
    void getDataById() {
        Mockito.when(repo.getTeacher(testTeacher.getId())).thenReturn(testTeacher);
        assertEquals(testTeacher, service.getDataById(Integer.toString(testTeacher.getId())).get(0));
        assertThrows(IncorrectRequestException.class, () -> service.getDataById("badId"));
        Mockito.verify(repo, Mockito.times(1)).getTeacher(Mockito.any());
    }

    @Test
    @SneakyThrows
    void getDataByParameters() {
        Mockito.when(repo.getTeachers(Mockito.any())).thenReturn(List.of(testTeacher));
        String[] goodValue = {testTeacher.getId().toString()};
        Map<String, String[]> map = new HashMap<>();
        map.put(ModelConstants.RQ_FIRST_NAME, goodValue);
        assertEquals(testTeacher, service.getDataByParameters(map).get(0));
        map.put("badKey", goodValue);
        assertThrows(IncorrectRequestException.class, () -> service.getDataByParameters(map));
    }

    @Test
    void mappingVoToDto() {
        List<DtoResponse> responses = service.mappingVoToDto(List.of(testTeacher, testTeacher2));
        TeacherResponse tr1 = (TeacherResponse) responses.get(0);
        TeacherResponse tr2 = (TeacherResponse) responses.get(1);
        assertEquals(testTeacher.getId(), tr1.getId());
        assertEquals(testTeacher2.getId(), tr2.getId());
    }

    @Test
    void create() {
        Mockito.when(repo.addTeacher(testTeacher)).thenReturn(true);
        Mockito.when(repo.addTeacher(testTeacher2)).thenReturn(false);

        assertTrue(service.create(testTeacher));
        assertFalse(service.create(testTeacher2));
    }

    @Test
    @SneakyThrows
    void update() {
        Mockito.when(repo.getTeacher(testTeacher.getId())).thenReturn(testTeacher);
        String updatedName = "updatedName";
        TeacherRequest teacherRequest = new TeacherRequest();
        teacherRequest.setFirstName(updatedName);
        assertNotEquals(updatedName, testTeacher.getFirstName());
        service.update(testTeacher.getId().toString(), teacherRequest);
        assertEquals(updatedName, testTeacher.getFirstName());
    }

    @Test
    @SneakyThrows
    void delete() {
        Mockito.when(repo.removeTeacher(testTeacher.getId())).thenReturn(true);
        Mockito.when(repo.removeTeacher(testTeacher2.getId())).thenReturn(false);
        assertDoesNotThrow(()->service.delete(testTeacher.getId().toString()));
        assertThrows(NoDataException.class, ()->service.delete(testTeacher2.getId().toString()));
        assertThrows(IncorrectRequestException.class, ()-> service.delete("badPath"));
    }

     */
}