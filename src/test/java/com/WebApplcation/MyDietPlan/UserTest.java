package com.WebApplcation.MyDietPlan;

import com.WebApplcation.MyDietPlan.Exception.InputErrorException;
import com.WebApplcation.MyDietPlan.Exception.SystemErrorException;
import com.WebApplcation.MyDietPlan.Entity.User;
import com.WebApplcation.MyDietPlan.Repository.MyDietPlanRepository;
import com.WebApplcation.MyDietPlan.Service.AuthenticationService;
import com.WebApplcation.MyDietPlan.Service.WebsiteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;





public class UserTest {

    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private MyDietPlanRepository repo;
    @Mock
    private AuthenticationService authenticationService;
    @InjectMocks
    private WebsiteService websiteService;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testCreateUser() {
        MyDietPlanRepository ur = new MyDietPlanRepository(jdbcTemplate);
        User user = new User("Alex", "Wentzel");
    }

    
    // A unit test to test if our deleteUser method works.


    @Test
    public void testDeleteUser() {
        MyDietPlanRepository myDietPlanRepository;
        myDietPlanRepository = Mockito.mock(MyDietPlanRepository.class);
        int userIdToDelete = 1;

        myDietPlanRepository.deleteUser(userIdToDelete);

        verify(myDietPlanRepository).deleteUser(userIdToDelete);
    }


    @Test
    public void testUpdateUser() {

        MyDietPlanRepository mockedMyDietPlanRepository = mock(MyDietPlanRepository.class);
        int userId = 1;
        User updatedUser = new User("Simon", "Hellemose");
        updatedUser.setUserId(userId);

        mockedMyDietPlanRepository.updateUser(updatedUser);
        verify(mockedMyDietPlanRepository).updateUser(eq(updatedUser));


    }
    @Test
    public void testUpdateUser_ValidUser() throws InputErrorException, SystemErrorException {

        // Arrange
        User user = new User("username", "password");
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setEmail("test@example.com");
        user.setPassword("Password");
        user.setGoal("Goal");
        user.setActivityLevel("ActivityLevel");
        user.setGender('M');
        user.setWeight(100);
        user.setHeight(200);
        user.setAge(50);
        user.setRole("Role");
        user.setDailyCalorieBurn(2500);
        user.setDailyCalorieGoal(2000);
        user.setUserId(1);


        // Mock behavior of authentication service
        when(authenticationService.isValidUser(user)).thenReturn(true);

        // Mock behavior of repository
        when(repo.updateUser(user)).thenReturn(user);

        System.out.println(user);
        // Act
        User updatedUser = websiteService.updateUser(user);


        // Assert
        assertNotNull(updatedUser);
        assertEquals(user, updatedUser);
        verify(authenticationService).hashAndSetPassword(user);
        verify(user).setupDailyCalorieGoal();
        verify(authenticationService).setSession(updatedUser);

    }






    


}
