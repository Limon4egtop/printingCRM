package ru.limon4egtop.printingCRM.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.limon4egtop.printingCRM.models.Clients;
import ru.limon4egtop.printingCRM.models.Employee;
import ru.limon4egtop.printingCRM.repos.ClientRepo;
import ru.limon4egtop.printingCRM.repos.EmployeeRepo;
import ru.limon4egtop.printingCRM.repos.OrderRepo;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MainController {
    private OrderRepo orderRepo;
    private ClientRepo clientRepo;
    private EmployeeRepo employeeRepo;

    @Autowired
    public MainController(OrderRepo orderRepo, ClientRepo clientRepo, EmployeeRepo employeeRepo) {
        this.orderRepo = orderRepo;
        this.clientRepo = clientRepo;
        this.employeeRepo = employeeRepo;
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
        model.addAttribute("orders",
                isOwner() ? orderRepo.findAllByOrderByIdDesc()
                        : orderRepo.findOrdersByManagerUsernameOrderByIdDesc(getAuthenticationUserId()));
        model.addAttribute("clientsMap", getCompanysMap());
        model.addAttribute("employeeMap", getEmployeeMap());
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
        return clientRepo.findAll().stream()
                .collect(Collectors.toMap(Clients::getId, Clients::getCompanyName));
    }


}
