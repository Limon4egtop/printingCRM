package ru.limon4egtop.printingCRM.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.limon4egtop.printingCRM.Services.impl.ClientsServiceImp;
import ru.limon4egtop.printingCRM.Services.impl.EmployeeServiceImp;
import ru.limon4egtop.printingCRM.Services.impl.OrderServiceImp;
import ru.limon4egtop.printingCRM.models.Clients;
import ru.limon4egtop.printingCRM.models.Employee;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MainController {
    protected ClientsServiceImp clientsServiceImp;
    protected OrderServiceImp orderServiceImp;
    protected EmployeeServiceImp employeeServiceImp;

    @Autowired
    public MainController(final ClientsServiceImp clientsServiceImp, final OrderServiceImp orderServiceImp, final EmployeeServiceImp employeeServiceImp) {
        this.clientsServiceImp = clientsServiceImp;
        this.orderServiceImp = orderServiceImp;
        this.employeeServiceImp = employeeServiceImp;
    }

    protected String getAuthenticationUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    protected Boolean isOwner() {
        boolean hasRoleAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_OWNER"));
        ;
        return hasRoleAdmin;
    }

    @GetMapping("/")
    public String getMainPage(Model model) {
        String authenticatedUserId = getAuthenticationUserId();
        boolean isOwner = isOwner();

        // Определяем, является ли пользователь с ролью ROLE_PRINTER
        boolean isPrinter = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_PRINTER"));

        boolean isManager = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_MANAGER"));

        // Выборка заказов в зависимости от роли
        if (isOwner) {
            model.addAttribute("orders", this.orderServiceImp.getAll());
            model.addAttribute("employeeMap", getEmployeeMap());
            model.addAttribute("clientsMap", getCompanysMap());
        } else if (isManager) {
            model.addAttribute("orders", this.orderServiceImp.getOrdersByManagerUsername(authenticatedUserId));
            model.addAttribute("clientsMap", getCompanysMap());
        }
        else if (isPrinter) {
            model.addAttribute("orders", this.orderServiceImp.getOrdersByOrderStatus("На печати"));
        }
        return "mainPage";
    }


    // TODO: добавить пагинацию
    // TODO: добавить наследование от mainController
    // TODO: добавить сервисы

    protected Map<String, String> getEmployeeMap() {
        return this.employeeServiceImp.getAllEmployees().stream()
                .collect(Collectors.toMap(Employee::getUsername, employee -> employee.getFirstName() + " " + employee.getLastName()));
    }

    protected Map<Long, String> getCompanysMap() {
        return clientsServiceImp.getAllClients().stream()
                .collect(Collectors.toMap(Clients::getId, Clients::getCompanyName));
    }


}
