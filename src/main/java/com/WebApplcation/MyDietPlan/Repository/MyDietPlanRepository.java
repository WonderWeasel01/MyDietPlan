package com.WebApplcation.MyDietPlan.Repository;

import com.WebApplcation.MyDietPlan.Entity.Image;
import com.WebApplcation.MyDietPlan.Entity.Ingredient;
import com.WebApplcation.MyDietPlan.Entity.Recipe;
import com.WebApplcation.MyDietPlan.Entity.Subscription;
import com.WebApplcation.MyDietPlan.Entity.User;
import com.WebApplcation.MyDietPlan.Exception.SystemErrorException;
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
import java.util.Base64;
import java.util.List;
@Repository
public class MyDietPlanRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MyDietPlanRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


	public Ingredient getIngredientById(int id){
        String sql = "SELECT * FROM `Ingredient` WHERE ingredient_id = ?";
    
        return jdbcTemplate.queryForObject(sql,new Object[]{id}, ingredientRowMapper());
    }

    public boolean updateRecipeActiveStatus(int recipeID, int newActive){
        String sql = "UPDATE `Recipe` SET `active`= ? where recipe_id = ?";
        return 0<jdbcTemplate.update(sql,newActive,recipeID);
    }

    public Ingredient createIngredient(Ingredient ingredient){
        String sql = "INSERT INTO `Ingredient`(`name`, `protein`, `fat`, `carbohydrates`, `calories`) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, ingredient.getName());
            ps.setDouble(2, ingredient.getProteinPerHundredGrams());
            ps.setDouble(3, ingredient.getFatPerHundredGrams());
            ps.setDouble(4, ingredient.getCarbohydratesPerHundredGrams());
            ps.setDouble(5, ingredient.getCaloriesPerHundredGrams());
            return ps;
        }, keyHolder);

        int newId = keyHolder.getKey().intValue();

        ingredient.setIngredientID(newId);

        return ingredient;
    }

    public boolean updateRecipeWithoutIngredients(Recipe recipe) {
        String sql = "UPDATE Recipe SET " + "recipe_title = ?, " + "time_of_day = ?, " + "prep_time = ?, " +
                "total_calories = ?, " + "total_protein = ?, " + "total_fat = ?, " + "total_carbohydrates = ?, "
                + "instructions = ? "  + "WHERE recipe_id = ?";

        int rowsAffected = jdbcTemplate.update(sql, recipe.getTitle(), recipe.getTimeOfDay(), recipe.getPrepTime(),
                recipe.getTotalCalories(), recipe.getTotalProtein(), recipe.getTotalFat(), recipe.getTotalCarbohydrates(),
                recipe.getInstructions(), recipe.getRecipeID());

        return rowsAffected > 0;
    }

    public boolean insertIngredientsOntoRecipe(int recipeID, List<Ingredient> ingredients) {
        String sql = "INSERT INTO `Recipe_has_ingredient`(`recipe_id`, `ingredient_id`, `ingredient_weight`) VALUES (?,?,?)";

        int totalInserted = 0;

        for (Ingredient ingredient : ingredients) {
            int rowsAffected = jdbcTemplate.update(sql, recipeID, ingredient.getIngredientID(), ingredient.getWeightGrams());
            totalInserted += rowsAffected;
        }

        return totalInserted == ingredients.size();
    }

    public boolean deleteIngredientsFromRecipe(int recipeID){
        String sql = "DELETE FROM `Recipe_has_ingredient` WHERE recipe_id = ?";
        return 0 < jdbcTemplate.update(sql,recipeID);
    }

    public Recipe getRecipeWithoutImageAndIngredients(int recipeID){
        String sql = "SELECT * FROM Recipe WHERE recipe_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{recipeID}, recipeRowMapper());
    }

    public boolean updateImageByRecipeID(int recipeID, Image image){
        String sql = "UPDATE `Image`INNER JOIN `Recipe` ON Image.image_id = Recipe.image_id SET image_name = ?, image_type = ?, image_blob = ? WHERE Recipe.recipe_id = ?";
        return 0 < jdbcTemplate.update(sql, image.getImageName(), image.getImageType(), image.getBlob(), recipeID);
    }

    public Recipe getRecipeWithIngredientsByRecipeID(int recipeID){
        String sql = "SELECT * FROM Recipe WHERE recipe_id = ?";

		Recipe recipe = jdbcTemplate.queryForObject(sql, new Object[]{recipeID}, recipeRowMapper());
        if (recipe != null){
            //Recipe ingredientlist requires an Arraylist
            ArrayList<Ingredient> ingredients = (ArrayList<Ingredient>) getRecipeIngredientsByRecipeID(recipeID);
            //Set the ingredientlist
            recipe.setIngredientList(ingredients);

            //The recipe.imageID has been recieved in previous query. Retrieve the rest of the recipe's image with that imageID.
            Image image = getImageByImageID(recipe.getImage().getImageID());
            recipe.setImage(image);

        }
        return recipe;
    }
   

    /**
     * Retrieves the list of ingredients that match a recipeID
     * @param recipeID the id of the recipe that the user wants the list of ingredients from
     * @return A list of the ingredients that are attached to the recipe with the given recipeID
     */
    public List<Ingredient> getRecipeIngredientsByRecipeID(int recipeID) {
        String sql = "SELECT Ingredient.*, Recipe_has_ingredient.ingredient_weight " +
                "FROM Ingredient " +
                "JOIN Recipe_has_ingredient ON Ingredient.ingredient_id = Recipe_has_ingredient.ingredient_id " +
                "WHERE Recipe_has_ingredient.recipe_id = ?";

		List<Ingredient> ingredients = jdbcTemplate.query(sql, new Object[]{recipeID}, (resultSet, rowNum) -> {
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

        return ingredients;
    }



    /**
     * Inserts a new recipe into the database
     * @param recipe The recipe that the user wants to create
     * @return The newly created recipe with the auto generated id attached
     */

    public Recipe createRecipe(Recipe recipe){
        String sql = "INSERT INTO `Recipe`(`time_of_day`,`recipe_title`,`prep_time`, `total_calories`, `total_protein`, `total_fat`, `total_carbohydrates`, `active`,`instructions`,`image_id`) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        if(insertImage(recipe.getImage()) == null){
            return null;
        }
        //insert the recipe into the database
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, recipe.getTimeOfDay());
            ps.setString(2,recipe.getTitle());
            ps.setString(3,recipe.getPrepTime());
            ps.setDouble(4, recipe.getTotalCalories());
            ps.setDouble(5, recipe.getTotalProtein());
            ps.setDouble(6, recipe.getTotalFat());
            ps.setDouble(7,recipe.getTotalCarbohydrates());
            ps.setBoolean(8,recipe.getActive());
            ps.setString(9,recipe.getInstructions());
            ps.setInt(10,recipe.getImage().getImageID());

            return ps;
        }, keyHolder);

        // Retrieve the generated key
        Number generatedKey = keyHolder.getKey();
        if (generatedKey != null) {
            int recipeID = generatedKey.intValue();
            recipe.setRecipeID(recipeID);
        } else return null;

        // Insert the ingredientlist
        if(insertIngredientsOntoRecipe(recipe.getRecipeID(), recipe.getIngredientList())){
        } else return null;

        return recipe;
    }

    /**
     * @param recipeID the id of a recipe from the database
     * @return if return is 0 or above delete recipe from database
     */
    public boolean deleteRecipe(int recipeID){
        String sql = "DELETE FROM `Recipe` WHERE recipe_id = ?";
        int rowsAffected = jdbcTemplate.update(sql,recipeID);
        return rowsAffected > 0;
    }

    /**
     * The method reads every recipe from the database and returns them as a list.
     * @return A list of recipes.
     */
    public List<Recipe> getAllRecipeWithoutIngredients(){
        String sql = "SELECT * FROM Recipe";
        return jdbcTemplate.query(sql, recipeRowMapper());
    }
    public List<Recipe> getAllActiveRecipe(){
        String sql = "SELECT * FROM Recipe where active = 1";
        List<Recipe> recipes = jdbcTemplate.query(sql, recipeRowMapper());

        for (Recipe recipe : recipes) {
            int recipeID = recipe.getRecipeID();
            //Get and set ingredient list
            ArrayList<Ingredient> ingredients = (ArrayList<Ingredient>) getRecipeIngredientsByRecipeID(recipeID);
            recipe.setIngredientList(ingredients);
            //Get and set image
            recipe.setImage(getImageByImageID(recipe.getImage().getImageID()));
        }

        return recipes;
    }
    public Recipe getRecipeByID(Recipe recipe) {
        String sql = "SELECT * FROM Recipe WHERE recipe_id = ?";
        return jdbcTemplate.queryForObject(sql, recipeRowMapper(), recipe);
    }

    public List<Recipe> getAllActiveRecipeWithoutIngredientsAndImage(){
        String sql = "SELECT * FROM Recipe where active = 1";
        return jdbcTemplate.query(sql, recipeRowMapper());
    }


    public List<Recipe> getAllDeactivatedRecipeWithoutIngredients(){
        String sql = "SELECT * FROM Recipe WHERE active = 0";
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
        String sql = "SELECT * FROM `Ingredient` ORDER BY `Ingredient`.`name` ASC";
        return jdbcTemplate.query(sql,ingredientRowMapper());
    }


    /**
     * Inserts a new user into the database.
     * @param user a new user that doesn't exist in the database
     * @return The user that was insert into the database, with the auto-generated id attached.
     */
    public User createUser(User user){
        String sql = "INSERT INTO `User`(`email`, `password`,`first_name`, `last_name`, `gender`, `height`, `weight`, `age`, `activity_level`, `goal`, `role`, `daily_calorie_burn`, `daily_calorie_goal`)" +
                " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
            ps.setDouble(12,user.getDailyCalorieBurn());
            ps.setDouble(13,user.getDailyCalorieGoal());
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
        int rowsAffected = jdbcTemplate.update(sql, userID);

        //Returns true if deletion was successful
        return rowsAffected > 0;
    }

    /**
     * Finds a user by their userId.
     * @param id The userId you are searching for.
     * @return The user, which matches the userId you are searching for.
     * @throws EmptyResultDataAccessException Throws an exception whenever you are searching for a userId that doesn't exist.
     */

    public User getUserByID(int id) {
        String sql = "SELECT * FROM `User` where user_id = ?;";
        return jdbcTemplate.queryForObject(sql, userRowMapper(), id);
    }


    /**
     * Finds a user by their email.
     * @param email The email you are searching for.
     * @return The user, which matches the email you are searching for.
     * @throws EmptyResultDataAccessException Throws an exception whenever you are searching for an email that doesn't exist.
     */
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM `User` WHERE email = ?;";
        return jdbcTemplate.queryForObject(sql, userRowMapper(), email);
    }

    public String getPasswordByEmail(String email){
        String sql = "SELECT password FROM `User` WHERE email = ?";
        return jdbcTemplate.queryForObject(sql,new Object[]{email}, String.class);
    }

    public Subscription insertSubscription(Subscription subscription, int userID) {
        String sql = "INSERT INTO `Subscription` (`user_id`, `subscriptionStartDate`, `subscriptionEndDate`, `subscriptionStatus`, `subscriptionPrice`) VALUES (?,?,?,?,?);"; 
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userID);
            ps.setDate(2, subscription.getSubscriptionStartDate());
            ps.setDate(3, subscription.getSubscriptionEndDate());
            ps.setBoolean(4, subscription.getActiveSubscription());
            ps.setDouble(5, subscription.getSubscriptionPrice());

            return ps;
        }, keyHolder);

        // Retrieve the generated key
        Number generatedKey = keyHolder.getKey();
        if (generatedKey != null) {
            int id = generatedKey.intValue();
            subscription.setSubscriptionID(id);//
        }
        return subscription;
    }


    /**
     * Updates settings for a user in the database.
     * @param user An existing user in the database.
     * @return User that was just updated in the database.
     */

    public boolean updateUser(User user){
        String sql = "UPDATE `User` SET `first_name`= ?, `last_name`= ?, `email`= ?, `password`= ?, " +
                "`goal`= ?, `activity_level`= ?, `gender`= ?, `weight`= ?, `height`= ?, `age`= ?, " +
                "`daily_calorie_burn`= ?, `daily_calorie_goal`= ? WHERE user_id = ?";

        return 0 < jdbcTemplate.update(sql,
                user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(),
                user.getGoal(), user.getActivityLevel(), String.valueOf(user.getGender()),
                user.getWeight(), user.getHeight(), user.getAge(),
                user.getDailyCalorieBurn(), user.getDailyCalorieGoal(), user.getUserId());
    }

    public int getActiveRecipeAmount(){
        String sql ="SELECT COUNT(*) FROM Recipe WHERE Active = 1;";
        return jdbcTemplate.queryForObject(sql, Integer.class);

    }


    public Subscription getSubscriptionByUserID(int userId){
        String sql = "SELECT * FROM Subscription WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{userId}, subscriptionRowMapper());
    }

    public boolean updateSubscription(Subscription subscription){
        String sql = "UPDATE Subscription SET `subscriptionStartDate`= ? ,`subscriptionEndDate`= ?,`subscriptionStatus`= ?,`subscriptionPrice`= ? WHERE user_id = ?";
        return 0 < jdbcTemplate.update(sql,subscription.getSubscriptionStartDate(),subscription.getSubscriptionEndDate(),subscription.getActiveSubscription(), subscription.getSubscriptionPrice(), subscription.getUserID());
    }

    public boolean isActiveMember(int userID){
        String sql = "SELECT subscriptionStatus FROM Subscription WHERE user_id = ?";
        Boolean status = jdbcTemplate.queryForObject(sql, new Object[]{userID}, Boolean.class);
        if (status == null){
            return false;
        }

        return status;
    }

    public boolean cancelSubscription(int userID){
        String sql = "UPDATE Subscription SET subscriptionStatus = ? Where user_id = ?";
        return 0 < jdbcTemplate.update(sql,0,userID);
    }

    public boolean activateSubscription(int userID){
        String sql = "UPDATE Subscription SET subscriptionStatus = ? Where user_id = ?";
        return 0 < jdbcTemplate.update(sql,1,userID);
    }






    public Image insertImage(Image image) {
        String sql = "INSERT INTO Image(image_name, image_type, image_blob) VALUES (?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, image.getImageName());
            ps.setString(2, image.getImageType());
            ps.setBytes(3, image.getBlob());

            return ps;
        }, keyHolder);

        // Retrieve the generated key
        Number generatedKey = keyHolder.getKey();
        if (generatedKey != null) {
            int id = generatedKey.intValue();
            image.setImageID(id);;//
        }
        return image;
    }

    public boolean updateImage(Image image) {
        String sql = "UPDATE Image SET image_name = ?, image_type = ?, image_blob = ? WHERE image_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, image.getImageName(), image.getImageType(), image.getBlob(), image.getImageID());
        return rowsAffected > 0;
    }


    public Image getImageByImageID(int imageID) {
        String sql = "SELECT Image.image_id, Image.image_name, Image.image_type, Image.image_blob FROM Image " +
                "INNER JOIN Recipe ON Image.image_id = Recipe.image_id WHERE Image.image_id = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{imageID}, imageRowMapper());

    }

    public List<User> getAllUsers(String searchQuery) {
            String sql = "SELECT * FROM User";

            if (searchQuery != null && !searchQuery.isEmpty()) {
                sql += "WHERE first_name LIKE ?";
                return jdbcTemplate.query(sql, userRowMapper(), "%" + searchQuery + "%");
            } else {
                return jdbcTemplate.query(sql, userRowMapper());
            }
    }
    public void addFavoriteRecipe (int userID, int recipeID) {
        String sql = "INSERT INTO User_favorite_recipe (user_id, recipe_id) values (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userID);
            ps.setInt(2, recipeID);

            return ps;
        }, keyHolder);
    }

    public List<Recipe> getFavoriteRecipesByUserID(int userID) {
        // Define the SQL query string, the r is just shortened from recipe and ufr is shortened user_favorite_recipe
        String sql = "SELECT * FROM Recipe r" +
                "INNER JOIN user_favorite_recipe ufr ON r.recipe_id = ufr.recipe_id" +
                "WHERE ufr.user_id = ?";
        return jdbcTemplate.query(sql,recipeRowMapper(), userID);
    }


     private RowMapper<Image> imageRowMapper(){
        return ((rs, rowNum) -> {
            Image image = new Image();
            image.setImageID(rs.getInt("image_id"));
            image.setImageName(rs.getString("image_name"));
            image.setImageType(rs.getString("image_type"));
            image.setBlob(rs.getBytes("image_blob"));
            return image;
        });
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
            user.setDailyCalorieBurn(rs.getDouble("daily_calorie_burn"));
            user.setDailyCalorieGoal(rs.getDouble("daily_calorie_goal"));
            return user;
        });
    }


    private RowMapper<Recipe> recipeRowMapper(){
        return ((rs, rowNum) -> {
            Recipe recipe = new Recipe();
            Image image = new Image();
            recipe.setImage(image);
            recipe.setRecipeID(rs.getInt("recipe_id"));
            recipe.setTitle(rs.getString("recipe_title"));
            recipe.setTimeOfDay(rs.getString("time_of_day"));
            recipe.setPrepTime(rs.getString("prep_time"));
            recipe.setTotalCalories(rs.getDouble("total_calories"));
            recipe.setTotalProtein(rs.getDouble("total_protein"));
            recipe.setTotalFat(rs.getDouble("total_fat"));
            recipe.setTotalCarbohydrates(rs.getDouble("total_carbohydrates"));
            recipe.setActive(rs.getBoolean("active"));
            recipe.setInstructions(rs.getString("instructions"));
            recipe.getImage().setImageID((rs.getInt("image_id")));
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

   private RowMapper<Subscription> subscriptionRowMapper(){
        return ((rs, rowNum) -> {
            Subscription subscription = new Subscription();
            subscription.setSubscriptionID(rs.getInt("subscriptionID"));
            subscription.setUserID(rs.getInt("user_id"));
            subscription.setSubscriptionStartDate(rs.getDate("subscriptionStartDate"));
            subscription.setSubscriptionEndDate(rs.getDate("subscriptionEndDate"));
            subscription.setActiveSubscription(rs.getBoolean("subscriptionStatus"));
            subscription.setSubscriptionPrice(rs.getDouble("subscriptionPrice"));
            return subscription;
        });
    }



}
