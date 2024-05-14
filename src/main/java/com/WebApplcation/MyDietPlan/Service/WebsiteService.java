package com.WebApplcation.MyDietPlan.Service;

import com.WebApplcation.MyDietPlan.Exception.InputErrorException;
import com.WebApplcation.MyDietPlan.Exception.SystemErrorException;
import com.WebApplcation.MyDietPlan.Model.Ingredient;
import com.WebApplcation.MyDietPlan.Model.Recipe;
import com.WebApplcation.MyDietPlan.Model.User;
import com.WebApplcation.MyDietPlan.Repository.MyDietPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class WebsiteService {

    @Autowired
    private MyDietPlanRepository repo;

    public WebsiteService(MyDietPlanRepository myDietPlanRepository){
        this.repo = myDietPlanRepository;
    }

/*
    public List<Recipe> adjustRecipesForUser(List<Recipe> weeklyDietPlan){

    }


 */

    public Recipe createRecipe(Recipe recipe, List<Integer> ingredientIds, List<Integer> weights) throws SystemErrorException, InputErrorException {
        if(recipe == null) {
            throw new InputErrorException("Venligst udfyld alle felterne korrekt!");
        }
            try {
                ArrayList<Ingredient> ingredients = new ArrayList<>();
                for (int i = 0; i < ingredientIds.size(); i++) {
                    Ingredient ingredient = getIngredientByID(ingredientIds.get(i));
                    ingredient.setWeightGrams(weights.get(i));
                    ingredients.add(ingredient);
                }
                recipe.setIngredientList(ingredients);
                if (isValidRecipe(recipe)) {
                    recipe.calculateMacros();
                    return repo.createRecipe(recipe);
                } else throw new InputErrorException("Venligst udfyld alle felterne korrekt!");
            } catch (DataAccessException e) {
                System.err.println("Error accessing the database: " + e.getMessage());
                throw new SystemErrorException("Der er sket en fejl på vores side. Prøv igen senere.");
            }
    }
    public boolean isValidRecipe(Recipe recipe){
        return StringUtils.hasText(recipe.getTitle()) && StringUtils.hasText(recipe.getPrepTime()) && StringUtils.hasText(recipe.getTimeOfDay())
                && StringUtils.hasText(recipe.getInstructions()) && StringUtils.hasText(recipe.getPictureURL()) && recipe.getIngredientList() != null
                && !recipe.getIngredientList().isEmpty();
    }

    public List<Ingredient> getAllIngredients(){
        return repo.getAllIngredients();
    }

    public Ingredient createIngredient(Ingredient ingredient) throws SystemErrorException, InputErrorException {
        try {
            if(ingredient != null && isValidIngredient(ingredient)) {
                return repo.createIngredient(ingredient);
            } else throw new InputErrorException("Udfyld venligst alle felterne korrekt");
        }
        catch (DataAccessException e) {
            System.err.println("Error accessing the database: " + e.getMessage());
            throw new SystemErrorException("Der er sket en fejl på vores side. Prøv igen senere!");
        }
    }

    public Ingredient getIngredientByID(int id){
        return repo.getIngredientById(id);
    }

    public boolean isValidIngredient(Ingredient ingredient){
        return StringUtils.hasText(ingredient.getName()) && ingredient.getCaloriesPerHundredGrams() >= 0 && ingredient.getProteinPerHundredGrams() >= 0
                && ingredient.getFatPerHundredGrams() >= 0 && ingredient.getCarbohydratesPerHundredGrams() >= 0;
    }



/*
    public Recipe getRecipe(int recipeID) {

    }

    public Recipe updateRecipe(int recipeID, Recipe newRecipe) {

    }

    public Recipe createRecipe(Recipe recipe) {

    }

    public boolean deleteRecipe(int recipeID){

    }



    public List<Recipe> getAllBreakfastRecipe() {


    }

    public List<Recipe> getAllLunchRecipe(){

    }

    public List<Recipe> getAllDinnerRecipe(){

    }
*/
}

