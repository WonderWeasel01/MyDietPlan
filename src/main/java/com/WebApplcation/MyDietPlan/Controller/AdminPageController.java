package com.WebApplcation.MyDietPlan.Controller;

import com.WebApplcation.MyDietPlan.Repository.RecipeRepository;
import com.WebApplcation.MyDietPlan.Service.AdminService;
import com.WebApplcation.MyDietPlan.Service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Objects;

@Controller
public class AdminPageController {
    @Autowired
    private final AdminService as;
    @Autowired
    private final RecipeRepository rr;

    public AdminPageController(RecipeRepository recipeRepository, AdminService adminService){
        this.rr = recipeRepository;
        this.as = adminService;
    }

    @GetMapping("/admin")
    public String adminPageForm(){
        if(!Objects.equals(AuthenticationService.user.getRole(), "Admin")){
            return "redirect:/";
        }
        return "adminPage";
    }




}
