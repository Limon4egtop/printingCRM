package ru.limon4egtop.printingCRM.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import ru.limon4egtop.printingCRM.Services.impl.ClientsServiceImp;
import ru.limon4egtop.printingCRM.Services.impl.EmployeeServiceImp;
import ru.limon4egtop.printingCRM.Services.impl.OrderServiceImp;
import ru.limon4egtop.printingCRM.models.Clients;
import ru.limon4egtop.printingCRM.models.Employee;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class MainController {
    protected final ClientsServiceImp clientsServiceImp;
    protected final OrderServiceImp orderServiceImp;
    protected final EmployeeServiceImp employeeServiceImp;

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
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_OWNER"));
    }

    protected Boolean isUserOwner(final String username) {
        return this.employeeServiceImp.loadUserByUsername(username).getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_OWNER"));
    }

    protected Boolean isManager() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_MANAGER"));
    }

    protected Boolean isPrinter() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_PRINTER"));
    }

    protected Boolean isPrinterWithAccess(final String username,
                                          final String orderStatus) {
        return this.employeeServiceImp.loadUserByUsername(username).getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_PRINTER")) && Boolean.TRUE.equals(Objects.equals(orderStatus, "На печати"));
    }

    protected Map<String, String> getEmployeeMap() {
        return this.employeeServiceImp.getAllEmployees().stream()
                .collect(Collectors.toMap(Employee::getUsername, employee -> employee.getFirstName() + " " + employee.getLastName()));
    }

    protected Map<Long, String> getCompanysMap() {
        return clientsServiceImp.getAllClients().stream()
                .collect(Collectors.toMap(Clients::getId, Clients::getCompanyName));
    }
}

// TODO: добавить пагинацию