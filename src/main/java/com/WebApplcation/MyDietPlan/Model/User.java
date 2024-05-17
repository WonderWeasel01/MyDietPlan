package com.WebApplcation.MyDietPlan.Model;

import java.util.ArrayList;

public class User {

    private int userId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private char gender;
    private int height;
    private int weight;
    private int age;
    private String activityLevel;
    private String goal;
    private String role;
    ArrayList<Recipe> favoriteRecipes = new ArrayList<>();
    ArrayList<Recipe> weeklyDietPlan = new ArrayList<>();
    private Subscription subscription;
    private double dailyCalorieBurn;
    private double dailyCalorieGoal;

  public User() {
    }
    public User(String firstName, String lastName) {
    }

    public User(String email, String password, String firstName, String lastName, char gender, int height, int weight, int age, String activityLevel, String goal, String role) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.age = age;
        this.activityLevel = activityLevel;
        this.goal = goal;
        this.role = role;
    }

    public User(int userId, String email, String password, String firstName, String lastName, char gender, int height, int weight, int age, String activityLevel, String goal, String role) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.age = age;
        this.activityLevel = activityLevel;
        this.goal = goal;
        this.role = role;
    }

    public double getDailyCalorieBurn() {
        return dailyCalorieBurn;
    }

    public void setDailyCalorieBurn(double dailyCalorieBurn) {
        this.dailyCalorieBurn = dailyCalorieBurn;
    }

    public ArrayList<Recipe> getWeeklyDietPlan() {
        return weeklyDietPlan;
    }

    public void setWeeklyDietPlan(ArrayList<Recipe> weeklyDietPlan) {
        this.weeklyDietPlan = weeklyDietPlan;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public void calculateDailyCalorieBurn() {
        double BMR = 0;
        if (this.gender == 'M') {
            BMR = (10 * weight) + (6.25 * height) - (5 * age) + 5;
        } else if (this.gender == 'F') {
            BMR = (10 * weight) + (6.25 * height) - (5 * age) - 161;
        }
        switch (activityLevel) {
            case "No exercise":
                this.dailyCalorieBurn = BMR * 1.2;
                break;
            case "Light activity":
                this.dailyCalorieBurn = BMR * 1.375;
                break;
            case "Average activity":
                this.dailyCalorieBurn = BMR * 1.55;
                break;
            case "Intense activity":
                this.dailyCalorieBurn = BMR * 1.725;
                break;
            case "Extreme activity":
                this.dailyCalorieBurn = BMR * 1.9;
                break;
        }
    }

    public void calculateDailyCalorieGoal(String goal, double dailyCalorieBurn){
      double calorieGoal = 0;
      switch(goal) {
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
      this.dailyCalorieGoal = calorieGoal;
    }

    public void setupDailyCalorieGoal(){
      calculateDailyCalorieBurn();
      calculateDailyCalorieGoal(this.goal,this.dailyCalorieBurn);
    }

    public double getDailyCalorieGoal() {
        return dailyCalorieGoal;
    }

    public void setDailyCalorieGoal(double dailyCalorieGoal) {
        this.dailyCalorieGoal = dailyCalorieGoal;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public ArrayList<Recipe> getFavoriteRecipes() {
        return favoriteRecipes;
    }

    public void setFavoriteRecipes(ArrayList<Recipe> favoriteRecipes) {
        this.favoriteRecipes = favoriteRecipes;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", gender=" + gender +
                ", height=" + height +
                ", weight=" + weight +
                ", age=" + age +
                ", role='" + role + '\'' +
                ", activityLevel='" + activityLevel + '\'' +
                ", goal='" + goal + '\'' +
                ", favoriteRecipes=" + favoriteRecipes +
                '}';
    }
}
