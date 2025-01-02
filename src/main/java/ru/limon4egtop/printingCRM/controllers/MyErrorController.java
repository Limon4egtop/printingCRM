package ru.limon4egtop.printingCRM.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/error")
public class MyErrorController implements ErrorController {

    @RequestMapping("/customError")
    public String handleError(HttpServletRequest request,
                              Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error/error-404";
            }
            else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "error/error-500";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "error/error-403";
            }
        }
        model.addAttribute("errorCode", status);
        return "error/error";
    }

    @GetMapping("/errorClientNameNull")
    public String errorOrder() {
        return "error/error-clientNameNull";
    }
}
