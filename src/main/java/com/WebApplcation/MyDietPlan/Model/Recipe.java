package com.WebApplcation.MyDietPlan.Model;

import org.springframework.util.StringUtils;

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
    private String day;
    private Image image;

    ArrayList<Ingredient> ingredientList;

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

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Recipe calculateMacros(){
        //Ensure that the result is correct if the method is called more than once.
        Recipe adjustedRecipe = this;
        adjustedRecipe.totalCalories = 0;
        adjustedRecipe.totalProtein = 0;
        adjustedRecipe.totalFat = 0;
        adjustedRecipe.totalCarbohydrates = 0;


        for(int i = 0; i<adjustedRecipe.ingredientList.size(); i++) {
            Ingredient ingredient = adjustedRecipe.ingredientList.get(i);
            adjustedRecipe.totalProtein += ingredient.getProteinPerHundredGrams() * (ingredient.getWeightGrams() / 100.0);
            adjustedRecipe.totalFat += ingredient.getFatPerHundredGrams() * (ingredient.getWeightGrams() / 100.0) ;
            adjustedRecipe.totalCarbohydrates += ingredient.getCarbohydratesPerHundredGrams() * (ingredient.getWeightGrams() / 100.0);
            adjustedRecipe.totalCalories += ingredient.getCaloriesPerHundredGrams() * (ingredient.getWeightGrams() / 100.0);
        }

        //Round the total macros to avoid unnecessary decimals
        adjustedRecipe.totalProtein = Math.round(adjustedRecipe.totalProtein * 100.0) / 100.0;
        adjustedRecipe.totalFat = Math.round(adjustedRecipe.totalFat * 100.0) / 100.0;
        adjustedRecipe.totalCarbohydrates = Math.round(adjustedRecipe.totalCarbohydrates * 100.0) / 100.0;
        adjustedRecipe.totalCalories = Math.round(adjustedRecipe.totalCalories * 100.0) / 100.0;
        return adjustedRecipe;
    }


    public Recipe adjustRecipeToUser(double dailyCalorieBurn){
        Recipe adjustedRecipe = this;

        double recipeCalorieGoal = dailyCalorieBurn/3.0;
        double multiplier = recipeCalorieGoal/adjustedRecipe.totalCalories;


        for(int i = 0; i<adjustedRecipe.ingredientList.size(); i++){
            adjustedRecipe.ingredientList.get(i).setWeightGrams((adjustedRecipe.ingredientList.get(i).getWeightGrams() * multiplier));
        }
        adjustedRecipe.calculateMacros();
        return adjustedRecipe;
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
