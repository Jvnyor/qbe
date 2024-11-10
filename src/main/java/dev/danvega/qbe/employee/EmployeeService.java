package dev.danvega.qbe.employee;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.domain.ExampleMatcher.StringMatcher;
import static org.springframework.data.domain.ExampleMatcher.matching;

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

    // Find employees with custom matching rules
    public List<Employee> findEmployeesWithCustomMatcher(String searchLike) {
        searchLike = StringUtils.trimToNull(searchLike);

        Employee employee = Employee.builder()
                .id(StringUtils.isNumeric(searchLike) ? Long.parseLong(searchLike) : null)
                .firstName(searchLike)
                .lastName(searchLike)
                .department(searchLike)
                .position(searchLike)
                .salary(NumberUtils.isCreatable(searchLike) ? NumberUtils.createBigDecimal(searchLike) : null)
                .build();

        // Create a custom ExampleMatcher
        ExampleMatcher matcher = ExampleMatcher.matchingAny()
                .withIgnoreCase()                          // Ignore case for all string matches
                .withStringMatcher(StringMatcher.CONTAINING)// Use LIKE %value% for strings
                .withIgnoreNullValues()
                .withMatcher("id", match -> match.exact())// Ignore null values
                .withMatcher("firstName", match -> match.contains()) // But make firstName exact match
                .withMatcher("lastName", match -> match.contains())
                .withMatcher("department", match -> match.contains()) // Department can be partial
                .withMatcher("position", match -> match.contains())
                .withMatcher("salary", match -> match.exact());

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
