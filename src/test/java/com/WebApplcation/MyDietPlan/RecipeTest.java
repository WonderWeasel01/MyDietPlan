package com.WebApplcation.MyDietPlan;

import com.WebApplcation.MyDietPlan.Model.Ingredient;
import com.WebApplcation.MyDietPlan.Model.Recipe;
import com.WebApplcation.MyDietPlan.Repository.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.ArrayList;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class RecipeTest {
	@Mock
	private JdbcTemplate jdbcTemplate;
	@Mock
	private GeneratedKeyHolder keyHolder;

	@InjectMocks
	private RecipeRepository recipeRepository;


	@Test
	public void testCreateRecipeMorning() {

		Ingredient chickenBreast = new Ingredient("Chicken",0,2,20,98,200);
		Ingredient salat = new Ingredient("Salat",3,1,0,15,100);

		ArrayList<Ingredient> ingredientList = new ArrayList<>();
		ingredientList.add(chickenBreast);
		ingredientList.add(salat);

		Recipe recipe = new Recipe(540,20,60,30,"Morgen", "Boil", ingredientList,false);

		// Mock the jdbcTemplate.update method to return 1. 1 = succesful insertion in database
		when(jdbcTemplate.update(any(), any(), any())).thenReturn(1);

		// Mock the keyHolder.getKey method to return 123. Simulates that the auto generated key in the database = 123
		when(keyHolder.getKey()).thenReturn(123);

		Recipe result = recipeRepository.createRecipe(recipe);

		// Assert if the original recipe is equal to the recipe returned by createRecipe().
		assertEquals(recipe.getIngredientList(),result.getIngredientList());
		assertEquals(123,result.getRecipeID());


	}

	/*
	@Test
	public void testCreateRecipeWithNullGeneratedKey() {
		Ingredient chickenBreast = new Ingredient("Chicken",0,2,20,98,200);
		Ingredient salat = new Ingredient("Salat",3,1,0,15,100);

		ArrayList<Ingredient> ingredientList = new ArrayList<>();
		ingredientList.add(chickenBreast);
		ingredientList.add(salat);

		Recipe recipe = new Recipe(540,20,60,30,"Morgen", "Boil", ingredientList,false);

		when(jdbcTemplate.update(any(), any(), any())).thenReturn(1);
		when(jdbcTemplate.update(any(), any(), any(), any(KeyHolder.class))).thenReturn(0); // Simulate null generated key

		// Act
		Recipe result = yourClass.createRecipe(recipe);

		// Assert
		// Add assertions to verify that the method handles null generated key appropriately
	}


	 */




}

