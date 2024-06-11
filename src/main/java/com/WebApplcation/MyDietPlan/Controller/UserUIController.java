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
import java.util.List;


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
        if(!isLoggedInAndHasSub()){
            return "redirect:/";
        }
        
        try{
            User user = authenticationService.getUser();
            Recipe recipe = websiteService.getUsersAdjustedRecipeById(user,recipeID);

            model.addAttribute("recipe", recipe);
            model.addAttribute("user", user);
            return "showRecipe";
        } catch (EntityNotFoundException e) {
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
    public String createUser(@ModelAttribute User user, RedirectAttributes redirectAttributes){
        try{
            websiteService.setupAndSaveUser(user);
            return "redirect:/";
        } catch (SystemErrorException | InputErrorException e) {
           redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/opretBruger";
        }
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

        User user = authenticationService.getUser();
        //If the user already has adjustedRecipes attached, use them to save time.
        if(user.getAdjustedRecipes() != null) {
            model.addAttribute("weeklyRecipes", user.getAdjustedRecipes());
        } else try {
            //Adjust and set weekly recipes on user
            ArrayList<Recipe> adjustedRecipes = websiteService.adjustAndSetWeeklyRecipes(user);

            //Create and set the Base64 string on the recipes
            websiteService.CreateAndSetBase64String(adjustedRecipes);

            model.addAttribute("weeklyRecipes", adjustedRecipes);

        }catch (SystemErrorException | EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }

        model.addAttribute("user", user);
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

        model.addAttribute("user", user);
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
        Subscription subscription = websiteService.getSubscriptionByUserID(user.getUserId());
        int subDaysLeft = websiteService.daysLeftOnSubscription(user.getUserId());

        model.addAttribute("user",user);
        model.addAttribute("subDaysLeft", subDaysLeft);
        model.addAttribute("subscriptionStatus", subscription.getActiveSubscription());

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
       return "redirect:/minProfil";
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
        return "redirect:/minProfil";
    }

    @GetMapping("/brugerTilføjFavoritOpskrifter/{recipeID}")
    public String addFavoriteRecipe (@PathVariable int recipeID, RedirectAttributes redirectAttributes) {
        try {
            websiteService.addFavoriteRecipe(recipeID);
        } catch (SystemErrorException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } return "redirect:/seOpskrift/" + recipeID;
    }

    @GetMapping("/brugerFavoritOpskrifter")
    public String getFavoriteRecipes (RedirectAttributes redirectAttributes, Model model) {
        try {
            User user = authenticationService.getUser();
            List<Recipe> favoriteRecipes = websiteService.getFavoriteRecipesByUserID(user.getUserId());
            model.addAttribute("favoriteRecipes", favoriteRecipes);
            model.addAttribute("user", user);
            return "userFavoriteRecipes";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/minProfil";


    }






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
