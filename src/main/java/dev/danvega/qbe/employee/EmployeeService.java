package dev.danvega.qbe.employee;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.domain.ExampleMatcher.*;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // Find all employees matching exact criteria
    public List<Employee> findEmployeesByExample(Employee employee) {
        Example<Employee> example = Example.of(employee);
        return employeeRepository.findAll(example);
    }

    // Find a single employee with example
    public Optional<Employee> findOneEmployeeByExample(Employee employee) {
        Example<Employee> example = Example.of(employee);
        return employeeRepository.findOne(example);
    }

    // Find employees with custom matching rules
    public List<Employee> findEmployeesWithCustomMatcher(String firstName,
                                                         String department) {
        Employee employee = Employee.builder()
                .firstName(firstName)
                .department(department)
                .build();

        // Create a custom ExampleMatcher
        ExampleMatcher matcher = matching()
                .withIgnoreCase()                          // Ignore case for all string matches
                .withStringMatcher(StringMatcher.CONTAINING)// Use LIKE %value% for strings
                .withIgnoreNullValues()                    // Ignore null values
                .withMatcher("firstName", match -> match.exact()) // But make firstName exact match
                .withMatcher("department", match -> match.contains()); // Department can be partial

        Example<Employee> example = Example.of(employee, matcher);
        return employeeRepository.findAll(example);
    }

    // Count employees matching example
    public long countEmployeesByExample(Employee employee) {
        Example<Employee> example = Example.of(employee);
        return employeeRepository.count(example);
    }

    // Check if any employees match the example
    public boolean existsByExample(Employee employee) {
        Example<Employee> example = Example.of(employee);
        return employeeRepository.exists(example);
    }
}
