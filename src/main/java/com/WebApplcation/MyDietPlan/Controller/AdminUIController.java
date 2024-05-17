package com.WebApplcation.MyDietPlan.Controller;

import com.WebApplcation.MyDietPlan.Exception.EntityNotFoundException;
import com.WebApplcation.MyDietPlan.Exception.InputErrorException;
import com.WebApplcation.MyDietPlan.Exception.SystemErrorException;
import com.WebApplcation.MyDietPlan.Model.Image;
import com.WebApplcation.MyDietPlan.Model.Ingredient;
import com.WebApplcation.MyDietPlan.Model.Recipe;
import com.WebApplcation.MyDietPlan.Service.AuthenticationService;
import com.WebApplcation.MyDietPlan.Service.WebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminUIController {
    @Autowired
    WebsiteService websiteService;
    @Autowired
    AuthenticationService authenticationService;
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
            recipe = websiteService.setupRecipeWithIngredients(recipe, ingredientIds, weights);
            Image image = websiteService.convertFileToImage(file);
            recipe.setImage(image);
            websiteService.createRecipe(recipe);
            redirectAttributes.addFlashAttribute("successMessage", "Opskrift gemt!");
            return "redirect:/admin";
        } catch (InputErrorException | SystemErrorException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Der skete en fejl under forsøget på at gemme billedet");
        }
        return "adminPage";

    }

    @GetMapping("/opretIngrediens")
    public String addIngredientForm(Model model){
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
        try{
            List<Ingredient> allIngredients = websiteService.getAllIngredients();
            Recipe recipe = websiteService.getRecipeById(recipeID);

            model.addAttribute("recipe", recipe);
            model.addAttribute("allIngredients", allIngredients);


            return "editRecipe";
        } catch(EntityNotFoundException | SystemErrorException e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/recipeShowcase";
    }

    @PostMapping("/opdaterOpskrift")
    public String editRecipePost(@ModelAttribute Recipe recipe, @ModelAttribute("recipeIngredients") ArrayList<Ingredient> recipeIngredients,
                                 @RequestParam List<Integer> ingredientIds, @RequestParam List<Integer> weights, RedirectAttributes redirectAttributes){
        try {
            websiteService.setupRecipeWithIngredients(recipe,ingredientIds,weights);
            websiteService.updateRecipe(recipe);
            redirectAttributes.addFlashAttribute("successMessage", "Opskrift gemt!");
            return "redirect:/recipeShowcase";
        } catch (InputErrorException | SystemErrorException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/recipeShowcase";
        }
    }
    @GetMapping("/sletOpskrift/{recipeID}")
    public String deleteRecipe(@PathVariable int recipeID){
        websiteService.deleteRecipe(recipeID);
        return "redirect:/recipeShowcase";
    }

    @GetMapping("/aktiverOpskrift/{recipeID}")
    public String activateRecipe(@PathVariable int recipeID, RedirectAttributes redirectAttributes){
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
