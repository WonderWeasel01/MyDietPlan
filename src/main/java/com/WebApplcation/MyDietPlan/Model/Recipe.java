package com.WebApplcation.MyDietPlan.Model;

import java.util.ArrayList;

public class Recipe {

    private int recipeID;
    private String title;
    private double totalCalories = 0;
    private double totalCarbohydrates = 0;
    private double totalFat = 0;
    private double totalProtein = 0;
    private String prepTime;
    private String timeOfDay;
    private String instructions;
    private Boolean active = false;
    private String pictureURL;

    ArrayList<Ingredient> ingredientList;

    public Recipe() {

    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public Recipe(String prepTime, String timeOfDay, String instructions, Boolean active, ArrayList<Ingredient> ingredientList) {
        this.prepTime = prepTime;
        this.timeOfDay = timeOfDay;
        this.instructions = instructions;
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
        for(int i = 0; i<ingredientList.size(); i++) {
            Ingredient ingredient = ingredientList.get(i);
           this.totalProtein += ingredient.getProteinPerHundredGrams() * (ingredient.getWeightGrams() / 100);
           this.totalFat += ingredient.getFatPerHundredGrams() * (ingredient.getWeightGrams() / 100) ;
           this.totalCarbohydrates += ingredient.getCarbohydratesPerHundredGrams() * (ingredient.getWeightGrams() / 100);
           this.totalCalories += ingredient.getCaloriesPerHundredGrams() * (ingredient.getWeightGrams() / 100);
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

    public double getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(double totalCalories) {
        this.totalCalories = totalCalories;
    }

    public double getTotalCarbohydrates() {
        return totalCarbohydrates;
    }

    public void setTotalCarbohydrates(double totalCarbohydrates) {
        this.totalCarbohydrates = totalCarbohydrates;
    }

    public double getTotalFat() {
        return totalFat;
    }

    public void setTotalFat(double totalFat) {
        this.totalFat = totalFat;
    }

    public double getTotalProtein() {
        return totalProtein;
    }

    public void setTotalProtein(double totalProtein) {
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
                ", title='" + title + '\'' +
                ", totalCalories=" + totalCalories +
                ", totalCarbohydrates=" + totalCarbohydrates +
                ", totalFat=" + totalFat +
                ", totalProtein=" + totalProtein +
                ", prepTime='" + prepTime + '\'' +
                ", timeOfDay='" + timeOfDay + '\'' +
                ", instructions='" + instructions + '\'' +
                ", active=" + active +
                ", pictureURL='" + pictureURL + '\'' +
                ", ingredientList=" + ingredientList +
                '}';
    }
}
