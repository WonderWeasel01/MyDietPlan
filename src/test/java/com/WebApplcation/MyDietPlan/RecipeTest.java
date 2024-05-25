package com.WebApplcation.MyDietPlan;
import com.WebApplcation.MyDietPlan.Entity.Ingredient;
import com.WebApplcation.MyDietPlan.Entity.Recipe;
import com.WebApplcation.MyDietPlan.Exception.SystemErrorException;
import com.WebApplcation.MyDietPlan.Repository.MyDietPlanRepository;
import com.WebApplcation.MyDietPlan.UseCase.AuthenticationService;
import com.WebApplcation.MyDietPlan.UseCase.WebsiteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;


class RecipeTest {
	@Mock
	private MyDietPlanRepository repo;
	@Mock
	private AuthenticationService authenticationService;
	@InjectMocks
	private WebsiteService websiteService;
	private Recipe recipe;
	private double dailyCalorieGoal;
	Ingredient ingredient1;
	Ingredient ingredient2;
	Ingredient ingredient3;
	Ingredient ingredient4;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		//Initialise the calorie goal.
		dailyCalorieGoal = 2146;

		// Create sample ingredients. All numbers used are from an example given by Minkostplan.
		 ingredient1 = new Ingredient("Oats", 40, 16.9, 6.9, 66, 389.0);
		 ingredient2 = new Ingredient("Skim milk", 150, 3.5, 0.1, 3.5, 132);
		 ingredient3 = new Ingredient("Apple", 100, 0.3, 0.2, 14, 52);
		 ingredient4 = new Ingredient("Honey", 10, 0.3, 0.2, 82.4, 304);

		//Add the ingredients to a list
		ArrayList<Ingredient> ingredients = new ArrayList<>();
		ingredients.add(ingredient1);
		ingredients.add(ingredient2);
		ingredients.add(ingredient3);
		ingredients.add(ingredient4);

		// Create sample recipe
		recipe = new Recipe();
		recipe.setRecipeID(1);
		recipe.setTitle("Overnight oats");
		recipe.setPrepTime("20 min");
		recipe.setTimeOfDay("Breakfast");
		recipe.setInstructions("mix ingredients and let rest over night");
		recipe.setIngredientList(ingredients);

		//Calculate and set the macros
		websiteService.calculateAndSetMacros(recipe);
	}

	@Test
	void testCalculateAndSetMacros() {
		double expectedProtein =
				(ingredient1.getProteinPerHundredGrams() * ingredient1.getWeightGrams() / 100) +
						(ingredient2.getProteinPerHundredGrams() * ingredient2.getWeightGrams() / 100) +
						(ingredient3.getProteinPerHundredGrams() * ingredient3.getWeightGrams() / 100) +
						(ingredient4.getProteinPerHundredGrams() * ingredient4.getWeightGrams() / 100);

		double expectedFat =
				(ingredient1.getFatPerHundredGrams() * ingredient1.getWeightGrams() / 100) +
						(ingredient2.getFatPerHundredGrams() * ingredient2.getWeightGrams() / 100) +
						(ingredient3.getFatPerHundredGrams() * ingredient3.getWeightGrams() / 100) +
						(ingredient4.getFatPerHundredGrams() * ingredient4.getWeightGrams() / 100);

		double expectedCarbohydrates =
				(ingredient1.getCarbohydratesPerHundredGrams() * ingredient1.getWeightGrams() / 100) +
						(ingredient2.getCarbohydratesPerHundredGrams() * ingredient2.getWeightGrams() / 100) +
						(ingredient3.getCarbohydratesPerHundredGrams() * ingredient3.getWeightGrams() / 100) +
						(ingredient4.getCarbohydratesPerHundredGrams() * ingredient4.getWeightGrams() / 100);

		double expectedCalories =
				(ingredient1.getCaloriesPerHundredGrams() * ingredient1.getWeightGrams() / 100) +
						(ingredient2.getCaloriesPerHundredGrams() * ingredient2.getWeightGrams() / 100) +
						(ingredient3.getCaloriesPerHundredGrams() * ingredient3.getWeightGrams() / 100) +
						(ingredient4.getCaloriesPerHundredGrams() * ingredient4.getWeightGrams() / 100);

		// Call the method to be tested
		websiteService.calculateAndSetMacros(recipe);

		// Verify the calculations
		assertEquals(expectedProtein,recipe.getTotalProtein(), 1);
		assertEquals(expectedFat,recipe.getTotalFat(), 1);
		assertEquals(expectedCarbohydrates,recipe.getTotalCarbohydrates(),1);
		assertEquals(expectedCalories,recipe.getTotalCalories(),1);

		// Call the method again to ensure consistent results
		websiteService.calculateAndSetMacros(recipe);

		// Verify the calculations again
		assertEquals(expectedProtein,recipe.getTotalProtein(), 1);
		assertEquals(expectedFat,recipe.getTotalFat(), 1);
		assertEquals(expectedCarbohydrates,recipe.getTotalCarbohydrates(),1);
		assertEquals(expectedCalories,recipe.getTotalCalories(),1);
	}

	@Test
	void testAdjustRecipesToUser() {
		//Call the adjustRecipeToUser Method
		Recipe adjustedRecipe = websiteService.adjustRecipeToUser(recipe,dailyCalorieGoal);
		//Assert that the weight have been scaled properly
		assertEquals(77,adjustedRecipe.getIngredientList().get(0).getWeightGrams(),3);
		assertEquals(292, adjustedRecipe.getIngredientList().get(1).getWeightGrams(), 3);
		assertEquals(198, adjustedRecipe.getIngredientList().get(2).getWeightGrams(), 3);
		assertEquals(23, adjustedRecipe.getIngredientList().get(3).getWeightGrams(), 3);

		//Assert that the total calories in the recipe is correct after ingredient adjustment.
		assertEquals(858, adjustedRecipe.getTotalCalories(), 5);
	}

	@Test
	void deleteRecipeFailedTest() throws SystemErrorException {

		//Mock the deleteRecipe method in MyDietPlanRepository to return false when called.
		when(repo.deleteRecipe(recipe.getRecipeID())).thenReturn(false);
		//Call the method to be tested
		boolean result = websiteService.deleteRecipe(recipe.getRecipeID());
		//Assert that websiteservice.deleteRecipe also return false
		assertFalse(result);
	}

	@Test
	public void testDeleteRecipe_ThrowsSystemErrorException_WhenEmptyResultDataAccessException() {
		// Configure the mock to throw EmptyResultDataAccessException
		doThrow(new EmptyResultDataAccessException(1)).when(repo).deleteRecipe(recipe.getRecipeID());

		// Assert that SystemErrorException is thrown
		SystemErrorException exception = assertThrows(SystemErrorException.class, () -> {
			websiteService.deleteRecipe(recipe.getRecipeID());
		});

		//Assert that the correct error message is thrown.
		assertEquals("Der er sket en fejl. Prøv igen senere", exception.getMessage());
	}


}

