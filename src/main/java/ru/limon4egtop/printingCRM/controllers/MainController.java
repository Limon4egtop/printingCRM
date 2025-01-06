package ru.limon4egtop.printingCRM.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.limon4egtop.printingCRM.Services.impl.ClientsServiceImp;
import ru.limon4egtop.printingCRM.models.Clients;
import ru.limon4egtop.printingCRM.models.Employee;
import ru.limon4egtop.printingCRM.repos.EmployeeRepo;
import ru.limon4egtop.printingCRM.repos.OrderRepo;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MainController {
    private OrderRepo orderRepo;
    private EmployeeRepo employeeRepo;
    protected ClientsServiceImp clientsServiceImp;

    @Autowired
    public MainController(OrderRepo orderRepo, EmployeeRepo employeeRepo, final ClientsServiceImp clientsServiceImp) {
        this.orderRepo = orderRepo;
        this.employeeRepo = employeeRepo;
        this.clientsServiceImp = clientsServiceImp;
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
            model.addAttribute("orders", orderRepo.findAllByOrderByDateCreateDescIdDesc());
            model.addAttribute("employeeMap", getEmployeeMap());
            model.addAttribute("clientsMap", getCompanysMap());
        } else if (isManager) {
            model.addAttribute("orders", orderRepo.findOrdersByManagerUsernameOrderByDateCreateDescIdDesc(authenticatedUserId));
            model.addAttribute("clientsMap", getCompanysMap());
        }
        else if (isPrinter) {
            model.addAttribute("orders", orderRepo.findOrdersByOrderStatus("На печати"));
        }
        return "mainPage";
    }


    // TODO: добавить пагинацию
    // TODO: добавить наследование от mainController
    // TODO: добавить сервисы

    protected Map<String, String> getEmployeeMap() {
        return employeeRepo.findAll().stream()
                .collect(Collectors.toMap(Employee::getUsername, employee -> employee.getFirstName() + " " + employee.getLastName()));
    }

    protected Map<Long, String> getCompanysMap() {
        return clientsServiceImp.getAllClients().stream()
                .collect(Collectors.toMap(Clients::getId, Clients::getCompanyName));
    }


}
