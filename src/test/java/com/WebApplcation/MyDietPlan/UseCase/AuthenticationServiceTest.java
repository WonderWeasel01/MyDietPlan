package com.WebApplcation.MyDietPlan.UseCase;

import com.WebApplcation.MyDietPlan.Entity.Subscription;
import com.WebApplcation.MyDietPlan.Entity.User;
import com.WebApplcation.MyDietPlan.Exception.EntityNotFoundException;
import com.WebApplcation.MyDietPlan.Exception.SystemErrorException;
import com.WebApplcation.MyDietPlan.Repository.MyDietPlanRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;
    @Mock
    private MyDietPlanRepository repository;

    @Mock
    HttpSession session;

    Subscription mockSubscription;
    User mockUser;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockSubscription = new Subscription();

        mockUser = new User();
        mockUser.setUserId(1);
    }

    @Test
    public void testLogout() {
        authenticationService.setSession(mockUser);
        // Act: Call the logout method
        authenticationService.logout();

        // Assert: Check if the user is set to null
        assertNull(authenticationService.getUser(), "The user should be null after logout.");
    }

    @Test
    public void testIsPayingUserRenewsExpiredActiveSubscription() throws SystemErrorException, EntityNotFoundException {
        mockSubscription.setActiveSubscription(true);
        mockSubscription.setSubscriptionEndDate(Date.valueOf(LocalDate.now().minusDays(1)));

        //Mock authenticationService and repository
        when(authenticationService.getUser()).thenReturn(mockUser);
        when(repository.getSubscriptionByUserID(authenticationService.getUser().getUserId())).thenReturn(mockSubscription);


        //Call the method that is being tested
        authenticationService.isPayingUser();

        Date expectedEndDate = Date.valueOf(LocalDate.now().plusMonths(1));
        // Assert that the subscription end date was extended by one month
        assertEquals(expectedEndDate, mockSubscription.getSubscriptionEndDate());
    }

    @Test
    public void testIsPayingUserExpiredInactiveSubscription() throws SystemErrorException, EntityNotFoundException {
        mockSubscription.setActiveSubscription(false);
        mockSubscription.setSubscriptionEndDate(Date.valueOf(LocalDate.now().minusDays(1)));

        //Mock authenticationService and repository
        when(authenticationService.getUser()).thenReturn(mockUser);
        when(repository.getSubscriptionByUserID(authenticationService.getUser().getUserId())).thenReturn(mockSubscription);

        // Assert that the subscription end date was extended by one month
        assertFalse(authenticationService.isPayingUser());
    }
    @Test
    public void testIsPayingUserNoSubscriptionFound() throws SystemErrorException, EntityNotFoundException {
        //Mock authenticationService and repository
        when(authenticationService.getUser()).thenReturn(mockUser);
        when(repository.getSubscriptionByUserID(authenticationService.getUser().getUserId())).thenThrow(EmptyResultDataAccessException.class);

        // Assert that the subscription end date was extended by one month
        assertFalse(authenticationService.isPayingUser());
    }

}
