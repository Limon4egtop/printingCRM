package ru.limon4egtop.printingCRM.Services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.limon4egtop.printingCRM.Services.ClientsService;
import ru.limon4egtop.printingCRM.models.Clients;
import ru.limon4egtop.printingCRM.repos.ClientRepo;

import java.util.List;

@Service
public class ClientsServiceImp implements ClientsService {
    private final ClientRepo clientRepo;

    @Autowired
    public ClientsServiceImp(ClientRepo clientRepo) {
        this.clientRepo = clientRepo;
    }

    @Override
    public Clients addClient(Clients client) {
        return this.clientRepo.save(client);
    }

    @Override
    public Clients updateClient(Clients client) {
        return this.clientRepo.save(client);
    }

    @Override
    public Clients getClientByID(Long id) {
        return this.clientRepo.findClientsById(id);
    }

    @Override
    public Clients getClientByCompanyName(String companyName) {
        return this.clientRepo.findByCompanyName(companyName);
    }

    @Override
    public List<Clients> getAllClients() {
        return this.clientRepo.findAll();
    }

    @Override
    public List<Clients> getAllClientsOrderByIdDesc() {
        return this.clientRepo.findAllByOrderByIdDesc();
    }
}
