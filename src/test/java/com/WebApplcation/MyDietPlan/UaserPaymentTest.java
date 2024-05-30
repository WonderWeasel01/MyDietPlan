package com.WebApplcation.MyDietPlan;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import com.WebApplcation.MyDietPlan.Repository.MyDietPlanRepository;



public class UaserPaymentTest {


    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private MyDietPlanRepository Repo;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testIsActiveMember_whenUserIsActive() {
        int userId = 1;
        String sql = "SELECT subscriptionStatus FROM Subscription WHERE user_id = ?";
        when(jdbcTemplate.queryForObject(sql, new Object[]{userId}, Boolean.class)).thenReturn(true);

        boolean isActive = Repo.isActiveMember(userId);

        assertTrue(isActive);
    }

    @Test
    public void testIsActiveMember_whenUserIsNotActive() {
        int userId = 2;
        String sql = "SELECT subscriptionStatus FROM Subscription WHERE user_id = ?";
        when(jdbcTemplate.queryForObject(sql, new Object[]{userId}, Boolean.class)).thenReturn(false);

        boolean isActive = Repo.isActiveMember(userId);

        assertFalse(isActive);
    }

    @Test
    public void testIsActiveMember_whenQueryReturnsNull() {
        int userId = 3;
        String sql = "SELECT subscriptionStatus FROM Subscription WHERE user_id = ?";
        when(jdbcTemplate.queryForObject(sql, new Object[]{userId}, Boolean.class)).thenReturn(null);

        boolean isActive = Repo.isActiveMember(userId);

        assertFalse(isActive);
    }
}





