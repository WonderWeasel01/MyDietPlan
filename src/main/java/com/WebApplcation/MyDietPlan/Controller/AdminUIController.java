package com.WebApplcation.MyDietPlan.Controller;

import com.WebApplcation.MyDietPlan.Entity.Subscription;
import com.WebApplcation.MyDietPlan.Entity.User;
import com.WebApplcation.MyDietPlan.Exception.EntityNotFoundException;
import com.WebApplcation.MyDietPlan.Exception.InputErrorException;
import com.WebApplcation.MyDietPlan.Exception.SystemErrorException;
import com.WebApplcation.MyDietPlan.Entity.Ingredient;
import com.WebApplcation.MyDietPlan.Entity.Recipe;
import com.WebApplcation.MyDietPlan.UseCase.AuthenticationService;
import com.WebApplcation.MyDietPlan.UseCase.WebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        model.addAttribute("user", authenticationService.getUser());
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
        model.addAttribute("user", authenticationService.getUser());
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
        model.addAttribute("user", authenticationService.getUser());
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
        model.addAttribute("user", authenticationService.getUser());
        if(!isAdminLoggedIn()){
            return "redirect:/";
        }
       try{
           ArrayList<Recipe> recipes = websiteService.getAllActiveRecipes();
           model.addAttribute("recipes", recipes);
           return "activeRecipesShowcase";
       }catch (EntityNotFoundException e){
           redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
       }
       return "redirect:/aktiveOpskrifter";
    }

   @GetMapping("/findBrugerAdmin")
   public String adminEditUser(@RequestParam(required = false) String searchQuery, Model model, RedirectAttributes redirectAttributes) {
        try {
            List<User> allUsers = websiteService.getAllUsers((searchQuery));
            List<User> filteredUsers;

            //Filter the Users
            if (searchQuery != null && !searchQuery.isEmpty()) {
                filteredUsers = allUsers.stream()
                        .filter(user -> user.getFirstName().toLowerCase().contains(searchQuery.toLowerCase()) ||
                                user.getLastName().toLowerCase().contains(searchQuery.toLowerCase()))
                        .collect(Collectors.toList());
            } else {
                filteredUsers = allUsers;
            }

            //Add to the model
            model.addAttribute("users", filteredUsers);
            return "adminFindUser";

        } catch (SystemErrorException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "adminPage";
    }

    @GetMapping("/opdaterBrugerAdmin/{userID}")
    public String adminEditUserForm(@PathVariable int userID, Model model, RedirectAttributes redirectAttributes){
        try{
            model.addAttribute("user", websiteService.getUserByID(userID));
            Subscription subscription = websiteService.getSubscriptionByUserID(userID);

            if(subscription != null){
                System.out.println(subscription);
                model.addAttribute("subscription", subscription);
            }

            return "adminEditUser";
        } catch (SystemErrorException | EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/findBrugerAdmin";
        }
    }


    @PostMapping("/opdaterBrugerOplysningerAdmin")
    public String editUserPost(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            if(websiteService.handleAdminUserInfoUpdate(user)){
                redirectAttributes.addFlashAttribute("successMessage", "Brugeren blev opdateret!");
            }
        } catch (InputErrorException | SystemErrorException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/findBrugerAdmin";
    }

    @PostMapping("/opdaterBrugerAbonnementAdmin")
    public String editUserSubscription(@ModelAttribute Subscription subscription, RedirectAttributes redirectAttributes){
        try{
            System.out.println(subscription);
            if(websiteService.handleAdminUserSubscriptionUpdate(subscription)){
                redirectAttributes.addFlashAttribute("successMessage", "Brugerens abonnement blev opdateret!");
            }
        } catch (SystemErrorException e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/findBrugerAdmin";
    }


    private boolean isAdminLoggedIn(){
        return authenticationService.isAdminLoggedIn();
    }



}
