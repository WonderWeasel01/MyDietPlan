package com.WebApplcation.MyDietPlan.Controller;

import com.WebApplcation.MyDietPlan.Exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.WebApplcation.MyDietPlan.Exception.InputErrorException;
import com.WebApplcation.MyDietPlan.Exception.SystemErrorException;
import com.WebApplcation.MyDietPlan.Model.User;
import com.WebApplcation.MyDietPlan.Service.AuthenticationService;

import java.util.Objects;


@Controller
public class LoginController {

    @Autowired
    private AuthenticationService as;

    @GetMapping("/login")
    public String loginForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user, Model model) {
        try{
            //Check if the user credentials are valid
            if (!as.validateLogin(user.getEmail(), user.getPassword())) {
                model.addAttribute("loginError", "Email eller password forkert");
                return "login";
            }

            //Log the user in;
            User validatedUser = as.loginUser(user.getEmail());
            return determineViewDependingOnRole(validatedUser);
        } catch (EntityNotFoundException e){
            model.addAttribute("loginError" , "Email eller password forkert");
            return "login";
        }
    }


    private String determineViewDependingOnRole(User user) {
        if ("Admin".equals(user.getRole())) {
            return "redirect:/admin";
        }
        return "redirect:/";
    }
}
