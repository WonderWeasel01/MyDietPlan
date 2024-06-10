package com.WebApplcation.MyDietPlan.Controller;


import com.WebApplcation.MyDietPlan.Entity.User;
import com.WebApplcation.MyDietPlan.UseCase.AuthenticationService;
import com.WebApplcation.MyDietPlan.UseCase.WebsiteService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class WelcomePageTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    public void testWelcomePageLoadTime() throws Exception {
        //Start timer
        long startTime = System.currentTimeMillis();
        User mockUser = new User();
        mockUser.setDailyCalorieGoal(2000);

        //Mock authenticationservice
        when(authenticationService.isPayingUser()).thenReturn(true);
        when(authenticationService.isUserLoggedIn()).thenReturn(true);
        when(authenticationService.getUser()).thenReturn(mockUser);

        //Perform the get with mockMvc
        MvcResult result = mockMvc.perform(get("/velkommen"))
                .andExpect(status().isOk())
                .andReturn();

        //Stop timer
        long endTime = System.currentTimeMillis();
        //Calculate the load time
        long loadTime = endTime - startTime;

        System.out.println("Load time: " + loadTime + " ms");

        String responseContent = result.getResponse().getContentAsString();
        assertNotNull(responseContent);

        assertTrue(loadTime < 2000);


    }


}
