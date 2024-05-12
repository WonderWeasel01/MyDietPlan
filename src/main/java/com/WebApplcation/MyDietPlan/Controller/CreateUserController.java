package com.WebApplcation.MyDietPlan.Controller;

import com.WebApplcation.MyDietPlan.Exception.InputErrorException;
import com.WebApplcation.MyDietPlan.Exception.SystemErrorException;
import com.WebApplcation.MyDietPlan.Model.User;
import com.WebApplcation.MyDietPlan.Service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CreateUserController {

    @Autowired
    private AuthenticationService as;

    public CreateUserController(AuthenticationService authenticationService){
        this.as = authenticationService;
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
            as.createUser(user);
            return "redirect:/";
        } catch (DuplicateKeyException e) {
            redirectAttributes.addFlashAttribute("inputError", "Email bruges allerede!");
        } catch (SystemErrorException e) {
            redirectAttributes.addFlashAttribute("systemErrorMessage", "Der er sket en fejl på vores side. Prøv igen senere!");
        } catch (InputErrorException e) {
            redirectAttributes.addFlashAttribute("MissingInputErrorMessage", "Udfyld venligst alle felterne!");
        }
        return "createUser";
    }
}
