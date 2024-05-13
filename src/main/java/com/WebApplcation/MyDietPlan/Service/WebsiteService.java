package com.WebApplcation.MyDietPlan.Service;

import com.WebApplcation.MyDietPlan.Exception.InputErrorException;
import com.WebApplcation.MyDietPlan.Exception.SystemErrorException;
import com.WebApplcation.MyDietPlan.Model.Ingredient;
import com.WebApplcation.MyDietPlan.Model.Recipe;
import com.WebApplcation.MyDietPlan.Repository.MyDietPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class WebsiteService {

    @Autowired
    private MyDietPlanRepository repo;

    public WebsiteService(MyDietPlanRepository myDietPlanRepository){
        this.repo = myDietPlanRepository;
    }


    public Recipe createRecipe(Recipe recipe) throws SystemErrorException, InputErrorException {
        try {
            if(recipe != null && isValidRecipe(recipe)) {
                recipe.calculateMacros();
                return repo.createRecipe(recipe);
            } else throw new InputErrorException("Failed to create recipe due to input issue");
        }
        catch (DataAccessException e) {
            System.err.println("Error accessing the database: " + e.getMessage());
            throw new SystemErrorException("Failed to create recipe due to database access issues.");
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
            } else throw new InputErrorException("Failed to create ingredient due to input issue");
        }
        catch (DataAccessException e) {
            System.err.println("Error accessing the database: " + e.getMessage());
            throw new SystemErrorException("Failed to create ingredient due to database access issues.");
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

