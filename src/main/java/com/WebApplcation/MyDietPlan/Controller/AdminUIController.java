package com.WebApplcation.MyDietPlan.Controller;

import com.WebApplcation.MyDietPlan.Exception.EntityNotFoundException;
import com.WebApplcation.MyDietPlan.Exception.InputErrorException;
import com.WebApplcation.MyDietPlan.Exception.SystemErrorException;
import com.WebApplcation.MyDietPlan.Model.Ingredient;
import com.WebApplcation.MyDietPlan.Model.Recipe;
import com.WebApplcation.MyDietPlan.Service.AuthenticationService;
import com.WebApplcation.MyDietPlan.Service.WebsiteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminUIController {
    WebsiteService websiteService;
    AuthenticationService authenticationService;
    @Autowired
    public AdminUIController(WebsiteService websiteService, AuthenticationService authenticationService){
        this.websiteService = websiteService;
        this.authenticationService = authenticationService;
    }
    @GetMapping("/admin")
    public String adminPageForm(Model model){
        if(!isAdminLoggedIn()){
            return "redirect:/";
        }
        List<Ingredient> ingredients = websiteService.getAllIngredients();
        Recipe recipe = new Recipe();

        model.addAttribute("ingredients", ingredients);
        model.addAttribute("recipe", recipe);

        return "adminPage";
    }

    @PostMapping("/admin")
    public String recipePost(@ModelAttribute Recipe recipe, @RequestParam List<Integer> ingredientIds, @RequestParam List<Integer> weights, @RequestParam MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        try {
            websiteService.setupRecipeWithIngredients(recipe, ingredientIds, weights);
            websiteService.setupRecipeWithImage(recipe,file);
            websiteService.createRecipe(recipe);
            redirectAttributes.addFlashAttribute("successMessage", "Opskrift gemt!");
            return "redirect:/admin";
        } catch (InputErrorException | SystemErrorException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin";
    }

    @GetMapping("/opretIngrediens")
    public String addIngredientForm(Model model){
        if(!isAdminLoggedIn()){
            return "redirect:/";
        }
        model.addAttribute("ingredient", new Ingredient());
        return "addIngredient";
    }
    @PostMapping("/opretIngrediens")
    public String addIngredientPost(@ModelAttribute Ingredient ingredient, Model model, RedirectAttributes redirectAttributes){
        try{
            websiteService.createIngredient(ingredient);
            redirectAttributes.addFlashAttribute("createIngredientSuccess", "Ingrediens oprettet!");
            return "redirect:/opretIngrediens";
        } catch (InputErrorException | SystemErrorException e){
            model.addAttribute("createIngredientError", "Udfyld venligst alle felterne korrekt");
        }
        return "addIngredient";
    }

    @GetMapping("/recipeShowcase")
    public String recipeShowcase(Model model){
        if(!isAdminLoggedIn()){
            return "redirect:/";
        }
        try{
            ArrayList<Recipe> allRecipes = websiteService.getAllDeactivatedRecipes();
            model.addAttribute("recipes", allRecipes);
        } catch (EntityNotFoundException e){
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "oldRecipesShowcase";
    }

    @GetMapping("/opdaterOpskrift/{recipeID}")
    public String editRecipe(Model model, @PathVariable int recipeID, RedirectAttributes redirectAttributes){
        if(!isAdminLoggedIn()){
            return "redirect:/";
        }
        try{
            List<Ingredient> allIngredients = websiteService.getAllIngredients();
            Recipe recipe = websiteService.getRecipeById(recipeID);

            // ObjectMapper can convert java objects into JSON or vice versa
            ObjectMapper objectMapper = new ObjectMapper();
            String ingredientsJson = objectMapper.writeValueAsString(recipe.getIngredientList());

            model.addAttribute("ingredientsJson", ingredientsJson);

            model.addAttribute("recipe", recipe);
            model.addAttribute("allIngredients", allIngredients);


            return "editRecipe";
        } catch(EntityNotFoundException | SystemErrorException e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/recipeShowcase";
    }

    @PostMapping("/opdaterOpskrift")
    public String editRecipePost(@ModelAttribute("ingredientListJson") String ingredientListJson, @ModelAttribute Recipe recipe, RedirectAttributes redirectAttributes){
        try {
            // ObjectMapper can convert java objects into JSON or vice versa
            ObjectMapper objectMapper = new ObjectMapper();

            //Convert the JSON string into a List<Ingredient>. TypeReference is used to give information to the objectmapper about what
            // type it needs to convert the JSON into.
            List<Ingredient> ingredients = objectMapper.readValue(ingredientListJson, new TypeReference<>() {
            });

            //Convert to arraylist and add it to the recipe that is being updated.
            ArrayList<Ingredient> ingredients1 = (ArrayList<Ingredient>) ingredients;
            recipe.setIngredientList(ingredients1);

            //Calculate the new total macros for the recipe.
            recipe.calculateAndSetMacros();

            //Update the recipe.
            websiteService.updateRecipe(recipe);
            redirectAttributes.addFlashAttribute("successMessage", "Opskrift gemt!");
            return "redirect:/recipeShowcase";

        } catch (InputErrorException | SystemErrorException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/recipeShowcase";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Der er sket en fejl. Prøv igen senere");
            return "redirect:/recipeShowcase";
        }
    }
    @GetMapping("/sletOpskrift/{recipeID}")
    public String deleteRecipe(@PathVariable int recipeID, RedirectAttributes redirectAttributes){
        if(!isAdminLoggedIn()){
            return "redirect:/";
        }
        try{
            websiteService.deleteRecipe(recipeID);
            redirectAttributes.addFlashAttribute("successMessage", "Opskrift slettet!");
        } catch (SystemErrorException e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/recipeShowcase";

    }

    @GetMapping("/aktiverOpskrift/{recipeID}")
    public String activateRecipe(@PathVariable int recipeID, RedirectAttributes redirectAttributes){
        if(!isAdminLoggedIn()){
            return "redirect:/";
        }
        try{
            if(websiteService.updateRecipeActiveStatus(recipeID)){
                redirectAttributes.addFlashAttribute("successMessage", "Opskrift status ændret!");
            }
        } catch (EntityNotFoundException | SystemErrorException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/recipeShowcase";
    }

    @GetMapping("/aktiveOpskrifter")
    public String showActiveRecipes(Model model, RedirectAttributes redirectAttributes){
        if(!isAdminLoggedIn()){
            return "redirect:/";
        }
       try{
           ArrayList<Recipe> recipes = websiteService.getAllActiveRecipes();
           model.addAttribute("recipes", recipes);
           return "activeRecipesShowcase";
       }catch (EntityNotFoundException e){
           redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
       }
       return "redirect:/aktiveOpskrifter";
    }





    private boolean isAdminLoggedIn(){
        return authenticationService.isAdminLoggedIn();
    }



}
