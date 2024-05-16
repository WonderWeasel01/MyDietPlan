package com.WebApplcation.MyDietPlan.Service;

import com.WebApplcation.MyDietPlan.Exception.EntityNotFoundException;
import com.WebApplcation.MyDietPlan.Exception.InputErrorException;
import com.WebApplcation.MyDietPlan.Exception.SystemErrorException;
import com.WebApplcation.MyDietPlan.Model.Image;
import com.WebApplcation.MyDietPlan.Model.Ingredient;
import com.WebApplcation.MyDietPlan.Model.Recipe;
import com.WebApplcation.MyDietPlan.Repository.MyDietPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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

    @Autowired
    private MyDietPlanRepository repo;

    public WebsiteService(MyDietPlanRepository myDietPlanRepository){
        this.repo = myDietPlanRepository;
    }

/*
    public List<Recipe> adjustRecipesForUser(List<Recipe> weeklyDietPlan){

    }


 */

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

    public Image convertFileToImage(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setImageName(file.getOriginalFilename());
        image.setImageType(file.getContentType());
        image.setBlob(file.getBytes());
        return image;
    }


    /**
     * Validates a recipe and updates it.
     * @param recipe the updated recipe.
     * @return true if update went well
     * @throws InputErrorException Rethrows if validateRecipe(recipe) fails.
     * @throws SystemErrorException
     */
    public boolean updateRecipe(Recipe recipe) throws InputErrorException, SystemErrorException {
        validateRecipe(recipe);
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
    public boolean deleteRecipe(int recipeID){
        return repo.deleteRecipe(recipeID);
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
                return repo.updateRecipeActive(recipeID, 0);
            } else {
                if(getActiveRecipeAmount() < 21){
                    return repo.updateRecipeActive(recipeID, 1);
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

    public Recipe setupRecipeWithIngredients(Recipe recipe, List<Integer> ingredientIds, List<Integer> weights) throws InputErrorException {
        validateIngredientAndWeightSizes(ingredientIds,weights);
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientIds.size(); i++) {
            Ingredient ingredient = getIngredientByID(ingredientIds.get(i));
            ingredient.setWeightGrams(weights.get(i));
            ingredients.add(ingredient);
        }
        recipe.setIngredientList(ingredients);
        recipe.calculateMacros();
        return recipe;
    }

    private void validateRecipe(Recipe recipe) throws InputErrorException {
        if (recipe == null || !isValidRecipe(recipe)) {
            throw new InputErrorException("Udfyld venligst felterne korrekt");
        }
    }
    public boolean isValidRecipe(Recipe recipe){
        return StringUtils.hasText(recipe.getTitle()) && StringUtils.hasText(recipe.getPrepTime()) && StringUtils.hasText(recipe.getTimeOfDay())
                && StringUtils.hasText(recipe.getInstructions()) && recipe.recipeHasImage() && recipe.getIngredientList() != null
                && !recipe.getIngredientList().isEmpty();
    }
    private void validateIngredientAndWeightSizes(List<Integer> ingredientIds, List<Integer> weights) throws InputErrorException {
        if (ingredientIds.size() != weights.size()) {
            throw new InputErrorException("The number of ingredients and weights do not match.");
        }
    }

    public List<Recipe> getActiveRecipesForDay(String day) throws EntityNotFoundException {
        try{
            return repo.getActiveRecipeForDay(day);
        } catch (EmptyResultDataAccessException e){
            throw new EntityNotFoundException("Kunne ikke finde " + day + "'s" + "opskrifter");
        }
    }


    public List<Ingredient> getAllIngredients(){
        return repo.getAllIngredients();
    }

    public Recipe getRecipeById(int recipeID) throws EntityNotFoundException, SystemErrorException {
        try{
            return repo.getRecipeWithIngredientsByRecipeID(recipeID);
        } catch (EmptyResultDataAccessException e){
            System.err.println(e.getMessage());
            throw new EntityNotFoundException("Kunne ikke finde en opskrift med givet id");
        } catch (DataAccessException e){
            System.err.println(e.getMessage());
            throw new SystemErrorException("Der er sket en fejl. Prøv igen senere");
        }
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

    public ArrayList<Recipe> adjustRecipesToUser(double dailyCalorieBurn, ArrayList<Recipe> recipes){
        ArrayList<Recipe> adjustedRecipes = new ArrayList<>();


        for(int i = 0; i<recipes.size(); i++){
            Recipe recipeToAdjust = recipes.get(i);
            adjustedRecipes.add(recipeToAdjust.adjustRecipeToUser(dailyCalorieBurn));
        }
        System.out.println(adjustedRecipes);
        return adjustedRecipes;
    }



    /*public Recipe getRecipeByTimeOfDay(int recipeID) throws EntityNotFoundException, SystemErrorException {
        try{
            return repo.getRecipeWithIngredientsByRecipeID(recipeID);
        } catch (EmptyResultDataAccessException e){
            System.err.println(e.getMessage());
            throw new EntityNotFoundException("Kunne ikke finde en opskrift med givet id");
        } catch (DataAccessException e){
            System.err.println(e.getMessage());
            throw new SystemErrorException("Der er sket en fejl. Prøv igen senere");
        }
    }*/



/*
    public Recipe getRecipe(int recipeID) {

    }

    public Recipe updateRecipe(int recipeID, Recipe newRecipe) {

    }

    public Recipe createRecipe(Recipe recipe) {

    }

    public boolean deleteRecipe(int recipeID){

    }



    public List<Recipe> getAllBreakfastRecipe(){


    }

    public List<Recipe> getAllLunchRecipe(){

    }

    public List<Recipe> getAllDinnerRecipe(){

    }
*/
}

