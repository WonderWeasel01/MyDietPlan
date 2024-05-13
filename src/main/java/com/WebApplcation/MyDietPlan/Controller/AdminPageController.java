package com.WebApplcation.MyDietPlan.Controller;

import com.WebApplcation.MyDietPlan.Exception.InputErrorException;
import com.WebApplcation.MyDietPlan.Exception.SystemErrorException;
import com.WebApplcation.MyDietPlan.Model.Ingredient;
import com.WebApplcation.MyDietPlan.Model.Recipe;
import com.WebApplcation.MyDietPlan.Repository.RecipeRepository;
import com.WebApplcation.MyDietPlan.Repository.UserRepository;
import com.WebApplcation.MyDietPlan.Service.AdminService;
import com.WebApplcation.MyDietPlan.Service.AuthenticationService;
import com.WebApplcation.MyDietPlan.Service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;

@Controller
public class AdminPageController {
    @Autowired
    private final AdminService as;
    @Autowired
    private final RecipeService rs;

    public AdminPageController(AdminService adminService, RecipeService recipeService){
        this.as = adminService;
        this.rs = recipeService;
    }

    @GetMapping("/admin")
    public String adminPageForm(Model model){
        if(AuthenticationService.user == null ||!Objects.equals(AuthenticationService.user.getRole(), "Admin")){
            return "redirect:/";
        }
        List<Ingredient> ingredients = rs.getAllIngredients();
        Recipe recipe = new Recipe();

        model.addAttribute("ingredients", ingredients);
        model.addAttribute("recipe", recipe);

        return "adminPage";
    }

    @PostMapping("/admin")
    public String recipePost(@ModelAttribute Recipe recipe, RedirectAttributes redirectAttribute){
        try {
            rs.createRecipe(recipe);
            redirectAttribute.addFlashAttribute("succesMessage", "Opskrift gemt succesfuldt!");
            return "redirect:/admin";
        } catch (InputErrorException e){
            redirectAttribute.addFlashAttribute("inputErrorMessage", "Udfyld venligst alle felterne korrekt!");
        } catch (SystemErrorException e){
            redirectAttribute.addFlashAttribute("errorMessage", "Der er sket en fejl på vores side. Prøv igen senere!");
        }
        return "adminPage";
    }




}
