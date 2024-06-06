package com.WebApplcation.MyDietPlan.Controller;

import com.WebApplcation.MyDietPlan.Entity.Recipe;

import com.WebApplcation.MyDietPlan.Entity.Subscription;
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
    public String paymentSite(Model model) {
        model.addAttribute("user", authenticationService.getUser());
        return "paymentSite";
    }

    @GetMapping("/ingenAbonnement")
    public String noSubscriptionAlert(Model model) {
        model.addAttribute("user", authenticationService.getUser());
        return "noSubscription";
    }


    @GetMapping("/seOpskrift/{recipeID}")
    public String showRecipe(Model model, @PathVariable int recipeID, RedirectAttributes redirectAttributes){
        try{
            Recipe recipe = websiteService.getUsersAdjustedRecipeById(recipeID);
            model.addAttribute("recipe", recipe);
            model.addAttribute("user", authenticationService.getUser());
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
    public String createUser( @ModelAttribute User user, RedirectAttributes redirectAttributes){
        try{
            websiteService.setupDailyCalorieGoal(user);
            websiteService.createUser(user);
            return "redirect:/";
        } catch (SystemErrorException | InputErrorException e) {
           redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/opretBruger";
    }

    @GetMapping("/CreatePayment")
    public String CreatePaymentForm(Model model){
        return "paymentSite";
    }


    @GetMapping("/login")
    public String loginForm(Model model) {
        // Grants instant access if the user is already logged in
        if (authenticationService.isAdminLoggedIn()){
            return "redirect:/admin";
        }
        try{
            if(authenticationService.isUserLoggedIn()){
                return handleUserLoginView();
            }
        }catch (SystemErrorException | EntityNotFoundException e ){
            model.addAttribute("errorMessage", e.getMessage());
        }

        return "login";
    }



    @PostMapping("/login")
    public String loginUser(@RequestParam String email, @RequestParam String password, Model model) {
        try{
            User validatedUser = authenticationService.loginUserAndSetSession(email, password);
            return determineViewDependingOnRole(validatedUser);
        } catch (InputErrorException | SystemErrorException | EntityNotFoundException e){
            model.addAttribute("loginError" , e.getMessage());
            return "login";
        }
    }


    @PostMapping("/payingUser")
    public String handlePayingUser(RedirectAttributes redirectAttributes) {
        try{
            websiteService.paySubscription();
        } catch (EntityNotFoundException | SystemErrorException e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/CreatePayment";
        }
        return "redirect:/velkommen";
    }


    @GetMapping("/velkommen")
    public String welcomePage(Model model){
        if(!isLoggedInAndHasSub()){
            return "redirect:/";
        }
        try{
            User user = authenticationService.getUser();
            //If the user already has adjustedRecipes attached, use them to save time.
            if(user.getAdjustedRecipes() != null){
                model.addAttribute("weeklyRecipes", user.getAdjustedRecipes());
            } else {
                ArrayList<Recipe> adjustedRecipes = websiteService.adjustAndSetWeeklyRecipesToLoggedInUser();
                websiteService.setBase64Image(adjustedRecipes);
                model.addAttribute("weeklyRecipes", adjustedRecipes);
            }
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

    @GetMapping("/opdaterBruger")
    public String editUser(Model model) {
        if(!isLoggedInAndHasSub()){
            return "redirect:/";
        }
        User user = authenticationService.getUser();
        Subscription subscription = websiteService.getSubscriptionByUserID(user.getUserId());

        model.addAttribute("user", user);
        model.addAttribute("subscriptionStatus", subscription.getActiveSubscription());
        return "editUser";
    }

    @PostMapping("/opdaterBruger")
    public String updateUser(@ModelAttribute User updatedUser, RedirectAttributes redirectAttributes) {
        if(!isLoggedInAndHasSub()){
            return "redirect:/";
        }

        try {
            websiteService.handleUserSelfUpdate(updatedUser);
            redirectAttributes.addFlashAttribute("successMessage", "Oplysninger opdateret!");
        } catch (EntityNotFoundException | InputErrorException | SystemErrorException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/opdaterBruger";
    }

    @GetMapping("/minProfil")
    public String showUserProfile(Model model){
        if(!isLoggedInAndHasSub()){
            return "redirect:/";
        }

        User user = authenticationService.getUser();
        int subDaysLeft = websiteService.daysLeftOnSubscription(user.getUserId());

        model.addAttribute("user",user);
        model.addAttribute("subDaysLeft", subDaysLeft);


        return "userProfile";
    }


   @GetMapping("/afmeldAbonnement/{userID}")
    public String cancelSubscription(@PathVariable int userID, RedirectAttributes redirectAttributes){
       if(!isLoggedInAndHasSub()){
           return "redirect:/";
       }
       try{
           websiteService.cancelUserSubscription(userID);
           redirectAttributes.addFlashAttribute("successMessage","Abonnement afmeldt");
       } catch (EntityNotFoundException | SystemErrorException e){
           redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
       }
       return "redirect:/opdaterBruger";
    }
    @GetMapping("/genoptagAbonnement/{userID}")
    public String renewSubscription(@PathVariable int userID, RedirectAttributes redirectAttributes){
        if(!isLoggedInAndHasSub()){
            return "redirect:/";
        }
        try{
            websiteService.activateUserSubscription(userID);
            redirectAttributes.addFlashAttribute("successMessage","Abonnement genoptaget");
        } catch (EntityNotFoundException | SystemErrorException e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/opdaterBruger";
    }

    @GetMapping("/brugerFavoritOpskrifter")



    private String determineViewDependingOnRole(User user) throws SystemErrorException, EntityNotFoundException {
        if ("Admin".equals(user.getRole())) {
            return "redirect:/admin";
        } else return handleUserLoginView();
    }

    private String handleUserLoginView() throws SystemErrorException, EntityNotFoundException {
        if (authenticationService.isPayingUser()) {
            return "redirect:/velkommen";
        } else return "redirect:/ingenAbonnement";
    }

    private boolean isLoggedInAndHasSub() {
        try{
            return authenticationService.isUserLoggedIn() && authenticationService.isPayingUser();
        } catch (SystemErrorException | EntityNotFoundException e){
            return false;
        }

    }



}
