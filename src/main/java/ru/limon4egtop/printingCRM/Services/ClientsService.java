package ru.limon4egtop.printingCRM.Services;

import ru.limon4egtop.printingCRM.models.Clients;

import java.util.List;

public interface ClientsService {
    Clients addClient(Clients client);
    void updateClient(Clients client);
    Clients getClientByID(Long id);
    Clients getClientByCompanyName(String companyName);
    List<Clients> getAllClients();
    List<Clients> getAllClientsOrderByIdDesc();
}
