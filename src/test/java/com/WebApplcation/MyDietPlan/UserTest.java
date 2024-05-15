package com.WebApplcation.MyDietPlan;

import com.WebApplcation.MyDietPlan.Model.User;
import com.WebApplcation.MyDietPlan.Repository.MyDietPlanRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    
    // A unit test to test if our deleteUser method works.


    @Test
    public void testDeleteUser() {
        MyDietPlanRepository myDietPlanRepository;
        myDietPlanRepository = Mockito.mock(MyDietPlanRepository.class);
        int userIdToDelete = 1;

        myDietPlanRepository.deleteUser(userIdToDelete);

        verify(myDietPlanRepository).deleteUser(userIdToDelete);
    }

    //Dette er en annotation der markerer metoden som en test.
    @Test
    //Dette er metodesigntaturen, det angiver det er en public - offentlig, void - den ikke returnerer noget og er navngivet testUpdateUser
    public void testUpdateUser() {
        //Her erklæres en variable med navnet jdbcTemplate af typen JdbcTemplate. mock er et metodekald, som opretter et mockobjekt - en lokal testkopi af klassen. Funktionen mock stilles til rådighed af vores importerede package Mockito til at oprette mockobjekter.
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        //Her erklæres en variable med navnet mockedUserRepository af typen MyDietPlanRepository. mock oprettet igen et mockobjekt af klassen MyDietRepository
        MyDietPlanRepository mockedMyDietPlanRepository = mock(MyDietPlanRepository.class);
        //Her opretter vi et eksempel på et bruger ID, ved at erklære en variable af typen int med navnet userId og tildeler den værdien 1.
        int userId = 1;
        //Her oprettes et ny User objekt med navnet updatedUser, med fornavnet Simon og efternavnet Hellemose. Dette objekt repræsenterer den opdaterede brugerinformation
        User updatedUser = new User("Simon", "Hellemose");
        //Her kaldes metoden updateUser på mockedMyDietPlanRepository og sender userId og updatedUser som argumenter. Dette simulerer opførslen af at opdatere en bruger i repository klassen
        mockedMyDietPlanRepository.updateUser(userId, updatedUser);
        //Her bliver får vi Mockito til at verificere at den valgte metode bliver kaldt på vores mockobjekt "mockedMyDietPlanRepository". eq bruges til at sikre at argumenterne der sendes til updateUser matcher de forventede værdier. Her verificeres det, at updateUser metoden bliver kaldt med userId og updatedUser som argumenter.
        verify(mockedMyDietPlanRepository).updateUser(eq(userId), eq(updatedUser));

        


    }



    


}
