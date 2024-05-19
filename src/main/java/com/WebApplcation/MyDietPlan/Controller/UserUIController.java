package com.WebApplcation.MyDietPlan.Controller;

import com.WebApplcation.MyDietPlan.Model.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
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
            Recipe recipe = websiteService.getAdjustedRecipeById(recipeID);
            model.addAttribute("recipe", recipe);
            return "showRecipe";
        } catch (SystemErrorException e) {
            redirectAttributes.addAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/velkommen";
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
    public String createUser( @ModelAttribute User user, Model model){
        try{
            authenticationService.createUser(user);
            return "redirect:/";
        } catch (SystemErrorException | InputErrorException e) {
            model.addAttribute("createUserError", e.getMessage());
        }
        return "createUser";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        //Grants instant access if the user is already logged in
        if (authenticationService.isAdminLoggedIn()){
            return "redirect:/admin";
        } else if (authenticationService.isUserLoggedIn()) {
            return "redirect:/velkommen";
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String email, @RequestParam String password, Model model) {
        try{
            User validatedUser = authenticationService.loginUser(email, password);
            return determineViewDependingOnRole(validatedUser);
        } catch (InputErrorException e){
            model.addAttribute("loginError" , e.getMessage());
            return "login";
        }
    }


    @GetMapping("/velkommen")
    public String welcomePage(Model model){
        if(!isLoggedIn()){
            return "redirect:/";
        }
        User user = authenticationService.getUser();
        model.addAttribute("user", user);
        try{
            ArrayList<Recipe> weeklyRecipes = websiteService.getAllActiveRecipes();
            ArrayList<Recipe> adjustedRecipes = websiteService.adjustRecipesToUser(user.getDailyCalorieGoal(),weeklyRecipes);
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
        if(!isLoggedIn()){
            return "redirect:/";
        }
        User user = websiteService.getUserByID(userID);
        model.addAttribute("user", user);
        return "editUser";
    }

    @PostMapping("/opdaterBruger")
    public String updateUser(@ModelAttribute User updatedUser, Model model) {
        try {
            websiteService.updateUser(updatedUser);
            return "redirect:/velkommen";
        } catch (InputErrorException | SystemErrorException e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", e.getMessage());
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
