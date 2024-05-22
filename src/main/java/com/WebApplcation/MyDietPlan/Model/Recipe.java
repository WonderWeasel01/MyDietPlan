package com.WebApplcation.MyDietPlan.Model;

import java.util.ArrayList;

public class Recipe {

    private int recipeID;
    private String title;
    private double totalCalories;
    private double totalCarbohydrates;
    private double totalFat;
    private double totalProtein;
    private String prepTime;
    private String timeOfDay;
    private String instructions;
    private Boolean active = false;
    private String day;
    private Image image;

    private ArrayList<Ingredient> ingredientList;

    public Recipe() {

    }

    public Recipe(String prepTime, String timeOfDay, String instructions, Boolean active, ArrayList<Ingredient> ingredientList) {
        this.prepTime = prepTime;
        this.timeOfDay = timeOfDay;
        this.instructions = instructions;
        this.ingredientList = ingredientList;

    }



    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }


    public Recipe(int recipeID, String title, double totalCalories, double totalCarbohydrates, double totalFat, double totalProtein, String prepTime, String timeOfDay, String instructions, Boolean active, ArrayList<Ingredient> ingredientList) {
        this.recipeID = recipeID;
        this.title = title;
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

    public Recipe(int recipeID, String title, double totalCalories, double totalCarbohydrates, double totalFat, double totalProtein, String prepTime, String timeOfDay, String instructions, Boolean active, String day, Image image, ArrayList<Ingredient> ingredientList) {
        this.recipeID = recipeID;
        this.title = title;
        this.totalCalories = totalCalories;
        this.totalCarbohydrates = totalCarbohydrates;
        this.totalFat = totalFat;
        this.totalProtein = totalProtein;
        this.prepTime = prepTime;
        this.timeOfDay = timeOfDay;
        this.instructions = instructions;
        this.active = active;
        this.day = day;
        this.image = image;
        this.ingredientList = ingredientList;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
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
                ", day='" + day + '\'' +
                ", image=" + image +
                ", ingredientList=" + ingredientList +
                '}';
    }
}
