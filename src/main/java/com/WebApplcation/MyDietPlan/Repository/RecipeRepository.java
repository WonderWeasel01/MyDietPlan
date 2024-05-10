package com.WebApplcation.MyDietPlan.Repository;

import com.WebApplcation.MyDietPlan.Model.Ingredient;
import com.WebApplcation.MyDietPlan.Model.Recipe;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@Repository
public class RecipeRepository {

    private final JdbcTemplate jdbcTemplate;

    public RecipeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


   /* public Recipe updateRecipe(int recipe){




    }
*/

    /**
     * Extracts a recipe object from the database and returns it.
     * @param recipeID the id of the recipe that the user wants returned
     * @return The recipe object that matches the given recipeID
     */
    public Recipe getRecipeByID(int recipeID) {
        String sql = "SELECT * FROM `Recipe` WHERE `recipe_id` = ?";
        Recipe recipe = jdbcTemplate.queryForObject(sql,new Object[]{recipeID}, recipeRowMapper());

        if(recipe == null){
            return null;
        }

        String sqlIngredients = "SELECT * FROM `Ingredient` WHERE `ingredient_id` IN (SELECT `ingredient_id` FROM `Recipe_has_ingredient` WHERE `recipe_id` = ?)";
        List<Ingredient> ingredients = jdbcTemplate.query(sqlIngredients, new Object[]{recipe.getRecipeID()}, (resultSet, rowNum) -> {
            Ingredient ingredient = new Ingredient();
            ingredient.setIngredientID(resultSet.getInt("ingredient_id"));
            ingredient.setName(resultSet.getString("name"));
            ingredient.setWeightGrams(resultSet.getInt("weight"));
            ingredient.setProteinPerHundredGrams(resultSet.getInt("protein"));
            ingredient.setFatPerHundredGrams(resultSet.getInt("fat"));
            ingredient.setCarbohydratesPerHundredGrams(resultSet.getInt("carbohydrates"));
            ingredient.setCaloriesPerHundredGrams(resultSet.getInt("calories"));
            return ingredient;
        });
        recipe.setIngredientList((ArrayList<Ingredient>) ingredients);
        return recipe;

    }



    /**
     * Inserts a new recipe into the database
     * @param recipe The recipe that the user wants to create
     * @return The newly created recipe with the auto generated id attached
     */

    public Recipe createRecipe(Recipe recipe){
        String sql = "INSERT INTO `Recipe`(`time_of_day`, `total_calories`, `total_protein`, `total_fat`, `total_carbohydrates`, `active`) VALUES (?,?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        //insert the recipe into the database
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, recipe.getTimeOfDay());
            ps.setInt(2, recipe.getTotalCalories());
            ps.setInt(3, recipe.getTotalFat());
            ps.setInt(4, recipe.getTotalProtein());
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

    /**
     * @param recipeID the id of a recipe from the database
     * @return if return is 0 or above delete recipe from database
     */
    public boolean deleteRecipe(int recipeID){
        String sql = "SELECT * FROM `Recipe` WHERE recipe_id = ?";
        int rowsAffected = jdbcTemplate.update(sql,recipeID);

        if(rowsAffected < 0) {
            return true;
        }
        else{
           return false;
        }
    }

    /**
     * The method reads every recipe from the database and returns them as a list.
     * @return A list of recipes.
     */
    public List<Recipe> getAllRecipe(){

        String sql = "SELECT * FROM Recipe";
        return jdbcTemplate.query(sql, recipeRowMapper());

    }



    /**
     * Retrieves all breakfast recipes from the database.
     *
     * @return A list of all "Morgen" Recipes
     */
    public List<Recipe> getAllBreakfastRecipes(){

        String sql = "SELECT * FROM `Recipe` WHERE time_of_day = ?";
        return jdbcTemplate.query(sql,recipeRowMapper(),"Morgen");

    }

    /**
     * Retrieves a list of all lunch recipes available from the database.
     * @return A list of all "Middag" recipes.
     */

    public List<Recipe> getAllLunchRecipes(){
        String sql = "SELECT * FROM `Recipe` WHERE time_of_day = ?";
        return jdbcTemplate.query(sql,recipeRowMapper(),"Middag");

    }

    /**
     * Retrieves a list of all dinner recipes available from the database.
     * @return A list of all "Aften" recipes.
     */

    public List<Recipe> getAllDinnerRecipes(){
        String sql = "SELECT * FROM `Recipe` WHERE time_of_day = ?";
        return jdbcTemplate.query(sql,recipeRowMapper(),"Aften");

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
