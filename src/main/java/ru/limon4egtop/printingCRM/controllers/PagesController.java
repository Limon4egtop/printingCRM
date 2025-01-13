package ru.limon4egtop.printingCRM.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.limon4egtop.printingCRM.Services.impl.ClientsServiceImp;
import ru.limon4egtop.printingCRM.Services.impl.EmployeeServiceImp;
import ru.limon4egtop.printingCRM.Services.impl.OrderServiceImp;

@Controller
public class PagesController extends MainController {

    @Autowired
    public PagesController(ClientsServiceImp clientsServiceImp, OrderServiceImp orderServiceImp, EmployeeServiceImp employeeServiceImp) {
        super(clientsServiceImp, orderServiceImp, employeeServiceImp);
    }

    @GetMapping("/")
    public String getMainPage(Model model) {
        String authenticatedUserId = getAuthenticationUserId();

        // Выборка заказов в зависимости от роли
        if (isOwner()) {
            model.addAttribute("orders", this.orderServiceImp.getAll());
            model.addAttribute("employeeMap", getEmployeeMap());
            model.addAttribute("clientsMap", getCompanysMap());
        } else if (isManager()) {
            model.addAttribute("orders", this.orderServiceImp.getOrdersByManagerUsername(authenticatedUserId));
            model.addAttribute("clientsMap", getCompanysMap());
        }
        else if (isPrinter()) {
            model.addAttribute("orders", this.orderServiceImp.getOrdersByOrderStatus("На печати"));
        }
        return "mainPage";
    }
}
