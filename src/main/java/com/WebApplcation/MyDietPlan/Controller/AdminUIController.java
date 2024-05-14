package com.WebApplcation.MyDietPlan.Controller;

import com.WebApplcation.MyDietPlan.Exception.InputErrorException;
import com.WebApplcation.MyDietPlan.Exception.SystemErrorException;
import com.WebApplcation.MyDietPlan.Model.Ingredient;
import com.WebApplcation.MyDietPlan.Model.Recipe;
import com.WebApplcation.MyDietPlan.Service.AuthenticationService;
import com.WebApplcation.MyDietPlan.Service.WebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String recipePost(@ModelAttribute Recipe recipe, @RequestParam List<Integer> ingredientIds, @RequestParam List<Integer> weights,
                             RedirectAttributes redirectAttributes) {
        try {
            websiteService.createRecipe(recipe, ingredientIds, weights);
            redirectAttributes.addFlashAttribute("successMessage", "Opskrift gemt!");
            return "redirect:/admin";
        } catch (InputErrorException | SystemErrorException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin";
        }
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


    private boolean isAdminLoggedIn(){
        return authenticationService.isAdminLoggedIn();
    }

    @GetMapping("/logout")
    public String logoutButton(){
        authenticationService.logout();
        return "redirect:/";
    }

}
