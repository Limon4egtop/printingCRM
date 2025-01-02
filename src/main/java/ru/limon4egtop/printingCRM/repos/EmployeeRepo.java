package ru.limon4egtop.printingCRM.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import ru.limon4egtop.printingCRM.models.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepo extends JpaRepository<Employee, Long> {
    UserDetails findByUsername(String username);
    List<Employee> findAll();
}
