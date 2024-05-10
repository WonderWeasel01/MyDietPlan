package com.WebApplcation.MyDietPlan.Service;

import com.WebApplcation.MyDietPlan.Model.Ingredient;
import com.WebApplcation.MyDietPlan.Model.Recipe;
import com.WebApplcation.MyDietPlan.Repository.RecipeRepository;
import com.WebApplcation.MyDietPlan.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private RecipeRepository rr = new RecipeRepository( new JdbcTemplate());
    @Autowired
    private UserRepository ur = new UserRepository(new JdbcTemplate());


    public Recipe createRecipe(Recipe recipe){
        return rr.createRecipe(recipe);
    }
/*
    public Recipe addRecipe(Recipe recipe){


    }

    public boolean deleteRecipe(int recipeID){

    }

    public boolean deleteUser(int userID){

    }
    */

}
