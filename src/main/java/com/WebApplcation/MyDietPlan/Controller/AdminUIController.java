package com.WebApplcation.MyDietPlan.Controller;

import com.WebApplcation.MyDietPlan.Exception.EntityNotFoundException;
import com.WebApplcation.MyDietPlan.Exception.InputErrorException;
import com.WebApplcation.MyDietPlan.Exception.SystemErrorException;
import com.WebApplcation.MyDietPlan.Entity.Ingredient;
import com.WebApplcation.MyDietPlan.Entity.Recipe;
import com.WebApplcation.MyDietPlan.UseCase.AuthenticationService;
import com.WebApplcation.MyDietPlan.UseCase.WebsiteService;
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
    private final WebsiteService websiteService;
    private final AuthenticationService authenticationService;
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
    public String recipePost(@ModelAttribute Recipe recipe,
                             @RequestParam List<Integer> ingredientIds,
                             @RequestParam List<Integer> weights,
                             @RequestParam MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        try {
            websiteService.setupRecipeWithIngredients(recipe, ingredientIds, weights);
            websiteService.setupRecipeWithImage(recipe,file);
            websiteService.createRecipe(recipe);
            redirectAttributes.addFlashAttribute("successMessage", "Opskrift gemt!");
        } catch (InputErrorException | SystemErrorException | EntityNotFoundException e) {
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
            model.addAttribute("createIngredientError", e.getMessage());
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

            model.addAttribute("recipe", recipe);
            model.addAttribute("allIngredients", allIngredients);


            return "editRecipe";
        } catch(EntityNotFoundException | SystemErrorException e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/recipeShowcase";
    }

    @PostMapping("/opdaterOpskrift")
    public String editRecipePost(@RequestParam List<Integer> ingredientIds, @RequestParam List<Integer> weights, @ModelAttribute Recipe recipe, RedirectAttributes redirectAttributes){
        try {
            // ObjectMapper can convert java objects into JSON or vice versa

            for(Integer weight : ingredientIds){
                System.out.println(weight);
            }
            websiteService.setupRecipeWithIngredients(recipe,ingredientIds, weights);

            //Update the recipe.
            websiteService.updateRecipe(recipe);
            redirectAttributes.addFlashAttribute("successMessage", "Opskrift gemt!");
            return "redirect:/recipeShowcase";

        } catch (InputErrorException | EntityNotFoundException | SystemErrorException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
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
           websiteService.setBase64Image(recipes);
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
