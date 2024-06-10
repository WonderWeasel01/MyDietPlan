package com.WebApplcation.MyDietPlan.UseCase;

import com.WebApplcation.MyDietPlan.Exception.EntityNotFoundException;
import com.WebApplcation.MyDietPlan.Exception.InputErrorException;
import com.WebApplcation.MyDietPlan.Exception.SystemErrorException;
import com.WebApplcation.MyDietPlan.Entity.User;
import com.WebApplcation.MyDietPlan.Repository.MyDietPlanRepository;
import com.WebApplcation.MyDietPlan.UseCase.AuthenticationService;
import com.WebApplcation.MyDietPlan.UseCase.WebsiteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
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
    @Mock
    private InputErrorException inputErrorException;
    @InjectMocks
    private WebsiteService websiteService;

    @Mock
    private DuplicateKeyException duplicateKeyException;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testCreateUser() {
        MyDietPlanRepository ur = new MyDietPlanRepository(jdbcTemplate);
        User userTest = new User("Alex", "Wentzel");
        System.out.println(userTest);
    }
    @Test
    void testSaveUser() throws SystemErrorException, InputErrorException {
        // Arrange
        User user = new User();
        user.setUserId(1);
        user.setFirstName("John");
        user.setLastName("Joe");

        User savedUser = new User();
        savedUser.setUserId(1);
        savedUser.setFirstName("John");
        savedUser.setLastName("Doe");

        when(repo.createUser(any(User.class))).thenReturn(savedUser);

        // Act
        User result = websiteService.saveUser(user);

        // Assert
        verify(repo).createUser(user);
        assertEquals(savedUser, result);
        System.out.println(result);
        System.out.println(savedUser);
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
    public void testUpdateUser_ValidUser() throws InputErrorException, SystemErrorException, EntityNotFoundException {

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
        when(repo.updateUser(user)).thenReturn(true);
        when(repo.getUserByID(user.getUserId())).thenReturn(user);
        System.out.println(user);

        User updatedUser = websiteService.getUserByID(user.getUserId());

        assertNotNull(updatedUser);
        assertEquals(user, updatedUser);
        verify(authenticationService).hashAndSetPassword(user);
        verify(authenticationService).setSession(updatedUser);

    }
    @Test
    public void testUpdateUser_InvalidUser() {
        // Arrange
        User user = new User();

        // Mock behavior of authentication service
        when(authenticationService.isValidUser(user)).thenReturn(false);
        System.out.println(user);

        // Act & Assert
        assertThrows(InputErrorException.class, () -> websiteService.updateUser(user));
        verify(authenticationService).isValidUser(user);
        verifyNoInteractions(repo);
    }

    @Test
    public void testUpdateUser_DuplicateKeyError() {
        // Arrange
        User user = new User("username", "password");
        user.setEmail("test@example.com");

        // Mock behavior of authentication service
        when(authenticationService.isValidUser(user)).thenReturn(true);

        // Mock behavior of repository throwing an exception
        when(repo.updateUser(user)).thenThrow(new DuplicateKeyException("Duplicate entry"));

        // Act & Assert
        assertThrows(InputErrorException.class, () -> websiteService.updateUser(user));
        verify(authenticationService).isValidUser(user);
        verify(repo).updateUser(user);
        verifyNoMoreInteractions(authenticationService);
    }





    


}
