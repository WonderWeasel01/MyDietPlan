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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class AdminUIController {
    @Autowired
    WebsiteService ws;
    public AdminUIController(WebsiteService websiteService){
        this.ws = websiteService;
    }
    @GetMapping("/admin")
    public String adminPageForm(Model model){
        if(!isAdminLoggedIn()){
            return "redirect:/";
        }
        List<Ingredient> ingredients = ws.getAllIngredients();
        Recipe recipe = new Recipe();

        model.addAttribute("ingredients", ingredients);
        model.addAttribute("recipe", recipe);

        return "adminPage";
    }

    @PostMapping("/admin")
    public String recipePost(@ModelAttribute Recipe recipe, RedirectAttributes redirectAttribute){
        try {
            ws.createRecipe(recipe);
            redirectAttribute.addFlashAttribute("succesMessage", "Opskrift gemt succesfuldt!");
            return "redirect:/admin";
        } catch (InputErrorException e){
            redirectAttribute.addFlashAttribute("inputErrorMessage", "Udfyld venligst alle felterne korrekt!");
        } catch (SystemErrorException e){
            redirectAttribute.addFlashAttribute("errorMessage", "Der er sket en fejl på vores side. Prøv igen senere!");
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
            ws.createIngredient(ingredient);
            redirectAttributes.addFlashAttribute("createIngredientSuccess", "Ingrediens oprettet!");
            return "redirect:/opretIngrediens";
        } catch (InputErrorException e){
            model.addAttribute("createIngredientError", "Udfyld venligst alle felterne korrekt");
        } catch (SystemErrorException e) {
            model.addAttribute("createIngredientError", "Der er sket en fejl på vores side. Prøv igen senere!");
        }
        return "addIngredient";

    }




    private boolean isAdminLoggedIn(){
        return AuthenticationService.user != null && Objects.equals(AuthenticationService.user.getRole(), "Admin");
    }

}
