package com.WebApplcation.MyDietPlan;

import com.WebApplcation.MyDietPlan.Model.User;
import com.WebApplcation.MyDietPlan.Repository.MyDietPlanRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
@AutoConfigureMockMvc
public class UserTest {

    @Mock
    private JdbcTemplate jdbcTemplate;


    @InjectMocks
    private MyDietPlanRepository repo;
    private org.mockito.Mockito Mockito;

    @Test
    public void testCreateUser() {
        MyDietPlanRepository ur = new MyDietPlanRepository(jdbcTemplate);
        User user = new User("Alex", "Wentzel");
    }

    /**
     * A unit test to test if our deleteUser method works.

/*
    @Test
    public void testDeleteUser() {
        UserRepository userRepository;
        userRepository = Mockito.mock(UserRepository.class);
        int userIdToDelete = 1;

        userRepository.deleteUser(userIdToDelete);

        Mockito.verify(userRepository).deleteUser(userIdToDelete);
    }
*/

   /* @Test
    public void testUpdateUser() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        MyDietPlanRepository mockedMyDietPlanRepository = Mockito.mock(MyDietPlanRepository.class);

        int userId = 1;
        User updatedUser = new User("Simon", "Hellemose");

        mockedMyDietPlanRepository.updateUser(userId, updatedUser);


        verify(mockedMyDietPlanRepository).updateUser(eq(userId), eq(updatedUser));

        verify(jdbcTemplate, never()).update(anyString(), any(Object[].class));


    }



    public void testGetUserIdAfterUpdate() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        MyDietPlanRepository myDietPlanRepository = new MyDietPlanRepository(jdbcTemplate);
        int userId = 123;
        User updatedUser = new User("Simon", "Hellemose");

        MyDietPlanRepository.updateUser(userId,  updatedUser);

        verify(userRepository).getUserByID(userId);
    }

    private JdbcTemplate mock(Class<JdbcTemplate> jdbcTemplateClass) {
        return null;
    }*/


}
