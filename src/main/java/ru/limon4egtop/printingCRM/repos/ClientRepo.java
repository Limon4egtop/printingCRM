package ru.limon4egtop.printingCRM.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.limon4egtop.printingCRM.models.Clients;

import java.util.List;

@Repository
public interface ClientRepo extends JpaRepository<Clients, Long> {
    Clients findByCompanyName(String companyName);
    List<Clients> findAll();
    List<Clients> findAllByOrderByIdDesc();
    Clients findClientsById(Long id);
}
