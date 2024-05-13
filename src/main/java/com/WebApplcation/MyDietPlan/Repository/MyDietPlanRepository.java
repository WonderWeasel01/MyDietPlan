package com.WebApplcation.MyDietPlan.Repository;

import com.WebApplcation.MyDietPlan.Model.Ingredient;
import com.WebApplcation.MyDietPlan.Model.Recipe;
import com.WebApplcation.MyDietPlan.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
@Repository
public class MyDietPlanRepository {

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public MyDietPlanRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


   /* public Recipe updateRecipe(int recipe){




    }
*/

    public Ingredient createIngredient(Ingredient ingredient){
        String sql = "INSERT INTO `Ingredient`(`name`, `protein`, `fat`, `carbohydrates`, `calories`) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, ingredient.getName());
            ps.setDouble(2, ingredient.getProteinPerHundredGrams());
            ps.setDouble(3, ingredient.getFatPerHundredGrams());
            ps.setDouble(4, ingredient.getCarbohydratesPerHundredGrams());
            ps.setInt(5, ingredient.getCaloriesPerHundredGrams());
            return ps;
        }, keyHolder);

        int newId = keyHolder.getKey().intValue();

        ingredient.setIngredientID(newId);

        return ingredient;
    }

    /**
     * Extracts a recipe object from the database and returns it.
     * @param recipeID the id of the recipe that the user wants returned
     * @return The recipe object that matches the given recipeID
     */
    public Recipe getRecipeByID(int recipeID) {
        String sql = "SELECT Ingredient.*, Recipe_has_ingredient.ingredient_weight " +
                "FROM Ingredient " +
                "JOIN Recipe_has_ingredient ON Ingredient.ingredient_id = Recipe_has_ingredient.ingredient_id " +
                "WHERE Recipe_has_ingredient.recipe_id = ?";
        Recipe recipe = jdbcTemplate.queryForObject(sql, new Object[]{recipeID}, recipeRowMapper());

        if (recipe == null) {
            return null;
        }

        List<Ingredient> ingredients = jdbcTemplate.query(sql, new Object[]{recipe.getRecipeID()}, (resultSet, rowNum) -> {
            Ingredient ingredient = new Ingredient();
            ingredient.setIngredientID(resultSet.getInt("ingredient_id"));
            ingredient.setName(resultSet.getString("name"));
            ingredient.setProteinPerHundredGrams(resultSet.getInt("protein"));
            ingredient.setFatPerHundredGrams(resultSet.getInt("fat"));
            ingredient.setCarbohydratesPerHundredGrams(resultSet.getInt("carbohydrates"));
            ingredient.setCaloriesPerHundredGrams(resultSet.getInt("calories"));
            ingredient.setWeightGrams(resultSet.getInt("ingredient_weight")); // Gets the weight from the joined table
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
        String sql = "INSERT INTO `Recipe`(`time_of_day`,`recipe_title`,`prep_time`, `total_calories`, `total_protein`, `total_fat`, `total_carbohydrates`, `active`) " +
                "VALUES (?,?,?,?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        //insert the recipe into the database
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, recipe.getTimeOfDay());
            ps.setString(2,recipe.getTitle());
            ps.setString(3,recipe.getPrepTime());
            ps.setInt(4, recipe.getTotalCalories());
            ps.setInt(5, recipe.getTotalFat());
            ps.setInt(6, recipe.getTotalProtein());
            ps.setInt(7,recipe.getTotalCarbohydrates());
            ps.setBoolean(8,recipe.getActive());

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

        return rowsAffected > 0;
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

    public List<Ingredient> getAllIngredients(){
        String sql = "SELECT * FROM `Ingredient`";
        return jdbcTemplate.query(sql,ingredientRowMapper());
    }


    /**
     * Inserts a new user into the database.
     * @param user a new user that doesn't exist in the database
     * @return The user that was insert into the database, with the auto-generated id attached.
     */
    public User createUser(User user){
        String sql = "INSERT INTO `User`(`email`, `password`,`first_name`, `last_name`, `gender`, `height`, `weight`, `age`, `activity_level`, `goal`, `role`)" +
                " VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFirstName());
            ps.setString(4, user.getLastName());
            ps.setString(5, String.valueOf(user.getGender()));
            ps.setInt(6, user.getHeight());
            ps.setInt(7, user.getWeight());
            ps.setInt(8, user.getAge());
            ps.setString(9, user.getActivityLevel());
            ps.setString(10, user.getGoal());
            ps.setString(11,user.getRole());
            return ps;
        }, keyHolder);

        // Retrieve the generated key
        Number generatedKey = keyHolder.getKey();
        if (generatedKey != null) {
            int userId = generatedKey.intValue();
            user.setUserId(userId);//
        }
        return user;
    }

    /**
     * Deletes a user from the database
     * @param userID The id of an existing user
     * @return True if deletion was successful. False if something went wrong
     */

    public boolean deleteUser(int userID){
        String sql = "DELETE FROM `User` WHERE user_id = ?";
        int rowsAffected = jdbcTemplate.update(sql);

        //Returns true if deletion was successful
        return rowsAffected > 0;
    }

    /**
     * Finds a user by their userId.
     * @param id The userId you are searching for.
     * @return The user, which matches the userId you are searching for.
     * @throws EmptyResultDataAccessException Throws an exception whenever you are searching for a userId that doesn't exist.
     */

    public User getUserByID(int id) throws EmptyResultDataAccessException{
        String sql ="SELECT * FROM `User` WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql,userRowMapper(),id);
    }


    /**
     * Finds a user by their email.
     * @param email The email you are searching for.
     * @return The user, which matches the email you are searching for.
     * @throws EmptyResultDataAccessException Throws an exception whenever you are searching for an email that doesn't exist.
     */
    public User getUserByEmail(String email) {
        String sql ="SELECT * FROM `User` WHERE email = ?";
        return jdbcTemplate.queryForObject(sql,userRowMapper(),email);

    }

    public String getPasswordByEmail(String email){
        String sql = "SELECT password FROM `User` WHERE email = ?";
        return jdbcTemplate.queryForObject(sql,new Object[]{email},  String.class);
    }

    /**
     * Updates settings for a user in the database.
     * @param userId The id of an existing user.
     * @param user An existing user in the database.
     * @return User that was just updated in the database.
     */

    public User updateUser(int userId, User user){
        String sql ="UPDATE `User` SET `user_id`=?, `first_name`=?, `email`=?, `goal`=?, `last_name`=?, `activity_level`=?, `gender`=?, `weight`=?, `height`=?,  `age`=?, `role`=? WHERE id = ?";
        jdbcTemplate.update(sql, user.getUserId(), user.getFirstName(), user.getEmail(), user.getGoal(), user.getLastName(), user.getActivityLevel(), user.getGender(), user.getWeight(), user.getHeight(), user.getAge(), user.getRole(), userId);
        return getUserByID(userId);
    }


    /**
     * @return A RowMapper for mapping ResultSet rows in DataBase
     */
    private RowMapper<User> userRowMapper(){
        return ((rs, rowNum) -> {
            User user = new User();
            user.setUserId(rs.getInt("user_id"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setGender(rs.getString("gender").charAt(0));
            user.setHeight(rs.getInt("height"));
            user.setWeight(rs.getInt("weight"));
            user.setAge(rs.getInt("age"));
            user.setActivityLevel(rs.getString("activity_level"));
            user.setGoal(rs.getString("goal"));
            user.setRole(rs.getString("role"));
            return user;
        });
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
    private RowMapper<Ingredient> ingredientRowMapper(){
        return ((rs, rowNum) -> {
            Ingredient ingredient = new Ingredient();
            ingredient.setIngredientID(rs.getInt("ingredient_id"));
            ingredient.setName(rs.getString("name"));
            ingredient.setProteinPerHundredGrams(rs.getInt("protein"));
            ingredient.setFatPerHundredGrams(rs.getInt("fat"));
            ingredient.setCarbohydratesPerHundredGrams(rs.getInt("carbohydrates"));
            ingredient.setCaloriesPerHundredGrams(rs.getInt("calories"));
            return ingredient;
        });
    }


}
