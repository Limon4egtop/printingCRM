package ru.limon4egtop.printingCRM.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import ru.limon4egtop.printingCRM.Services.EmployeeService;
import ru.limon4egtop.printingCRM.models.Employee;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/create")
    public String createEmployee() {
        return "createEmployee";
    }

    @GetMapping("/list")
    public String listEmployees(Model model) {
        model.addAttribute("usersData", employeeService.getAllEmployees());
        return "listEmployees";
    }

    @GetMapping("/view/{employeeId}")
    public String viewEmployee(@PathVariable("employeeId") Long employeeId,
                               Model model) {
        model.addAttribute("userData", employeeService.getEmployeeById(employeeId).get());
        return "viewEmployee";
    }

    @PostMapping("/put")
    public RedirectView putEmployee(@ModelAttribute("Employee") final Employee employee) {
        employeeService.create(employee);
        return new RedirectView("/employee/list");
    }

    @GetMapping("/editPussword/{employeeId}")
    public String editPussword(@PathVariable("employeeId") final Long employeeId,
                               Model model) {
        model.addAttribute("userId", employeeId);
        return "editEmployeePussword";
    }

    @PostMapping("/putPassword")
    public RedirectView putPassword(@ModelAttribute("Employee") final Employee employee) {
        employeeService.updatePassword(employee);
        return new RedirectView("/employee/list");
    }

    // TODO: добавить возможность изменить пароль
    // TODO: добавить страницу редактирования информации о сотруднике
}
