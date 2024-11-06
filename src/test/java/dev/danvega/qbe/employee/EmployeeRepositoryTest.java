package dev.danvega.qbe.employee;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.data.domain.ExampleMatcher.StringMatcher;

@SpringBootTest
@Testcontainers
class EmployeeRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"));

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    @DisplayName("Should find all developers in IT department")
    void shouldFindAllITDevelopers() {
        // Given
        Employee developerProbe = Employee.builder()
                .department("IT")
                .position("Developer")
                .build();

        // When
        List<Employee> developers = employeeRepository.findAll(Example.of(developerProbe));

        // Then
        assertThat(developers)
                .hasSize(2)
                .extracting(Employee::getFirstName)
                .containsExactlyInAnyOrder("Jane", "Mike");

        assertThat(developers)
                .extracting(Employee::getLastName)
                .containsExactlyInAnyOrder("Doe", "Johnson");

        // Verify all found employees are actually developers in IT
        developers.forEach(dev -> {
            assertEquals("IT", dev.getDepartment());
            assertEquals("Developer", dev.getPosition());
        });
    }

    @Test
    @DisplayName("Should find all employees with Smith lastname")
    void shouldFindAllSmithEmployees() {
        // Given
        Employee smithProbe = Employee.builder()
                .lastName("Smith")
                .build();

        // When
        List<Employee> smiths = employeeRepository.findAll(Example.of(smithProbe));

        // Then
        assertThat(smiths)
                .hasSize(4)
                .extracting(Employee::getFirstName)
                .containsExactlyInAnyOrder("John", "Thomas", "Anna", "Robert");

        // Verify all found employees have Smith as lastname
        smiths.forEach(smith ->
                assertEquals("Smith", smith.getLastName(),
                        "All employees should have Smith as lastname"));
    }

    @Test
    @DisplayName("Should find all John-like names with case-insensitive partial match")
    void shouldFindAllJohnVariations() {
        // Given
        Employee johnProbe = Employee.builder()
                .firstName("john")
                .build();

        ExampleMatcher nameMatcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(StringMatcher.CONTAINING);

        // When
        List<Employee> johns = employeeRepository.findAll(Example.of(johnProbe, nameMatcher));

        // Then
        assertThat(johns)
                .hasSize(2)
                .extracting(Employee::getFirstName)
                .containsExactlyInAnyOrder("John", "Johnny");

        // Verify each name contains "john" case insensitive
        johns.forEach(john ->
                assertTrue(john.getFirstName().toLowerCase().contains("john"),
                        "Each name should contain 'john'"));
    }

    @Test
    @DisplayName("Should find all managers across departments")
    void shouldFindAllManagers() {
        // Given
        Employee managerProbe = Employee.builder()
                .position("Manager")
                .build();

        // When
        List<Employee> managers = employeeRepository.findAll(Example.of(managerProbe));

        // Then
        assertThat(managers)
                .hasSize(4)
                .extracting(Employee::getDepartment)
                .containsExactlyInAnyOrder("HR", "Marketing", "Sales", "Operations");

        // Verify all found employees are managers
        managers.forEach(manager -> {
            assertEquals("Manager", manager.getPosition(),
                    "All employees should be managers");
        });
    }

    @Test
    @DisplayName("Should find engineers in Engineering department using complex matcher")
    void shouldFindEngineersWithComplexCriteria() {
        // Given
        Employee complexProbe = Employee.builder()
                .department("Engineering")
                .position("Engineer")
                .build();

        ExampleMatcher complexMatcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(StringMatcher.CONTAINING)
                .withIgnoreNullValues();

        // When
        List<Employee> engineers = employeeRepository.findAll(Example.of(complexProbe, complexMatcher));

        // Then
        assertThat(engineers)
                .hasSize(4)
                .extracting(Employee::getPosition)
                .allMatch(position -> position.contains("Engineer"));

        // Verify all are in Engineering department
        engineers.forEach(engineer -> {
            assertEquals("Engineering", engineer.getDepartment());
            assertTrue(engineer.getPosition().contains("Engineer"),
                    "Position should contain 'Engineer'");
        });
    }

    @Test
    @DisplayName("Should find no matches for non-existent criteria")
    void shouldFindNoMatchesForNonExistentCriteria() {
        // Given
        Employee nonExistentProbe = Employee.builder()
                .department("Non-Existent")
                .position("Imaginary Position")
                .build();

        // When
        List<Employee> results = employeeRepository.findAll(Example.of(nonExistentProbe));

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("Should find single employee by exact match")
    void shouldFindSingleEmployeeByExactMatch() {
        // Given
        Employee specificProbe = Employee.builder()
                .firstName("Jane")
                .lastName("Doe")
                .department("IT")
                .position("Developer")
                .build();

        // When
        Example<Employee> example = Example.of(specificProbe);
        boolean exists = employeeRepository.exists(example);
        Optional<Employee> employee = employeeRepository.findOne(example);

        // Then
        assertTrue(exists, "Should find exact match");
        assertTrue(employee.isPresent(), "Should find the employee");
        employee.ifPresent(emp -> {
            assertEquals("Jane", emp.getFirstName());
            assertEquals("Doe", emp.getLastName());
            assertEquals("IT", emp.getDepartment());
            assertEquals("Developer", emp.getPosition());
        });
    }

    @Test
    @DisplayName("Should handle null values in probe entity")
    void shouldHandleNullValuesInProbe() {
        // Given
        Employee probeWithNulls = Employee.builder()
                .department("IT")
                .firstName(null)  // Should be ignored
                .lastName(null)   // Should be ignored
                .position(null)   // Should be ignored
                .build();

        ExampleMatcher matcherIgnoringNulls = ExampleMatcher.matching()
                .withIgnoreNullValues();

        // When
        List<Employee> itEmployees = employeeRepository.findAll(
                Example.of(probeWithNulls, matcherIgnoringNulls));

        // Then
        assertThat(itEmployees)
                .isNotEmpty()
                .allMatch(employee -> "IT".equals(employee.getDepartment()));

        // Verify we got all IT department employees
        long expectedItEmployeeCount = employeeRepository.findAll().stream()
                .filter(e -> "IT".equals(e.getDepartment()))
                .count();

        assertEquals(expectedItEmployeeCount, itEmployees.size(),
                "Should find all IT department employees regardless of other fields");
    }

    @Test
    @DisplayName("Should match using custom ExampleMatcher configuration")
    void shouldMatchUsingCustomMatcher() {
        // Given
        Employee probe = Employee.builder()
                .firstName("JOHN")  // Testing case insensitive
                .department("it")   // Testing case insensitive
                .build();

        ExampleMatcher customMatcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withMatcher("firstName", match -> match.exact())
                .withMatcher("department", match -> match.exact())
                .withIgnoreNullValues();

        // When
        List<Employee> matches = employeeRepository.findAll(Example.of(probe, customMatcher));

        // Then
        assertThat(matches)
                .isNotEmpty()
                .allMatch(employee ->
                        employee.getFirstName().equalsIgnoreCase("JOHN") &&
                                employee.getDepartment().equalsIgnoreCase("it"));
    }
}