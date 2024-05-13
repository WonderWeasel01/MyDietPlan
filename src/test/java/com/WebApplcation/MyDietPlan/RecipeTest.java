package com.WebApplcation.MyDietPlan;

import com.WebApplcation.MyDietPlan.Model.Ingredient;
import com.WebApplcation.MyDietPlan.Model.Recipe;
import com.WebApplcation.MyDietPlan.Repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

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
	private RecipeRepository recipeRepository;

	@Mock
	private JdbcTemplate jdbcTemplate;

	private Recipe testRecipe;
	private Ingredient testIngredient;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this); // Initializes objects annotated with Mockito annotations
		testIngredient = new Ingredient("Apple", 25, 0, 0, 52, 150);
		testRecipe = new Recipe(200, 30, 10, 5, "Breakfast", "Chop apples and mix with yogurt", "30 mins", true, new ArrayList<>(Arrays.asList(testIngredient)) );

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
		Recipe result = recipeRepository.createRecipe(testRecipe);

		// Assert and verify
		assertNotNull(result);
		assertEquals(1,result.getIngredientList().size());
		assertEquals(1, result.getRecipeID());
		verify(jdbcTemplate, times(1)).update(any(), any(KeyHolder.class)); // Verifying interaction
		verify(jdbcTemplate, times(testRecipe.getIngredientList().size())).update(anyString(), eq(1), anyInt()); // Verifying ingredients update

		System.out.println("Test completed successfully with Recipe ID: " + result.getRecipeID());
	}





}

