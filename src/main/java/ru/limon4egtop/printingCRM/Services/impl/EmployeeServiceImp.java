package ru.limon4egtop.printingCRM.Services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.limon4egtop.printingCRM.models.Employee;
import ru.limon4egtop.printingCRM.repos.EmployeeRepo;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImp implements UserDetailsService {
    private EmployeeRepo employeeRepo;

    @Autowired
    public EmployeeServiceImp(EmployeeRepo employeeRepo) {
        this.employeeRepo = employeeRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return employeeRepo.findByUsername(username);
    }

    public void create(Employee putEmployee) {
//        System.out.println("id " + putEmployee.getId());
//        System.out.println("username " + putEmployee.getUsername());
//        System.out.println("password " + putEmployee.getPassword());
//        System.out.println("firstName " + putEmployee.getFirstName());
//        System.out.println("lastName " + putEmployee.getLastName());
//        System.out.println("role " + putEmployee.getAuthorities().stream().findFirst().get());
        // Кодирует пароль и создаёт новый объект пользователя
        Employee employee = Employee.builder()
                .username(putEmployee.getUsername())
                .password(new BCryptPasswordEncoder().encode(putEmployee.getPassword()))
                .authorities(putEmployee.getAuthorities().stream().findFirst().get().toString())
                .firstName(putEmployee.getFirstName())
                .lastName(putEmployee.getLastName())
                .build();
        employeeRepo.save(employee);
    }

    public void updatePassword(Employee putEmployee) {
        Employee employee = employeeRepo.findById(putEmployee.getId()).get();
        employee.setPassword(new BCryptPasswordEncoder().encode(putEmployee.getPassword()));
        employeeRepo.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepo.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepo.findById(id).orElseThrow(() -> new RuntimeException("Не найден сотрудник с id: " + id));
    }
}