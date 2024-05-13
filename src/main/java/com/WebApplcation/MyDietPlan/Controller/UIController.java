package com.WebApplcation.MyDietPlan.Controller;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.WebApplcation.MyDietPlan.Exception.EntityNotFoundException;
import com.WebApplcation.MyDietPlan.Exception.InputErrorException;
import com.WebApplcation.MyDietPlan.Exception.SystemErrorException;
import com.WebApplcation.MyDietPlan.Model.Ingredient;
import com.WebApplcation.MyDietPlan.Model.Recipe;
import com.WebApplcation.MyDietPlan.Model.User;
import com.WebApplcation.MyDietPlan.Service.AuthenticationService;
import com.WebApplcation.MyDietPlan.Service.WebsiteService;

@Controller
public class UIController {
    
    @Autowired
    private AuthenticationService authenticationService;
    private final WebsiteService ws;


    public UIController(AuthenticationService authenticationService, WebsiteService websiteService){
        this.authenticationService = authenticationService;
        this.ws = websiteService;
    }

    @GetMapping("/")
    public String index() {
        return "Index";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "login";
    }

    @GetMapping("/opretBruger")
    public String createUserForm(Model model){
        User user = new User();
        model.addAttribute("user",user);
        return "createUser";
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


    @PostMapping("/opretBruger")
    public String createUser(@ModelAttribute User user, Model model){
        try{
            authenticationService.createUser(user);
            return "redirect:/";
        } catch (DuplicateKeyException e) {
            model.addAttribute("createUserError", "Email bruges allerede!");
        } catch (SystemErrorException e) {
            model.addAttribute("createUserError", "Der er sket en fejl på vores side. Prøv igen senere!");
        } catch (InputErrorException e) {
            model.addAttribute("createUserError", "Udfyld venligst alle felterne korrekt!");
        }
        return "createUser";
    }


    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user, Model model) {
        try{
            //Check if the user credentials are valid
            if (!authenticationService.validateLogin(user.getEmail(), user.getPassword())) {
                model.addAttribute("loginError", "Email eller password forkert");
                return "login";
            }

            //Log the user in;
            User validatedUser = authenticationService.loginUser(user.getEmail());
            return determineViewDependingOnRole(validatedUser);
        } catch (EntityNotFoundException e){
            model.addAttribute("loginError" , "Email eller password forkert");
            return "login";
        }
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

    @GetMapping("/welcome")
    public String welcomePage(Model model){
        if(!isLoggedIn()){
            return "redirect:/";
        }
        return "loggedIn";

    }

    private String determineViewDependingOnRole(User user) {
        if ("Admin".equals(user.getRole())) {
            return "redirect:/admin";
        }
        return "redirect:/welcome";
    }

    private boolean isLoggedIn(){
       return AuthenticationService.user != null;
    }

    private boolean isAdminLoggedIn(){
        return AuthenticationService.user != null && Objects.equals(AuthenticationService.user.getRole(), "Admin");
    }
}
