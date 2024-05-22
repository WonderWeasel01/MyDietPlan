package com.WebApplcation.MyDietPlan;

import com.WebApplcation.MyDietPlan.Model.Ingredient;
import com.WebApplcation.MyDietPlan.Model.Recipe;
import com.WebApplcation.MyDietPlan.Model.User;
import com.WebApplcation.MyDietPlan.Repository.MyDietPlanRepository;
import com.WebApplcation.MyDietPlan.Service.AuthenticationService;
import com.WebApplcation.MyDietPlan.Service.WebsiteService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class RecipeTest {

	@InjectMocks
	private MyDietPlanRepository myDietPlanRepository;

	@Mock
	private JdbcTemplate jdbcTemplate;
	@Mock
	private HttpSession session;
	@Mock
	private Recipe testRecipe;
	@Mock
	private MyDietPlanRepository repository;
	private Ingredient testIngredient;
	private Ingredient testIngredient1;
	@Mock
	private AuthenticationService authenticationService = new AuthenticationService(session,repository);
	@Mock
	private WebsiteService websiteService = new WebsiteService(authenticationService,repository);

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this); // Initializes objects annotated with Mockito annotations
		testIngredient = new Ingredient("Apple", 25, 0, 0, 52, 100);
		testIngredient1 = new Ingredient("Chicken", 0, 2, 21, 105, 200);
		ArrayList<Ingredient> ingredientList = new ArrayList<>();
		ingredientList.add(testIngredient1);
		ingredientList.add(testIngredient);
		testRecipe.setIngredientList(ingredientList);
		// Additional logging to confirm setup success
		System.out.println("Setup complete with Recipe: " + testRecipe);
	}

	@Test
	public void testCreateRecipe() {
		// Prepare the mocks
		GeneratedKeyHolder keyHolder = mock(GeneratedKeyHolder.class);
		when(jdbcTemplate.update(any(), any(KeyHolder.class))).thenAnswer(invocation -> {
			PreparedStatement ps = invocation.getArgument(0, PreparedStatementCreator.class).createPreparedStatement(null);
			ps.executeUpdate();
			return 1; // Ensures that update is considered executed
		});
		when(keyHolder.getKey()).thenReturn(1);

		// Execute the method
		Recipe result = myDietPlanRepository.createRecipe(testRecipe);

		// Assert and verify
		assertNotNull(result);
		assertEquals(1,result.getIngredientList().size());
		assertEquals(1, result.getRecipeID());
		verify(jdbcTemplate, times(1)).update(any(), any(KeyHolder.class)); // Verifying interaction
		verify(jdbcTemplate, times(testRecipe.getIngredientList().size())).update(anyString(), eq(1), anyInt()); // Verifying ingredients update

		System.out.println("Test completed successfully with Recipe ID: " + result.getRecipeID());
	}

	@Test
	public void testAdjustRecipeToUser(){
		double calorieGoal = 2000.0;
		//Delta is used to accept small errors due to long decimals
		double delta = 0.0001;
		Recipe adjustedRecipe = websiteService.adjustRecipeToUser(testRecipe, calorieGoal);
		assertEquals(calorieGoal/3, adjustedRecipe.getTotalCalories(), delta);

	}





}

