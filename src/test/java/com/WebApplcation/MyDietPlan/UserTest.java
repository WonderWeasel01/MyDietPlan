package com.WebApplcation.MyDietPlan;

import com.WebApplcation.MyDietPlan.Model.User;
import com.WebApplcation.MyDietPlan.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserTest {

    @Mock
    private JdbcTemplate jdbcTemplate;


    @InjectMocks
    private UserRepository userRepository;
    private org.mockito.Mockito Mockito;

    @Test
    public void testCreateUser() {
        UserRepository ur = new UserRepository(jdbcTemplate);
        User user = new User("Alex", "Wentzel");
    }

    /**
     * A unit test to test if our deleteUser method works.
     */

    @Test
    public void testDeleteUser() {
        UserRepository userRepository;
        userRepository = Mockito.mock(UserRepository.class);
        int userIdToDelete = 1;

        userRepository.deleteUser(userIdToDelete);

        Mockito.verify(userRepository).deleteUser(userIdToDelete);
    }


    @Test
    public void testUpdateUser() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        UserRepository mockedUserRepository = Mockito.mock(UserRepository.class);

        int userId = 123;
        User updatedUser = new User("Simon", "Hellemose");

        mockedUserRepository.updateUser(userId, updatedUser);


        verify(mockedUserRepository).updateUser(eq(userId), eq(updatedUser));

        verify(jdbcTemplate, never()).update(anyString(), any(Object[].class));


    }



    public void testGetUserIdAfterUpdate() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        UserRepository userRepository = new UserRepository(jdbcTemplate);
        int userId = 123;
        User updatedUser = new User("Simon", "Hellemose");

        userRepository.updateUser(userId,  updatedUser);

        verify(userRepository).getUserByID(userId);
    }

    private JdbcTemplate mock(Class<JdbcTemplate> jdbcTemplateClass) {
        return null;
    }

}
