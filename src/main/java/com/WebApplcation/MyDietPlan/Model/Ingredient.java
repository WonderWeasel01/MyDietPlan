package com.WebApplcation.MyDietPlan.Model;

public class Ingredient {

    private int ingredientID;
    private String name;
    private double weightGrams;
    private double proteinPerHundredGrams;
    private double fatPerHundredGrams;
    private double carbohydratesPerHundredGrams;
    private double caloriesPerHundredGrams;



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

    public double getCarbohydratesPerHundredGrams() {
        return carbohydratesPerHundredGrams;
    }

    public void setCarbohydratesPerHundredGrams(double carbohydratesPerHundredGrams) {
        this.carbohydratesPerHundredGrams = carbohydratesPerHundredGrams;
    }

    public double getFatPerHundredGrams() {
        return fatPerHundredGrams;
    }

    public void setFatPerHundredGrams(double fatPerHundredGrams) {
        this.fatPerHundredGrams = fatPerHundredGrams;
    }

    public double getProteinPerHundredGrams() {
        return proteinPerHundredGrams;
    }

    public void setProteinPerHundredGrams(double proteinPerHundredGrams) {
        this.proteinPerHundredGrams = proteinPerHundredGrams;
    }

    public double getCaloriesPerHundredGrams() {
        return caloriesPerHundredGrams;
    }

    public void setCaloriesPerHundredGrams(double caloriesPerHundredGrams) {
        this.caloriesPerHundredGrams = caloriesPerHundredGrams;
    }

    public double getWeightGrams() {
        return weightGrams;
    }

    public void setWeightGrams(double weightGrams) {
        this.weightGrams = weightGrams;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "ingredientID=" + ingredientID +
                ", name='" + name + '\'' +
                ", weightGrams=" + weightGrams +
                ", proteinPerHundredGrams=" + proteinPerHundredGrams +
                ", fatPerHundredGrams=" + fatPerHundredGrams +
                ", carbohydratesPerHundredGrams=" + carbohydratesPerHundredGrams +
                ", caloriesPerHundredGrams=" + caloriesPerHundredGrams +
                '}';
    }
}
