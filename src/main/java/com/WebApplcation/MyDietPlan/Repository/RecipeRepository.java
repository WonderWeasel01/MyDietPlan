package com.WebApplcation.MyDietPlan.Repository;

import com.WebApplcation.MyDietPlan.Model.Ingredient;
import com.WebApplcation.MyDietPlan.Model.Recipe;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipeRepository {

    private final JdbcTemplate jdbcTemplate;

    public RecipeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


   /* public Recipe updateRecipe(int recipe){




    }

    public Recipe createRecipe(Recipe recipe){
        String sql = "INSERT INTO `Recipe`(`time_of_day`, `total_calories`, `total_protein`, `total_fat`, `total_carbohydrates`, `active`) VALUES (?,?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        //insert the recipe into the database
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, recipe.getTimeOfDay());
            ps.setInt(2, recipe.getTotalCalories());
            ps.setInt(3, recipe.getTotalFat());
            ps.setInt(4, recipe.getTotalFat());
            ps.setInt(5,recipe.getTotalCarbohydrates());
            ps.setBoolean(6,recipe.getActive());

            return ps;
        }, keyHolder);

        // Retrieve the generated key
        Number generatedKey = keyHolder.getKey();
        if (generatedKey != null) {
            int recipeID = generatedKey.intValue();
            recipe.setRecipeID(recipeID);
        }

        //Insert the ingredients into the database as well
        String sql1 = "INSERT INTO `Recipe_has_ingredient`(`recipe_id`, `ingredient_id`) VALUES (?,?)";
        for (Ingredient ingredient : recipe.getIngredientList()) {
            jdbcTemplate.update(sql1, recipe.getRecipeID(), ingredient.getIngredientID());
        }
        return recipe;
    }

    public boolean deleteRecipe(int recipeID){

    }

    /**
     * The method reads every recipe from the database and returns them as a list.
     * @return A list of recipes.
     */
    public List<Recipe> getAllRecipe(){

        String sql = "SELECT * FROM Recipe";
        return jdbcTemplate.query(sql, recipeRowMapper());

    }

    }

    public List<Recipe> getAllBreakfastRecipes(){

        String sql = "SELECT * FROM `Recipe` WHERE time_of_day = ?";
        return jdbcTemplate.query(sql,recipeRowMapper(),"Morgen");

    }

    public List<Recipe> getAllLunchRecipes(){

    }

    public List<Recipe> getAllDinnerRecipes(){

    }

    /**
     * @return A RowMapper for mapping ResultSet rows in DataBase
     */
    private RowMapper<Recipe> recipeRowMapper(){
        return ((rs, rowNum) -> {
            Recipe recipe = new Recipe();
            recipe.setRecipeID(rs.getInt("recipe_id"));
            recipe.setTimeOfDay(rs.getString("time_of_day"));
            recipe.setTotalCalories(rs.getInt("total_calories"));
            recipe.setTotalCarbohydrates(rs.getInt("total_carbohydrates"));
            recipe.setTotalFat(rs.getInt("total_fat"));
            recipe.setTotalProtein(rs.getInt("total_protein"));
            recipe.setActive(rs.getBoolean("active"));

            return recipe;
        });
    }


}
