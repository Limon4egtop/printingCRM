package ru.limon4egtop.printingCRM.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import ru.limon4egtop.printingCRM.Services.impl.ClientsServiceImp;
import ru.limon4egtop.printingCRM.Services.impl.EmployeeServiceImp;
import ru.limon4egtop.printingCRM.Services.impl.OrderServiceImp;
import ru.limon4egtop.printingCRM.models.Clients;
import ru.limon4egtop.printingCRM.models.Orders;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/client")
public class ClientController extends MainController {

    @Autowired
    public ClientController(final ClientsServiceImp clientsServiceImp, final OrderServiceImp orderServiceImp, final EmployeeServiceImp employeeServiceImp) {
        super(clientsServiceImp, orderServiceImp, employeeServiceImp);
    }

    @GetMapping("/list")
    public String client(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            if (isOwner()) {
                model.addAttribute("clients", this.clientsServiceImp.getAllClientsOrderByIdDesc());
                return "clientsPage";
            }
        }

        return "redirect:/error/customError";
    }

    @GetMapping("/clientInfo/{clientId}")
    public String getOrderInfoPage(Model model,
                                   @PathVariable("clientId") final Long clientId,
                                   final Principal principal) {
        String currentUsername = principal.getName();

        if (isOwner()) {
            model.addAttribute("orders", this.orderServiceImp.getOrdersByClientId(clientId));
        } else if (isManager()) {
            List<Orders> ordersList = this.orderServiceImp.getOrdersByClientIdAndManagerUsername(clientId, currentUsername);
            if (ordersList.isEmpty()) {
                return "error/error-403";
            }
            model.addAttribute("orders", ordersList);
        } else {
            return "error/error-403";
        }

        model.addAttribute("clientInfo", this.clientsServiceImp.getClientByID(clientId));

        return "clientInfo";
    }


    @GetMapping("/editClient/{clientId}")
    private String getEditOrderPage(@PathVariable("clientId") final Long clientId,
                                    Model model) {
        Clients client = this.clientsServiceImp.getClientByID(clientId);
        model.addAttribute("client", client);
        return "editClient";
    }

    @PostMapping("/refreshClientInfo")
    public RedirectView refreshClientInfo(@ModelAttribute("refreshClientData") final Clients client) {
        if (!isOwner() || !isManager()) {
            return new RedirectView("/error/customError");
        }
        this.clientsServiceImp.addClient(client);
        return new RedirectView("/client/clientInfo/"+client.getId());
    }
}
