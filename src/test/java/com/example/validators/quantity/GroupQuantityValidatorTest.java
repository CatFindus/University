package com.example.validators.quantity;

class GroupQuantityValidatorTest {
    /*
    Group mockMaxCountGroup = Mockito.mock(Group.class);
    Group mockMinCountGroup = Mockito.mock(Group.class);
    HttpServletRequest mockPostReq = Mockito.mock(HttpServletRequest.class);
    HttpServletRequest mockDeleteReq = Mockito.mock(HttpServletRequest.class);
    static int max, min;
    static Properties properties = new Properties();
    @BeforeAll
    static void init() {
        try (InputStream is = Objects.requireNonNull(Student.class.getClassLoader().getResource(MODEL_PROPERTY)).openStream()) {
            properties.load(is);
        } catch (IOException ignored) {}
        max = Integer.parseInt(properties.getProperty(MAX_STUDENT_IN_GROUP));
        min = Integer.parseInt(properties.getProperty(MIN_STUDENT_IN_GROUP));
    }
    @Test
    void test_max_count_validation() {
        Mockito.when(mockPostReq.getMethod()).thenReturn(POST);
        Mockito.when(mockMaxCountGroup.getStudentsQuantity()).thenReturn(max);
        assertThrows(IncorrectRequestException.class, ()-> new GroupQuantityValidator(mockPostReq, mockMaxCountGroup).validate());
        Mockito.when(mockMaxCountGroup.getStudentsQuantity()).thenReturn(max-1);
        assertDoesNotThrow(()-> new GroupQuantityValidator(mockPostReq, mockMaxCountGroup));
    }
    @Test
    void test_min_count_validation() {
        Mockito.when(mockDeleteReq.getMethod()).thenReturn(DELETE);
        Mockito.when(mockMinCountGroup.getStudentsQuantity()).thenReturn(min);
        assertThrows(IncorrectRequestException.class, ()-> new GroupQuantityValidator(mockDeleteReq, mockMinCountGroup).validate());
        Mockito.when(mockMinCountGroup.getStudentsQuantity()).thenReturn(min+1);
        assertDoesNotThrow(()-> new GroupQuantityValidator(mockDeleteReq, mockMinCountGroup));
    }

     */
}