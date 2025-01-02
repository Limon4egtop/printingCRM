package ru.limon4egtop.printingCRM.Services;

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

@Service // Помечает этот класс как компонент сервиса для внедрения зависимостей
public class EmployeeService implements UserDetailsService {
    private EmployeeRepo employeeRepo;

    @Autowired
    public EmployeeService(EmployeeRepo employeeRepo) {
        this.employeeRepo = employeeRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Извлекает данные пользователя из базы данных по имени пользователя
        return employeeRepo.findByUsername(username);
    }

    public void create(Employee putEmployee) {
        System.out.println("id " + putEmployee.getId());
        System.out.println("username " + putEmployee.getUsername());
        System.out.println("password " + putEmployee.getPassword());
        System.out.println("firstName " + putEmployee.getFirstName());
        System.out.println("lastName " + putEmployee.getLastName());
        System.out.println("role " + putEmployee.getAuthorities());
        // Кодирует пароль и создаёт новый объект пользователя
        Employee employee = Employee.builder()
                .username(putEmployee.getUsername())
                .password(new BCryptPasswordEncoder().encode(putEmployee.getPassword()))
                .authorities(putEmployee.getAuthorities().toString())
                .firstName(putEmployee.getFirstName())
                .lastName(putEmployee.getLastName())
                .build();

        // Сохраняет нового пользователя в базе данных
        employeeRepo.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepo.findAll();
    }

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepo.findById(id);
    }
}