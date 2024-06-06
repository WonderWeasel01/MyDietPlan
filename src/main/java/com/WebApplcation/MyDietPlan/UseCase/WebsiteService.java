package com.WebApplcation.MyDietPlan.UseCase;

import com.WebApplcation.MyDietPlan.Entity.*;
import com.WebApplcation.MyDietPlan.Exception.EntityNotFoundException;
import com.WebApplcation.MyDietPlan.Exception.InputErrorException;
import com.WebApplcation.MyDietPlan.Exception.SystemErrorException;
import com.WebApplcation.MyDietPlan.Repository.MyDietPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class WebsiteService {

    private final MyDietPlanRepository repository;
    private final AuthenticationService authenticationService;

    @Autowired
    public WebsiteService(AuthenticationService authenticationService, MyDietPlanRepository myDietPlanRepository) {
        this.authenticationService = authenticationService;
        this.repository = myDietPlanRepository;
    }

    /**
     * Validates a recipe and tries to insert the Recipe into the Database if it is a valid Recipe
     *
     * @param recipe The recipe being insert into the Database
     * @return The Recipe with newly attached recipeID.
     * @throws SystemErrorException If an error happens while inserting into database.
     * @throws InputErrorException  Rethrows InputErrorsExceptions from validateRecipe.
     */
    public Recipe createRecipe(Recipe recipe) throws SystemErrorException, InputErrorException {
        validateRecipe(recipe);
        try {
            return repository.createRecipe(recipe);
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new SystemErrorException("Der er sket en fejl på vores side. Prøv igen senere.");
        }
    }

    /**
     * Converts a MultipartFile to an Image entity object
     *
     * @param file A MultiPartFile with at least filename, type and bytes attached.
     * @return An image created from the MultipartFile
     * @throws IOException if an error happens while reading the file.
     */
    private Image convertFileToImage(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setImageName(file.getOriginalFilename());
        image.setImageType(file.getContentType());
        image.setBlob(file.getBytes());
        return image;
    }

    /**
     * Converts a file to an image and attaches it on the Recipe attribute 'Image'
     *
     * @param recipe    The recipe that is being setup with the image.
     * @param imageFile A MultiPartFile with at least filename, type and bytes attached.
     * @throws SystemErrorException If an IOException is caught while converting the file to an Image.
     */
    public void setupRecipeWithImage(Recipe recipe, MultipartFile imageFile) throws SystemErrorException {
        try {
            Image image = convertFileToImage(imageFile);
            recipe.setImage(image);
        } catch (IOException e) {
            e.printStackTrace();
            throw new SystemErrorException("Der skete en fejl under forsøget på at gemme billedet");
        }
    }


    /**
     * Validates a recipe and updates it.
     *
     * @param recipe the updated recipe.
     * @return true if update went well
     * @throws InputErrorException  Rethrows if validateRecipe(recipe) fails.
     * @throws SystemErrorException
     */
    public boolean updateRecipe(Recipe recipe) throws InputErrorException, SystemErrorException {
        try {
            if (repository.updateRecipeWithoutIngredients(recipe)) {
                return updateRecipeIngredients(recipe.getRecipeID(), recipe.getIngredientList());
            }
            throw new SystemErrorException("Der er sket en fejl på vores side. Prøv igen senere.");
        } catch (DataAccessException e) {
            System.err.println("Error accessing the database: " + e.getMessage());
            throw new SystemErrorException("Der er sket en fejl på vores side. Prøv igen senere.");
        }
    }

    /**
     * Deletes a recipe.
     *
     * @param recipeID ID of the recipe that is being deleted
     * @return true if the deletion went well
     */
    public boolean deleteRecipe(int recipeID) throws SystemErrorException {
        try {
            return repository.deleteRecipe(recipeID);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new SystemErrorException("Der er sket en fejl. Prøv igen senere");
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new SystemErrorException("Der er sket en fejl. Prøv igen senere");
        }
    }

    /**
     * Checks a recipes status and sets it to the opposite.
     *
     * @param recipeID ID of the recipe that the admin wants to update the status of.
     * @return True if status change went well.
     * @throws SystemErrorException    If 21 active recipe already exist
     * @throws EntityNotFoundException If the recipe that is being changed couldn't be found
     */
    public boolean updateRecipeActiveStatus(int recipeID) throws SystemErrorException, EntityNotFoundException {
        try {
            //Set the recipe active status to false if it is active
            if (getRecipeById(recipeID).getActive()) {
                return repository.updateRecipeActiveStatus(recipeID, 0);
            }
            //Set the recipe active status to true if there is less than 21 already active in the database.
            if (getActiveRecipeAmount() < 21) {
                return repository.updateRecipeActiveStatus(recipeID, 1);
            } throw new SystemErrorException("Der er allerede 21 aktive opskrifter. Deaktiver en eller flere aktive opskrifter for at tilføje flere");

        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Kunne ikke finde opskriften i databasen. Prøv igen senere");
        }
    }

    /**
     * Fetches all recipes from the Database where active = true.
     *
     * @return an Integer with the value of counted recipe from.
     * @throws SystemErrorException If there's an error while accessing the database.
     */
    public int getActiveRecipeAmount() throws SystemErrorException {
        try {
            return repository.getActiveRecipeAmount();
        } catch (NullPointerException | EmptyResultDataAccessException e) {
            return 0;
        } catch (DataAccessException e) {
            throw new SystemErrorException("Error getting active recipe amount. Trouble accessing database");
        }
    }

    /**
     * Deletes an ingredientlist from a recipe in the database and inserts a new one.
     *
     * @param recipeID       ID of the recipe being updated
     * @param newIngredients The new ingredients being attached to the recipe.
     * @return True if update was successful, False if not.
     * @throws SystemErrorException If there was an error while deleting the original ingredientlist
     */
    public boolean updateRecipeIngredients(int recipeID, List<Ingredient> newIngredients) throws SystemErrorException {
        if (repository.deleteIngredientsFromRecipe(recipeID)) {
            return repository.insertIngredientsOntoRecipe(recipeID, newIngredients);
        } else throw new SystemErrorException("Der er sket en fejl på vores side. Prøv igen senere.");
    }


    /**
     * Sets up the recipe with ingredients.
     *
     * @param recipe        The recipe being set up.
     * @param ingredientIds List of ingredient IDs.
     * @param weights       List of ingredient weights.
     * @throws InputErrorException     If ingredient and weight sizes do not match.
     * @throws EntityNotFoundException If an ingredient is not found.
     */
    public void setupRecipeWithIngredients(Recipe recipe, List<Integer> ingredientIds, List<Integer> weights) throws InputErrorException, EntityNotFoundException {
        validateIngredientAndWeightSizes(ingredientIds, weights);
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientIds.size(); i++) {
            Ingredient ingredient = getIngredientByID(ingredientIds.get(i));
            ingredient.setWeightGrams(weights.get(i));
            ingredients.add(ingredient);
        }
        recipe.setIngredientList(ingredients);
        calculateAndSetMacros(recipe);
    }

    /**
     * Rounds a value to two decimal places.
     *
     * @param value The value to be rounded.
     * @return The rounded value.
     */
    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    /**
     * Calculates and sets macros for a recipe.
     *
     * @param recipe The recipe being set up.
     */
    public void calculateAndSetMacros(Recipe recipe) {
        recipe.setTotalCalories(0);
        recipe.setTotalProtein(0);
        recipe.setTotalFat(0);
        recipe.setTotalCarbohydrates(0);

        for (int i = 0; i < recipe.getIngredientList().size(); i++) {
            Ingredient ingredient = recipe.getIngredientList().get(i);

            recipe.setTotalProtein(recipe.getTotalProtein()
                    + (ingredient.getProteinPerHundredGrams()
                    * ingredient.getWeightGrams() / 100.0));

            recipe.setTotalFat(recipe.getTotalFat()
                    + (ingredient.getFatPerHundredGrams()
                    * ingredient.getWeightGrams() / 100.0));

            recipe.setTotalCarbohydrates(recipe.getTotalCarbohydrates()
                    + (ingredient.getCarbohydratesPerHundredGrams()
                    * ingredient.getWeightGrams() / 100.0));

            recipe.setTotalCalories(recipe.getTotalCalories()
                    + (ingredient.getCaloriesPerHundredGrams()
                    * ingredient.getWeightGrams() / 100.0));
        }
        recipe.setTotalProtein(roundToTwoDecimals(recipe.getTotalProtein()));
        recipe.setTotalFat(roundToTwoDecimals(recipe.getTotalFat()));
        recipe.setTotalCarbohydrates(roundToTwoDecimals(recipe.getTotalCarbohydrates()));
        recipe.setTotalCalories(roundToTwoDecimals(recipe.getTotalCalories()));
    }

    /**
     * Adjusts the given recipe to match the user's daily calorie goal.
     *
     * @param recipe           The recipe to be adjusted.
     * @param dailyCalorieGoal The user's daily calorie goal.
     * @return The adjusted recipe.
     */
    public Recipe adjustRecipeToUser(Recipe recipe, double dailyCalorieGoal) {
        double recipeCalorieGoal = 0;
        switch (recipe.getTimeOfDay()) {
            case ("Breakfast"):
                recipeCalorieGoal = dailyCalorieGoal * 0.4;
                break;
            case ("Lunch"), ("Dinner"):
                recipeCalorieGoal = dailyCalorieGoal * 0.3;
                break;
        }

        double multiplier = recipeCalorieGoal / recipe.getTotalCalories();

        for (int i = 0; i < recipe.getIngredientList().size(); i++) {
            recipe.getIngredientList().get(i).setWeightGrams((Math.round(recipe.getIngredientList().get(i).getWeightGrams() * multiplier)));
        }
        calculateAndSetMacros(recipe);
        return recipe;
    }

    /**
     * Validates the given recipe.
     *
     * @param recipe The recipe to be validated.
     * @throws InputErrorException if the recipe is invalid.
     */
    private void validateRecipe(Recipe recipe) throws InputErrorException {
        if (recipe == null || !isValidRecipe(recipe)) {
            throw new InputErrorException("Udfyld venligst felterne korrekt");
        }
    }

    /**
     * Checks if a recipe  by ensuring that all required fields are filled.
     * Required fields include:
     * - Title
     * - Prep time
     * - Time of day
     * - Instructions
     * - Image name and type
     * - Non-empty ingredient list
     *
     * @param recipe The recipe to validate.
     * @return true if the recipe is valid, false otherwise.
     */
    public boolean isValidRecipe(Recipe recipe) {
        return StringUtils.hasText(recipe.getTitle()) && StringUtils.hasText(recipe.getPrepTime()) && StringUtils.hasText(recipe.getTimeOfDay())
                && StringUtils.hasText(recipe.getInstructions()) && StringUtils.hasText(recipe.getImage().getImageName()) && StringUtils.hasText(recipe.getImage().getImageType()) && recipe.getIngredientList() != null
                && !recipe.getIngredientList().isEmpty();
    }

    /**
     * Checks if an ingredient list and weight list match in size.
     *
     * @param ingredientIds List of ingredient IDs
     * @param weights       List of weights.
     * @throws InputErrorException If sizes do not match.
     */
    private void validateIngredientAndWeightSizes(List<Integer> ingredientIds, List<Integer> weights) throws InputErrorException {
        if (ingredientIds.size() != weights.size()) {
            throw new InputErrorException("The number of ingredients and weights do not match.");
        }
    }

    /**
     * Fetches all ingredients from the database.
     *
     * @return A list of all ingredients.
     */
    public List<Ingredient> getAllIngredients() {
        return repository.getAllIngredients();
    }

    /**
     * Fetches a recipe from an ID
     *
     * @param recipeID The recipe being fetched.
     * @return The recipe being fetched from the database.
     * @throws EntityNotFoundException If the recipe was not found.
     */
    public Recipe getRecipeById(int recipeID) throws EntityNotFoundException, SystemErrorException {
        try {
            return repository.getRecipeWithIngredientsByRecipeID(recipeID);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new EntityNotFoundException("Kunne ikke finde en opskrift med givet id");
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new SystemErrorException("Der er sket en fejl. Prøv igen senere");
        }
    }

    /**
     * Fetches a recipe from an ID
     *
     * @param recipeID The recipe being fetched.
     * @return The recipe being fetched from the database.
     * @throws EntityNotFoundException If the recipe was not found.
     */
    public Recipe getUsersAdjustedRecipeById(int recipeID) throws SystemErrorException {
        ArrayList<Recipe> recipes = authenticationService.getUser().getAdjustedRecipes();
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).getRecipeID() == recipeID) {
                return recipes.get(i);
            }
        }
        throw new SystemErrorException("Der er sket en fejl. Prøv igen senere");
    }

    /**
     * Creates an ingredient in the database.
     *
     * @param ingredient The ingredient to be created.
     * @return The created ingredient.
     * @throws SystemErrorException If an error occurs while creating the ingredient.
     * @throws InputErrorException  If the ingredient is invalid.
     */
    public Ingredient createIngredient(Ingredient ingredient) throws SystemErrorException, InputErrorException {
        if (!isValidIngredient(ingredient)) {
            throw new InputErrorException("Udfyld venligst alle felterne korrekt");
        }

        try {
            return repository.createIngredient(ingredient);
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new SystemErrorException("Der er sket en fejl på vores side. Prøv igen senere!");
        }
    }

    /**
     * Fetches an ingredient from an ID
     *
     * @param id The ingredient being fetched.
     * @return The ingredient being fetched from the database.
     * @throws EntityNotFoundException If the ingredient was not found.
     */
    public Ingredient getIngredientByID(int id) throws EntityNotFoundException {
        try {
            return repository.getIngredientById(id);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new EntityNotFoundException("Kan ikke finde ingrediens");
        }

    }

    /**
     * Checks if an ingredient is valid by ensuring that all required fields are filled.
     * Required fields include:
     * - Name
     * - Calories per 100 grams
     * - Protein per 100 grams
     * - Fat per 100 grams
     * - Carbohydrates per 100 grams
     *
     * @param ingredient The ingredient to validate.
     * @return true if the ingredient is valid, false otherwise.
     */
    public boolean isValidIngredient(Ingredient ingredient) {
        return (ingredient != null)
                && StringUtils.hasText(ingredient.getName())
                && ingredient.getCaloriesPerHundredGrams() >= 0
                && ingredient.getProteinPerHundredGrams() >= 0
                && ingredient.getFatPerHundredGrams() >= 0 && ingredient.getCarbohydratesPerHundredGrams() >= 0;
    }

    /**
     * Fetches all recipes from the database.
     *
     * @return A list of all recipes.
     * @throws EntityNotFoundException If no recipes were found.
     */
    public ArrayList<Recipe> getAllRecipes() throws EntityNotFoundException {
        try {
            return new ArrayList<>(repository.getAllRecipeWithoutIngredients());
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new EntityNotFoundException("Du har ikke tilføjet nogle opskrifter endnu!");
        }
    }

    /**
     * Fetches all deactivated recipes from the database.
     *
     * @return A list of all deactivated recipes.
     * @throws EntityNotFoundException If no deactivated recipes were found.
     */
    public ArrayList<Recipe> getAllDeactivatedRecipes() throws EntityNotFoundException {
        try {
            return new ArrayList<>(repository.getAllDeactivatedRecipeWithoutIngredients());
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new EntityNotFoundException("Du har ikke tilføjet nogle opskrifter endnu!");
        }
    }

    /**
     * Fetches all recipes from the database with ingredients.
     *
     * @return A list of all recipes with ingredients.
     * @throws EntityNotFoundException If no recipes were found.
     */
    public void setBase64Image(Recipe recipe) {
        String base64Image = Base64.getEncoder().encodeToString(recipe.getImage().getBlob());
        recipe.getImage().setBase64Image(base64Image);
    }

    /**
     * Sets Base64-encoded images for a list of recipes.
     *
     * @param recipes The list of recipes to set Base64-encoded images for.
     */
    public void setBase64Image(ArrayList<Recipe> recipes) {
        for (int i = 0; i < recipes.size(); i++) {
            setBase64Image(recipes.get(i));
        }
    }

    /**
     * Fetches all active recipes from the database.
     *
     * @return A list of all active recipes.
     * @throws EntityNotFoundException If no active recipes were found.
     */
    public ArrayList<Recipe> getAllActiveRecipes() throws EntityNotFoundException {
        try {
            return (ArrayList<Recipe>) repository.getAllActiveRecipe();
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new EntityNotFoundException("Der er ikke tilføjet nogle opskrifter endnu!");
        }
    }

    public ArrayList<Recipe> getAllActiveRecipesWithoutIngredientsAndImage() throws EntityNotFoundException {
        try {
            return (ArrayList<Recipe>) repository.getAllActiveRecipeWithoutIngredientsAndImage();
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new EntityNotFoundException("Du har ikke tilføjet nogle opskrifter endnu!");
        }
    }

    /**
     * Fetches all recipes from the database with ingredients.
     *
     * @return A list of all recipes with ingredients.
     * @throws EntityNotFoundException If no recipes were found.
     */
    public ArrayList<Recipe> adjustAndSetWeeklyRecipesToLoggedInUser() throws EntityNotFoundException {
        User loggedInUser = authenticationService.getUser();
        //Fetch the weekly recipes
        ArrayList<Recipe> weeklyRecipes = getAllActiveRecipes();

        //Adjust them to the logged-in user
        ArrayList<Recipe> adjustedRecipes = adjustRecipesToUser(weeklyRecipes, loggedInUser);

        //Set the adjustedRecipes on the user.
        loggedInUser.setAdjustedRecipes(adjustedRecipes);

        return adjustedRecipes;
    }

    /**
     * Adjusts the given recipes to match the user's daily calorie goal.
     *
     * @param weeklyRecipes The recipes to be adjusted.
     * @return The adjusted recipes.
     */
    public ArrayList<Recipe> adjustRecipesToUser(ArrayList<Recipe> weeklyRecipes, User user) {
        ArrayList<Recipe> adjustedRecipes = new ArrayList<>();
        User user = authenticationService.getUser();

        for (Recipe recipe : weeklyRecipes) {
            adjustedRecipes.add(adjustRecipeToUser(recipe, user.getDailyCalorieGoal()));
        }

        return adjustedRecipes;
    }

    /**
     * Fetches a user from the database by ID.
     *
     * @param userId The ID of the user being fetched.
     * @return The user being fetched.
     */
    public User getUserByID(int userId) throws EntityNotFoundException, SystemErrorException {
        try{
            return repository.getUserByID(userId);
        } catch(EmptyResultDataAccessException e){
            e.printStackTrace();
            throw new EntityNotFoundException("Kunne ikke finde en bruger med givet id");
        } catch (DataAccessException e ){
            e.printStackTrace();
            throw new SystemErrorException("Der skete en fejl med databasen. Prøv igen sener");
        }
    }

    /**
     * Updates the profile of the user with the specified user ID.
     *
     * @param user the user object containing the updated profile information.
     * @return returns the updated user object
     * @throws InputErrorException if the user object is null or if the user ID within the user object is 0.
     */
    public boolean updateUser(User user) throws InputErrorException, SystemErrorException {
        try {
            return repository.updateUser(user);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new SystemErrorException("Kunne ikke finde brugeren i databasen. Prøv igen senere");
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            throw new InputErrorException("Email bruges allerede");
        } catch (DataAccessException e){
            e.printStackTrace();
            throw new SystemErrorException("Der er sket en fejl med databasen. Prøv igen senere");
        }
    }

    public boolean handleUserSelfUpdate(User user) throws InputErrorException, SystemErrorException, EntityNotFoundException {
        if (!authenticationService.isValidUser(user)) {
            throw new InputErrorException("Udfyld venligst alle felter");
        }
        //Setup password and calorie goal
        authenticationService.hashAndSetPassword(user);
        setupDailyCalorieGoal(user);

        //Update the user and the session if the update went well.
        if(updateUser(user)){
            authenticationService.setSession(getUserByID(user.getUserId()));
            System.out.println(true);
            return true;
        } else {
            System.out.println(false)
            ;return false;
        }
    }

    public boolean handleAdminUserInfoUpdate(User user) throws InputErrorException, SystemErrorException {
        if (!authenticationService.isValidUser(user)) {
            throw new InputErrorException("Udfyld venligst alle felter");
        }
        authenticationService.hashAndSetPassword(user);
        setupDailyCalorieGoal(user);
        return updateUser(user);
    }

    public boolean handleAdminUserSubscriptionUpdate(Subscription subscription) throws SystemErrorException {
        try{
            return repository.updateSubscription(subscription);
        } catch(DataAccessException e){
            e.printStackTrace();
            throw new SystemErrorException("Der er sket en fejl med databasen. Prøv igen senere");
        }
    }

    public Subscription getSubscriptionByUserID(int userID){
        try{
            return repository.getSubscriptionByUserID(userID);
        } catch (EmptyResultDataAccessException e){
            e.printStackTrace();
            return null;
        }
    }

    public int daysLeftOnSubscription(int userID) {
        //Convert user sub end date to LocalDate
        LocalDate subscriptionEndDate = repository.getSubscriptionByUserID(userID).getSubscriptionEndDate().toLocalDate();

        LocalDate currentDate = LocalDate.now();

        // Calculate the difference in days between the current date and the subscription end date
        long daysLeft = ChronoUnit.DAYS.between(currentDate, subscriptionEndDate);

        return (int) daysLeft;
    }

    public List<User> getAllUsers(String searchQuery) throws SystemErrorException {
        try {
            return repository.getAllUsers(searchQuery);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            throw new SystemErrorException("Kunne ikke finde nogle brugere i databasen. Prøv igen senere. ");
        }
    }
    public void addFavoriteRecipe(Recipe recipe, User user) {
        user.addFavoriteRecipe(recipe);
    }
    public List<Recipe> addFavoriteRecipe(int userID, int recipeID) throws SystemErrorException {
        try {
            repository.addFavoriteRecipe(userID, recipeID);
        } catch (SystemErrorException e) {
            throw new SystemErrorException("Fejl ved tilføjelse af opskrift til favoritter");
        }
        return repository.getFavoriteRecipesByUserID(userID);
    }

    /**
     * Sets up a subscription for the user.
     */
    public void paySubscription() throws SystemErrorException, EntityNotFoundException{
        User user = authenticationService.getUser();
        int userID = user.getUserId();

        try{
            //Renew the users subscription
            Subscription subscription = repository.getSubscriptionByUserID(userID);
            authenticationService.renewSub(subscription);

            //Set up a new subscription if the user doesn't have one.
        } catch (EmptyResultDataAccessException e){
            Subscription newSubscription = setupNewSubscription();
            repository.insertSubscription(newSubscription,userID);
        }catch (DataAccessException e){
            throw new SystemErrorException("Der skete en database fejl. Prøv igen senere");
        }
    }

    public double calculateDailyCalorieBurn(User user) {
        double BMR = 0;
        double dailyCalorieBurn = 0;
        char gender = user.getGender();
        double weight = user.getWeight();
        double height = user.getHeight();
        double age = user.getAge();
        String activityLevel = user.getActivityLevel();

        if (gender == 'M') {
            BMR = (10 * weight) + (6.25 * height) - (5 * age) + 5;
        } else if (gender == 'F') {
            BMR = (10 * weight) + (6.25 * height) - (5 * age) - 161;
        }
        switch (activityLevel) {
            case "No exercise":
                dailyCalorieBurn = BMR * 1.2;
                break;
            case "Light activity":
                dailyCalorieBurn = BMR * 1.5;
                break;
            case "Average activity":
                dailyCalorieBurn = BMR * 1.7;
                break;
            case "Intense activity":
                dailyCalorieBurn = BMR * 1.9;
                break;
            case "Extreme activity":
                dailyCalorieBurn = BMR * 2.4;
                break;
        }
        return dailyCalorieBurn;
    }

    /**
     * Sets up all the necessary information that a user needs and saves the user in the database.
     * @param user A user with information to put in the database
     * @return Returns the user with an auto generated id attached.
     * @throws SystemErrorException If the system cant connect to database, sql errors etc.
     * @throws InputErrorException If the given user is missing important information or is trying to use an already existing email.
     */

    public User createUser(User user) throws SystemErrorException, InputErrorException {
        if(!authenticationService.isValidUser(user)){
            throw new InputErrorException("Venligst udfyld alle felterne korrekt");
        }
        authenticationService.hashAndSetPassword(user);
        user.setRole("User");
        return saveUser(user);
    }

    /**
     * Saves a user in the database.
     * @param user Save a user object.
     * @return The saved user object
     * @throws SystemErrorException If the system can't connect to the database, sql errors etc.
     * @throws InputErrorException If the given user is missing important information or is trying to use an already existing email.
     * @throws DuplicateKeyException If the user has the same id as an already existing user.
     */
    private User saveUser(User user) throws SystemErrorException, InputErrorException, DuplicateKeyException {
        try {
            return repository.createUser(user);
        } catch (DuplicateKeyException e) {
            e.printStackTrace();
            throw new InputErrorException("Email bruges allerede");
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            throw new InputErrorException("Venligst udfyld alle felterne korrekt");
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new SystemErrorException("Der skete en fejl under opretning af brugeren. Prøv igen senere");
        }
    }

    public double calculateDailyCalorieGoal(User user){
        double calorieGoal = 0;
        double dailyCalorieBurn = user.getDailyCalorieBurn();

        switch(user.getGoal()) {
            case ("Loose weight"):
                calorieGoal = dailyCalorieBurn - 500;
                break;
            case ("Maintain weight"):
                calorieGoal = dailyCalorieBurn;
                break;
            case ("Increase weight"):
                calorieGoal = dailyCalorieBurn + 500;
                break;
            case("Build muscle"):
                calorieGoal = dailyCalorieBurn + 300;
                break;
        }
        return calorieGoal;
    }

    public void setupDailyCalorieGoal(User user){
        user.setDailyCalorieBurn(calculateDailyCalorieBurn(user));
        user.setDailyCalorieGoal(calculateDailyCalorieGoal(user));
    }


    public void cancelUserSubscription(int userID) throws EntityNotFoundException, SystemErrorException {
        try{
            repository.cancelSubscription(userID);
        } catch (EmptyResultDataAccessException e){
            e.printStackTrace();
            throw new EntityNotFoundException("Kunne ikke finde et aktivt abonnement");
        } catch (DataAccessException e){
            e.printStackTrace();
            throw new SystemErrorException("Kunne ikke oprette forbindelse til databasen");
        }
    }


    /**
     * Sets up a new subscription.
     * @return The configured Subscription object.
     */
    public Subscription setupNewSubscription(){
        Subscription subscription = new Subscription();

        java.util.Date currentDate = new java.util.Date();
        java.sql.Date sqlStartDate = new java.sql.Date(currentDate.getTime());
        subscription.setSubscriptionStartDate(sqlStartDate);

        // Set the subscription end date one week later
        LocalDate localEndDate = sqlStartDate.toLocalDate().plusMonths(1);
        java.sql.Date sqlEndDate = java.sql.Date.valueOf(localEndDate);
        subscription.setSubscriptionEndDate(sqlEndDate);

        //Set subscription to me active= True
        subscription.setActiveSubscription(true);

        //Set the Price of the membership
        subscription.setSubscriptionPrice(0);
        return subscription;
    }


}










