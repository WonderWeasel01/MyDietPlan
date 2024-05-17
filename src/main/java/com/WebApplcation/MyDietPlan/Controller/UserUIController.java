package com.WebApplcation.MyDietPlan.Controller;

import com.WebApplcation.MyDietPlan.Model.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.WebApplcation.MyDietPlan.Exception.EntityNotFoundException;
import com.WebApplcation.MyDietPlan.Exception.InputErrorException;
import com.WebApplcation.MyDietPlan.Exception.SystemErrorException;
import com.WebApplcation.MyDietPlan.Model.User;
import com.WebApplcation.MyDietPlan.Service.AuthenticationService;
import com.WebApplcation.MyDietPlan.Service.WebsiteService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;


@Controller
public class UserUIController {
    
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private final WebsiteService websiteService;


    public UserUIController(AuthenticationService authenticationService, WebsiteService websiteService){
        this.authenticationService = authenticationService;
        this.websiteService = websiteService;
    }


    @GetMapping("/")
    public String index() {
        return "Index";
    }

    @GetMapping("/paymentSite")
    public String paymentSite() {
        return "paymentSite";
    }


    @GetMapping("/seOpskrift/{recipeID}")
    public String showRecipe(Model model, @PathVariable int recipeID, RedirectAttributes redirectAttributes){
        try{
            Recipe recipe = websiteService.getRecipeById(recipeID);
            model.addAttribute("recipe", recipe);
            System.out.println(StringUtils.hasText(recipe.getImage().getBase64Image()));
            return "showRecipe";
        } catch (SystemErrorException | EntityNotFoundException e) {
            redirectAttributes.addAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/velkommen";
    }
    @GetMapping("/login")
    public String loginForm(Model model) {
        if (authenticationService.isAdminLoggedIn()){
            return "redirect:/admin";
        } else if (authenticationService.isUserLoggedIn()) {
            return "redirect:/welcome";
        }
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

    /*@GetMapping("/CreatePayment")
    public String CreatePaymentForm(Model model){
        Subscription subscription = new Subscription();
        model.addAttribute("subscription",subscription);
        return "paymentSite";
    }*/


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


    @GetMapping("/velkommen")
    public String welcomePage(Model model){
        if(!isLoggedIn()){
            return "redirect:/";
        }
        model.addAttribute("user", AuthenticationService.user);
        try{

            ArrayList<Recipe> weeklyRecipes = websiteService.getAllActiveRecipes();
            ArrayList<Recipe> adjustedRecipes = websiteService.adjustRecipesToUser(AuthenticationService.user.getDailyCalorieBurn(),weeklyRecipes);
            model.addAttribute("weeklyRecipes", adjustedRecipes);

        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "loggedIn";

    }


    @GetMapping("/logout")
    public String logoutButton(){
        authenticationService.logout();
        return "redirect:/";
    }

    @GetMapping("/opdaterBruger/{userID}")
    public String editUser(@PathVariable int userID, Model model) {
        User user = authenticationService.getUserByID(userID);
        model.addAttribute("user", user);
        return "editUser";
    }

    @PostMapping("/opdaterBruger")
    public String updateUser(@ModelAttribute User updatedUser, Model model) {
        try {
            User updatedUserInfo = authenticationService.updateUser(updatedUser.getUserId(), updatedUser);
            model.addAttribute("user", updatedUserInfo);
            return "redirect:/";
        } catch (InputErrorException e) {
            model.addAttribute("Udfyld venligst alle felter", e.getMessage());
            return "editUser"; // Return to the edit page to display the error
        }
    }



    /*
    @GetMapping("/paymentSite")
    public String paymentSitePage(Model model){
        if(!isLoggedIn()){
            return "redirect:/";
        }
        return "paymentSite";

    }
*/


    private String determineViewDependingOnRole(User user) {
        if ("Admin".equals(user.getRole())) {
            return "redirect:/admin";
        }
        return "redirect:/velkommen";
    }



    private boolean isLoggedIn(){
       return authenticationService.isUserLoggedIn();
    }


}
