package ru.limon4egtop.printingCRM.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import ru.limon4egtop.printingCRM.Services.impl.EmployeeServiceImp;
import ru.limon4egtop.printingCRM.models.Employee;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    EmployeeServiceImp employeeServiceImp;

    @Autowired
    public EmployeeController(EmployeeServiceImp employeeServiceImp) {
        this.employeeServiceImp = employeeServiceImp;
    }

    @GetMapping("/create")
    public String createEmployee() {
        return "createEmployee";
    }

    @GetMapping("/list")
    public String listEmployees(Model model) {
        model.addAttribute("usersData", this.employeeServiceImp.getAllEmployees());
        return "listEmployees";
    }

    @GetMapping("/view/{employeeId}")
    public String viewEmployee(@PathVariable("employeeId") final Long employeeId,
                               Model model) {
        model.addAttribute("userData", this.employeeServiceImp.getEmployeeById(employeeId));
        return "viewEmployee";
    }

    @PostMapping("/put")
    public RedirectView putEmployee(@ModelAttribute("Employee") final Employee employee) {
        this.employeeServiceImp.create(employee);
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
        this.employeeServiceImp.updatePassword(employee);
        return new RedirectView("/employee/list");
    }

    // TODO: (опционально) добавить страницу редактирования информации о сотруднике
}
