package com.WebApplcation.MyDietPlan.Service;

import com.WebApplcation.MyDietPlan.Exception.EntityNotFoundException;
import com.WebApplcation.MyDietPlan.Exception.InputErrorException;
import com.WebApplcation.MyDietPlan.Exception.SystemErrorException;
import com.WebApplcation.MyDietPlan.Model.Image;
import com.WebApplcation.MyDietPlan.Model.Ingredient;
import com.WebApplcation.MyDietPlan.Model.Recipe;
import com.WebApplcation.MyDietPlan.Model.User;
import com.WebApplcation.MyDietPlan.Repository.MyDietPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class WebsiteService {

    private final MyDietPlanRepository repo;
    private final AuthenticationService authenticationService;

    @Autowired
    public WebsiteService(AuthenticationService authenticationService, MyDietPlanRepository myDietPlanRepository){
        this.authenticationService = authenticationService;
        this.repo = myDietPlanRepository;
    }

    public Recipe createRecipe(Recipe recipe) throws SystemErrorException, InputErrorException {
        validateRecipe(recipe);
        try {
            System.out.println(recipe);
            return repo.createRecipe(recipe);
        } catch (DataAccessException e) {
            e.printStackTrace();
            System.err.println("Error accessing the database: " + e.getMessage());
            throw new SystemErrorException("Der er sket en fejl på vores side. Prøv igen senere.");
        }
    }

    private Image convertFileToImage(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setImageName(file.getOriginalFilename());
        image.setImageType(file.getContentType());
        image.setBlob(file.getBytes());
        return image;
    }

    public void setupRecipeWithImage(Recipe recipe, MultipartFile imageFile) throws SystemErrorException {
        try{
            Image image = convertFileToImage(imageFile);
            recipe.setImage(image);
        }catch (IOException e){
            e.printStackTrace();
            throw new SystemErrorException("Der skete en fejl under forsøget på at gemme billedet");
        }
    }


    /**
     * Validates a recipe and updates it.
     * @param recipe the updated recipe.
     * @return true if update went well
     * @throws InputErrorException Rethrows if validateRecipe(recipe) fails.
     * @throws SystemErrorException
     */
    public boolean updateRecipe(Recipe recipe) throws InputErrorException, SystemErrorException {
        try{
            if(repo.updateRecipeWithoutIngredients(recipe)){
                return updateRecipeIngredients(recipe.getRecipeID(), recipe.getIngredientList());
            } throw new SystemErrorException("Der er sket en fejl på vores side. Prøv igen senere.");
        } catch (DataAccessException e){
            System.err.println("Error accessing the database: " + e.getMessage());
            throw new SystemErrorException("Der er sket en fejl på vores side. Prøv igen senere.");
        }
    }

    /**
     * Deletes a recipe.
     * @param recipeID ID of the recipe that is being deleted
     * @return true if the deletion went well
     */
    public boolean deleteRecipe(int recipeID) throws SystemErrorException {
        try{
            return repo.deleteRecipe(recipeID);
        } catch (EmptyResultDataAccessException e){
            e.printStackTrace();
            throw new SystemErrorException("Der er sket en fejl. Prøv igen senere");
        } catch (DataAccessException e){
            e.printStackTrace();
            throw new SystemErrorException("Der er sket en fejl. Prøv igen senere");
        }
    }

    /**
     * Checks a recipes status and sets it to the opposite.
     * @param recipeID ID of the recipe that the admin wants to update the status of.
     * @return True if status change went well.
     * @throws SystemErrorException If 21 active recipe already exist
     * @throws EntityNotFoundException If the recipe that is being changed couldn't be found
     */
    public boolean updateRecipeActiveStatus(int recipeID) throws SystemErrorException, EntityNotFoundException {
        try {
            if (getRecipeById(recipeID).getActive()) {
                return repo.updateRecipeActiveStatus(recipeID, 0);
            } else {
                if(getActiveRecipeAmount() < 21){
                    return repo.updateRecipeActiveStatus(recipeID, 1);
                } else throw new SystemErrorException("Der er allerede 21 aktive opskrifter. Deaktiver en eller flere aktive opskrifter for at tilføje flere");
            }
        } catch (EmptyResultDataAccessException e){
            throw new EntityNotFoundException("Der er sket en fejl med opdateringen af opskrift status. Prøv igen senere");
        }
    }
    public int getActiveRecipeAmount() throws SystemErrorException {
        try{
            return repo.getActiveRecipeAmount();
        }catch (NullPointerException | EmptyResultDataAccessException e){
            return 0;
        } catch (DataAccessException e){
            throw new SystemErrorException("Error getting active recipe amount. Trouble accessing database");
        }
    }

    public boolean updateRecipeIngredients(int recipeID, List<Ingredient> newIngredients) throws SystemErrorException {
        if(repo.deleteIngredientsFromRecipe(recipeID)){
           return repo.insertIngredientsOntoRecipe(recipeID,newIngredients);
        } else throw new SystemErrorException("Der er sket en fejl på vores side. Prøv igen senere.");
    }

    public void setupRecipeWithIngredients(Recipe recipe, List<Integer> ingredientIds, List<Integer> weights) throws InputErrorException {
        validateIngredientAndWeightSizes(ingredientIds,weights);
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientIds.size(); i++) {
            Ingredient ingredient = getIngredientByID(ingredientIds.get(i));
            ingredient.setWeightGrams(weights.get(i));
            ingredients.add(ingredient);
        }
        recipe.setIngredientList(ingredients);
        calculateAndSetMacros(recipe);
    }
    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public void calculateAndSetMacros(Recipe recipe){
        //Ensure that the result is correct if the method is called more than once.
        recipe.setTotalCalories(0);
        recipe.setTotalProtein(0);
        recipe.setTotalFat(0);
        recipe.setTotalCarbohydrates(0);

        for(int i = 0; i<recipe.getIngredientList().size(); i++) {
            Ingredient ingredient = recipe.getIngredientList().get(i);

            recipe.setTotalProtein(recipe.getTotalProtein() +
                    (ingredient.getProteinPerHundredGrams() * ingredient.getWeightGrams() / 100.0));

            recipe.setTotalFat(recipe.getTotalFat() +
                    (ingredient.getFatPerHundredGrams() * ingredient.getWeightGrams() / 100.0));

            recipe.setTotalCarbohydrates(recipe.getTotalCarbohydrates() +
                    (ingredient.getCarbohydratesPerHundredGrams() * ingredient.getWeightGrams() / 100.0));

            recipe.setTotalCalories(recipe.getTotalCalories() +
                    (ingredient.getCaloriesPerHundredGrams() * ingredient.getWeightGrams() / 100.0));
        }

        //Round the total macros to avoid unnecessary decimals
        recipe.setTotalProtein(roundToTwoDecimals(recipe.getTotalProtein()));
        recipe.setTotalFat(roundToTwoDecimals(recipe.getTotalFat()));
        recipe.setTotalCarbohydrates(roundToTwoDecimals(recipe.getTotalCarbohydrates()));
        recipe.setTotalCalories(roundToTwoDecimals(recipe.getTotalCalories()));
    }


    public Recipe adjustRecipeToUser(Recipe recipe , double dailyCalorieGoal){
        double recipeCalorieGoal = 0;
        switch (recipe.getTimeOfDay()){
            case("Breakfast"):
                recipeCalorieGoal = dailyCalorieGoal * 0.4;
                break;
            case("Lunch"), ("Dinner"):
                recipeCalorieGoal = dailyCalorieGoal * 0.3;
                break;
        }

        double multiplier = recipeCalorieGoal/recipe.getTotalCalories();

        for(int i = 0; i<recipe.getIngredientList().size(); i++){
            recipe.getIngredientList().get(i).setWeightGrams((Math.round(recipe.getIngredientList().get(i).getWeightGrams() * multiplier)));
        }
        calculateAndSetMacros(recipe);
        return recipe;
    }

    private void validateRecipe(Recipe recipe) throws InputErrorException {
        if (recipe == null || !isValidRecipe(recipe)) {
            throw new InputErrorException("Udfyld venligst felterne korrekt");
        }
    }
    public boolean isValidRecipe(Recipe recipe){
        return StringUtils.hasText(recipe.getTitle()) && StringUtils.hasText(recipe.getPrepTime()) && StringUtils.hasText(recipe.getTimeOfDay())
                && StringUtils.hasText(recipe.getInstructions()) && StringUtils.hasText(recipe.getImage().getImageName()) && StringUtils.hasText(recipe.getImage().getImageType()) && recipe.getIngredientList() != null
                && !recipe.getIngredientList().isEmpty();
    }
    private void validateIngredientAndWeightSizes(List<Integer> ingredientIds, List<Integer> weights) throws InputErrorException {
        if (ingredientIds.size() != weights.size()) {
            throw new InputErrorException("The number of ingredients and weights do not match.");
        }
    }
    public List<Ingredient> getAllIngredients(){
        return repo.getAllIngredients();
    }

    public Recipe getRecipeById(int recipeID) throws EntityNotFoundException, SystemErrorException {
        try{
            return repo.getRecipeWithIngredientsByRecipeID(recipeID);
        } catch (EmptyResultDataAccessException e){
            e.printStackTrace();
            throw new EntityNotFoundException("Kunne ikke finde en opskrift med givet id");
        } catch (DataAccessException e){
            e.printStackTrace();
            throw new SystemErrorException("Der er sket en fejl. Prøv igen senere");
        }
    }

    public Recipe getAdjustedRecipeById(int recipeID) throws SystemErrorException {
        ArrayList<Recipe> recipes = authenticationService.getUser().getAdjustedRecipes();
        for(int i = 0; i<recipes.size(); i++){
            if(recipes.get(i).getRecipeID() == recipeID){
                return recipes.get(i);
            }
        } throw new SystemErrorException("Der er sket en fejl. Prøv igen senere");
    }

    public Ingredient createIngredient(Ingredient ingredient) throws SystemErrorException, InputErrorException {
        try {
            if(ingredient != null && isValidIngredient(ingredient)) {
                return repo.createIngredient(ingredient);
            } else throw new InputErrorException("Udfyld venligst alle felterne korrekt");
        }
        catch (DataAccessException e) {
            e.printStackTrace();
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

    public ArrayList<Recipe> getAllRecipes() throws EntityNotFoundException {
        try{
            return new ArrayList<>(repo.getAllRecipeWithoutIngredients());
        } catch (EmptyResultDataAccessException e){
            throw new EntityNotFoundException("Du har ikke tilføjet nogle opskrifter endnu!");
        }
    }
    public ArrayList<Recipe> getAllDeactivatedRecipes() throws EntityNotFoundException {
        try{
            return new ArrayList<>(repo.getAllDeactivatedRecipeWithoutIngredients());
        } catch (EmptyResultDataAccessException e){
            throw new EntityNotFoundException("Du har ikke tilføjet nogle opskrifter endnu!");
        }
    }

    public ArrayList<Recipe> getAllActiveRecipes() throws EntityNotFoundException {
        try {
            ArrayList<Recipe> recipes = (ArrayList<Recipe>) repo.getAllActiveRecipe();
            for (int i = 0; i<recipes.size(); i++){
                Recipe recipe = recipes.get(i);

                String base64Image = Base64.getEncoder().encodeToString(recipe.getImage().getBlob());

                recipe.getImage().setBase64Image(base64Image);
            }
            return recipes;
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Du har ikke tilføjet nogle opskrifter endnu!");
        }
    }

    /**
     * Adjusts the macros of the weekly recipes and sets the logged-in user.adjustRecipes to the newly adjusted recipes.
     * @param dailyCalorieGoal The users dailyCalorieBurn
     * @param weeklyRecipes The list of recipes that has been chosen for this week's diet plan.
     * @return The
     */
    public ArrayList<Recipe> adjustRecipesToUser(double dailyCalorieGoal, ArrayList<Recipe> weeklyRecipes){
        ArrayList<Recipe> adjustedRecipes = new ArrayList<>();

        for(int i = 0; i<weeklyRecipes.size(); i++){
            Recipe recipeToAdjust = weeklyRecipes.get(i);
            adjustedRecipes.add(adjustRecipeToUser(recipeToAdjust,dailyCalorieGoal));
        }
        authenticationService.getUser().setAdjustedRecipes(adjustedRecipes);
        return adjustedRecipes;
    }

    public User getUserByID (int userId) {
        return repo.getUserByID(userId);
    }

    /**
     * Updates the profile of the user with the specified user ID.
     * @param user the user object containing the updated profile information.
     * @return returns the updated user object
     * @throws InputErrorException if the user object is null or if the user ID within the user object is 0.
     */
    public User updateUser(User user) throws InputErrorException, SystemErrorException {
        if(!authenticationService.isValidUser(user)){
            throw new InputErrorException("Udfyld venligst alle felter");
        }
        try{
            authenticationService.hashAndSetPassword(user);
            user.setupDailyCalorieGoal();
            //Attempt to update the user in the database
            User updatedUser = repo.updateUser(user);
            //Update the logged-in user with the updated information
            authenticationService.setSession(updatedUser);

            return updatedUser;
        } catch (EmptyResultDataAccessException e){
            e.printStackTrace();
            throw new SystemErrorException("Kunne ikke finde brugeren i databasen. Prøv igen senere");
        } catch (DuplicateKeyException e){
            throw new InputErrorException("Email bruges allerede");
        }

    }







}

