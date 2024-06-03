package com.WebApplcation.MyDietPlan.Exception;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentNotValidException.class})
    public String handleMissingServletRequestParameterException(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        // Capture the original URL
        String referer = request.getHeader("Referer");


        // Add the flash attribute
        redirectAttributes.addFlashAttribute("errorMessage", "Udfyld venligst alle felter korrekt.");
        // Redirect back to the original URL
        return "redirect:" + (referer);
    }


}
