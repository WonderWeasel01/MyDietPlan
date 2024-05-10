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
    private WeeklyDietPlan wdp;
    ArrayList<Recipe> favoriteRecipes = new ArrayList<>();

  public User() {
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

    public WeeklyDietPlan getWdp() {
        return wdp;
    }

    public void setWdp(WeeklyDietPlan wdp) {
        this.wdp = wdp;
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
