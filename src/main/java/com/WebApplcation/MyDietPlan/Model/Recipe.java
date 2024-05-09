package com.WebApplcation.MyDietPlan.Model;

import java.util.ArrayList;
import java.util.HashMap;

public class Recipe {

    int recipeID;
    int totalCalories;
    int totalCarbohydrates;
    int totalFat;
    int totalProtein;
    String timeOfDay;
    // måske fil?
    String instructions;

    ArrayList<Ingredient> ingredientList = new ArrayList<>();

    public Recipe() {

    }

    public Recipe(String timeOfDay, String instructions, Boolean active, ArrayList<Ingredient> ingredientList) {
        this.timeOfDay = timeOfDay;
        this.instructions = instructions;
        this.active = active;
        this.ingredientList = ingredientList;
    }

    public Recipe(int totalCalories, int totalCarbohydrates, int totalFat, int totalProtein, String timeOfDay, String instructions, ArrayList<Ingredient> ingredientList, boolean active) {
        this.totalCalories = totalCalories;
        this.totalCarbohydrates = totalCarbohydrates;
        this.totalFat = totalFat;
        this.totalProtein = totalProtein;
        this.timeOfDay = timeOfDay;
        this.instructions = instructions;
        this.ingredientList = ingredientList;
        this.active = active;
    }

    public Recipe(int recipeID, int totalCalories, int totalCarbohydrates, int totalFat, int totalProtein, String timeOfDay, String instructions, Boolean active, ArrayList<Ingredient> ingredientList) {
        this.recipeID = recipeID;
        this.totalCalories = totalCalories;
        this.totalCarbohydrates = totalCarbohydrates;
        this.totalFat = totalFat;
        this.totalProtein = totalProtein;
        this.timeOfDay = timeOfDay;
        this.instructions = instructions;
        this.active = active;
        this.ingredientList = ingredientList;
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
