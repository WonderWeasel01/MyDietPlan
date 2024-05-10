package com.WebApplcation.MyDietPlan.Model;

public class Ingredient {

    int ingredientID;
    String name;
    int weightGrams;
    int proteinPerHundredGrams;
    int fatPerHundredGrams;
    int carbohydratesPerHundredGrams;
    int caloriesPerHundredGrams;


    public Ingredient() {

    }

    public Ingredient(String name, int carbohydratesPerHundredGrams, int fatPerHundredGrams, int proteinPerHundredGrams, int caloriesPerHundredGrams, int weightGrams) {
        this.name = name;
        this.carbohydratesPerHundredGrams = carbohydratesPerHundredGrams;
        this.fatPerHundredGrams = fatPerHundredGrams;
        this.proteinPerHundredGrams = proteinPerHundredGrams;
        this.caloriesPerHundredGrams = caloriesPerHundredGrams;
        this.weightGrams = weightGrams;
    }

    public int getIngredientID() {
        return ingredientID;
    }

    public void setIngredientID(int ingredientID) {
        this.ingredientID = ingredientID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCarbohydratesPerHundredGrams() {
        return carbohydratesPerHundredGrams;
    }

    public void setCarbohydratesPerHundredGrams(int carbohydratesPerHundredGrams) {
        this.carbohydratesPerHundredGrams = carbohydratesPerHundredGrams;
    }

    public int getFatPerHundredGrams() {
        return fatPerHundredGrams;
    }

    public void setFatPerHundredGrams(int fatPerHundredGrams) {
        this.fatPerHundredGrams = fatPerHundredGrams;
    }

    public int getProteinPerHundredGrams() {
        return proteinPerHundredGrams;
    }

    public void setProteinPerHundredGrams(int proteinPerHundredGrams) {
        this.proteinPerHundredGrams = proteinPerHundredGrams;
    }

    public int getCaloriesPerHundredGrams() {
        return caloriesPerHundredGrams;
    }

    public void setCaloriesPerHundredGrams(int caloriesPerHundredGrams) {
        this.caloriesPerHundredGrams = caloriesPerHundredGrams;
    }

    public int getWeightGrams() {
        return weightGrams;
    }

    public void setWeightGrams(int weightGrams) {
        this.weightGrams = weightGrams;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "ingredientID=" + ingredientID +
                ", name='" + name + '\'' +
                ", carbohydratesPerHundredGrams=" + carbohydratesPerHundredGrams +
                ", fatPerHundredGrams=" + fatPerHundredGrams +
                ", proteinPerHundredGrams=" + proteinPerHundredGrams +
                ", caloriesPerHundredGrams=" + caloriesPerHundredGrams +
                ", weightGrams=" + weightGrams +
                '}';
    }
}
