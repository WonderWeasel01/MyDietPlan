package com.WebApplcation.MyDietPlan;

import com.WebApplcation.MyDietPlan.Entity.User;
import com.WebApplcation.MyDietPlan.Service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertNull;

public class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService as;

    @Mock
    private User mockUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        as.setSession(mockUser);
    }

    @Test
    public void testLogout() {
        // Act: Call the logout method
        as.logout();

        // Assert: Check if the user is set to null
        assertNull(as.getUser(), "The user should be null after logout.");
    }
}
