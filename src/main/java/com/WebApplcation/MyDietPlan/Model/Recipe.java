package com.WebApplcation.MyDietPlan.Model;

import java.util.ArrayList;
import java.util.HashMap;

public class Recipe {

    private int recipeID;
    private String title;
    private int totalCalories;
    private int totalCarbohydrates;
    private int totalFat;
    private int totalProtein;
    private String prepTime;
    private String timeOfDay;
    // måske fil?
    private String instructions;
    private Boolean active;

    ArrayList<Ingredient> ingredientList;

    public Recipe() {

    }

    public Recipe(String prepTime, String timeOfDay, String instructions, Boolean active, ArrayList<Ingredient> ingredientList) {
        this.prepTime = prepTime;
        this.timeOfDay = timeOfDay;
        this.instructions = instructions;
        this.active = active;
        this.ingredientList = ingredientList;

    }

    public Recipe(int totalCalories, int totalCarbohydrates, int totalFat, int totalProtein, String prepTime, String timeOfDay, String instructions, Boolean active, ArrayList<Ingredient> ingredientList) {
        this.totalCalories = totalCalories;
        this.totalCarbohydrates = totalCarbohydrates;
        this.totalFat = totalFat;
        this.totalProtein = totalProtein;
        this.prepTime = prepTime;
        this.timeOfDay = timeOfDay;
        this.instructions = instructions;
        this.active = active;
        this.ingredientList = ingredientList;
    }

    public Recipe(int recipeID, int totalCalories, int totalCarbohydrates, int totalFat, int totalProtein, String prepTime, String timeOfDay, String instructions, Boolean active, ArrayList<Ingredient> ingredientList) {
        this.recipeID = recipeID;
        this.totalCalories = totalCalories;
        this.totalCarbohydrates = totalCarbohydrates;
        this.totalFat = totalFat;
        this.totalProtein = totalProtein;
        this.prepTime = prepTime;
        this.timeOfDay = timeOfDay;
        this.instructions = instructions;
        this.active = active;
        this.ingredientList = ingredientList;
    }

    public void calculateMacros(){
        for (Ingredient ingredient : ingredientList) {
            this.totalFat += ingredient.fatPerHundredGrams * (ingredient.weightGrams / 100);
            this.totalProtein += ingredient.proteinPerHundredGrams * (ingredient.weightGrams / 100);
            this.totalCarbohydrates += ingredient.carbohydratesPerHundredGrams * (ingredient.weightGrams / 100);
            this.totalCalories += ingredient.caloriesPerHundredGrams * (ingredient.weightGrams / 100);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(String prepTime) {
        this.prepTime = prepTime;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public int getRecipeID() {return recipeID;}

    public void setRecipeID(int recipeID) {this.recipeID = recipeID;}

    public int getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(int totalCalories) {
        this.totalCalories = totalCalories;
    }

    public int getTotalCarbohydrates() {
        return totalCarbohydrates;
    }

    public void setTotalCarbohydrates(int totalCarbohydrates) {
        this.totalCarbohydrates = totalCarbohydrates;
    }

    public int getTotalFat() {
        return totalFat;
    }

    public void setTotalFat(int totalFat) {
        this.totalFat = totalFat;
    }

    public int getTotalProtein() {
        return totalProtein;
    }

    public void setTotalProtein(int totalProtein) {
        this.totalProtein = totalProtein;
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public ArrayList<Ingredient> getIngredientList() {
        return ingredientList;
    }

    public void setIngredientList(ArrayList<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "recipeID=" + recipeID +
                ", totalCalories=" + totalCalories +
                ", totalCarbohydrates=" + totalCarbohydrates +
                ", totalFat=" + totalFat +
                ", totalProtein=" + totalProtein +
                ", timeOfDay='" + timeOfDay + '\'' +
                ", instructions='" + instructions + '\'' +
                ", active=" + active +
                ", ingredientList=" + ingredientList +
                '}';
    }
}
