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
        catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            throw new RuntimeException("Failed to create recipe due to an unexpected error.", e);
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

