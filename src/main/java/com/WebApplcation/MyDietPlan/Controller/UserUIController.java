package com.WebApplcation.MyDietPlan.Controller;

import com.WebApplcation.MyDietPlan.Entity.Recipe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.WebApplcation.MyDietPlan.Exception.EntityNotFoundException;
import com.WebApplcation.MyDietPlan.Exception.InputErrorException;
import com.WebApplcation.MyDietPlan.Exception.SystemErrorException;
import com.WebApplcation.MyDietPlan.Entity.User;
import com.WebApplcation.MyDietPlan.UseCase.AuthenticationService;
import com.WebApplcation.MyDietPlan.UseCase.WebsiteService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;


@Controller
public class UserUIController {
    private final AuthenticationService authenticationService;
    private final WebsiteService websiteService;

    @Autowired
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

    @GetMapping("/ingenAbonnement")
    public String notLoggedIn() {
        return "noSubscription";
    }


    @GetMapping("/seOpskrift/{recipeID}")
    public String showRecipe(Model model, @PathVariable int recipeID, RedirectAttributes redirectAttributes){
        try{
            Recipe recipe = websiteService.getUsersAdjustedRecipeById(recipeID);
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

    @PostMapping("/opretBruger")
    public String createUser( @ModelAttribute User user, Model model){
        try{
            authenticationService.createUser(user);
            return "redirect:/";
        } catch (SystemErrorException | InputErrorException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/opretBruger";
    }

    @GetMapping("/CreatePayment")
    public String CreatePaymentForm(Model model){
        return "paymentSite";
    }


    @GetMapping("/login")
    public String loginForm() {
        // Grants instant access if the user is already logged in
        if (authenticationService.isAdminLoggedIn()){
            return "redirect:/admin";
        }

        if(authenticationService.isUserLoggedIn()){
            return handleUserLogin();
        }

        return "login";
    }

    private String handleUserLogin(){
        if (authenticationService.isPayingUser()) {
            return "redirect:/velkommen";
        } else return "redirect:/ingenAbonnement";
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


    @PostMapping("/payingUser")
    public String handlePayingUser() {
        authenticationService.payingUser();
        return "redirect:/velkommen";
    }


    @GetMapping("/velkommen")
    public String welcomePage(Model model){
        if(!isLoggedInAndHasSub()){
            return "redirect:/";
        }
        try{
            ArrayList<Recipe> adjustedRecipes = websiteService.adjustAndSetWeeklyRecipesToLoggedInUser();
            websiteService.setBase64Image(adjustedRecipes);
            model.addAttribute("weeklyRecipes", adjustedRecipes);

            User user = authenticationService.getUser();
            model.addAttribute("user", user);
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
        if(!isLoggedInAndHasSub()){
            return "redirect:/";
        }
        User user = websiteService.getUserByID(userID);
        model.addAttribute("user", user);
        return "editUser";
    }

    @PostMapping("/opdaterBruger")
    public String updateUser(@ModelAttribute User updatedUser, Model model) {
        if(!isLoggedInAndHasSub()){
            return "redirect:/";
        }

        try {
            websiteService.updateUser(updatedUser);
            return "redirect:/velkommen";
        } catch (InputErrorException | SystemErrorException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "editUser"; // Return to the edit page to display the error
        }
    }

    @GetMapping("/minProfil")
    public String showUserProfile(){
        if(!isLoggedInAndHasSub()){
            return "redirect:/";
        }
        return "userProfile";
    }


   @GetMapping("/afmeldAbonnement/{userID}")
    public String cancelSubscription(@PathVariable int userID, RedirectAttributes redirectAttributes){
       if(!isLoggedInAndHasSub()){
           return "redirect:/";
       }
       try{
           authenticationService.cancelUserSubscription(userID);
           redirectAttributes.addFlashAttribute("successMessage","Abonnement afmeldt");
       } catch (EntityNotFoundException | SystemErrorException e){
           redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
       }
       return "redirect:/opdaterBruger/" + userID;
    }





    private String determineViewDependingOnRole(User user) {
        if ("Admin".equals(user.getRole())) {
            return "redirect:/admin";
        }
        if(authenticationService.isPayingUser()){
            return "redirect:/velkommen";
        }
        return "redirect:/ingenAbonnement";
    }

    private boolean isLoggedInAndHasSub(){
       return authenticationService.isUserLoggedIn() && authenticationService.isPayingUser();
    }


}
