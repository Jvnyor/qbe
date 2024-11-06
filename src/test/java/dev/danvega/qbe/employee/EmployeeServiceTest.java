package dev.danvega.qbe.employee;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private EmployeeService employeeService;

    @Captor
    private ArgumentCaptor<Example<Employee>> exampleCaptor;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(employeeRepository);
    }

    @Test
    @DisplayName("Should find employees by exact criteria")
    void shouldFindEmployeesByExample() {
        // Given
        Employee probe = Employee.builder()
                .department("IT")
                .position("Developer")
                .build();

        List<Employee> expectedEmployees = List.of(
                Employee.builder()
                        .firstName("Jane")
                        .lastName("Doe")
                        .department("IT")
                        .position("Developer")
                        .build(),
                Employee.builder()
                        .firstName("Mike")
                        .lastName("Johnson")
                        .department("IT")
                        .position("Developer")
                        .build()
        );

        when(employeeRepository.findAll(any(Example.class))).thenReturn(expectedEmployees);

        // When
        List<Employee> result = employeeService.findEmployeesByExample(probe);

        // Then
        verify(employeeRepository).findAll(exampleCaptor.capture());
        Example<Employee> capturedExample = exampleCaptor.getValue();

        assertThat(result)
                .hasSize(2)
                .isEqualTo(expectedEmployees);

        assertThat(capturedExample.getProbe())
                .hasFieldOrPropertyWithValue("department", "IT")
                .hasFieldOrPropertyWithValue("position", "Developer");
    }

    @Test
    @DisplayName("Should find employees with custom matcher")
    void shouldFindEmployeesWithCustomMatcher() {
        // Given
        String firstName = "John";
        String department = "eng";

        List<Employee> expectedEmployees = List.of(
                Employee.builder()
                        .firstName("John")
                        .lastName("Smith")
                        .department("Engineering")
                        .position("Engineer")
                        .build()
        );

        when(employeeRepository.findAll(any(Example.class))).thenReturn(expectedEmployees);

        // When
        List<Employee> result = employeeService.findEmployeesWithCustomMatcher(firstName, department);

        // Then
        verify(employeeRepository).findAll(exampleCaptor.capture());
        Example<Employee> capturedExample = exampleCaptor.getValue();
        ExampleMatcher capturedMatcher = capturedExample.getMatcher();

        assertThat(result).isEqualTo(expectedEmployees);

        // Verify the probe values
        assertThat(capturedExample.getProbe())
                .hasFieldOrPropertyWithValue("firstName", "John")
                .hasFieldOrPropertyWithValue("department", "eng");

        // Verify matcher configuration
        assertThat(capturedMatcher.isIgnoreCaseEnabled()).isTrue();
        assertThat(capturedMatcher.getNullHandler()).isEqualTo(ExampleMatcher.NullHandler.IGNORE);
        assertThat(capturedMatcher.getDefaultStringMatcher())
                .isEqualTo(ExampleMatcher.StringMatcher.CONTAINING);
    }

    @Test
    @DisplayName("Should find one employee by example")
    void shouldFindOneEmployeeByExample() {
        // Given
        Employee probe = Employee.builder()
                .firstName("Jane")
                .lastName("Doe")
                .department("IT")
                .position("Developer")
                .build();

        Employee expectedEmployee = Employee.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Doe")
                .department("IT")
                .position("Developer")
                .build();

        when(employeeRepository.findOne(any(Example.class)))
                .thenReturn(Optional.of(expectedEmployee));

        // When
        Optional<Employee> result = employeeService.findOneEmployeeByExample(probe);

        // Then
        verify(employeeRepository).findOne(exampleCaptor.capture());

        assertThat(result)
                .isPresent()
                .contains(expectedEmployee);

        assertThat(exampleCaptor.getValue().getProbe())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(probe);
    }

    @Test
    @DisplayName("Should count employees by example")
    void shouldCountEmployeesByExample() {
        // Given
        Employee probe = Employee.builder()
                .department("IT")
                .build();

        when(employeeRepository.count(any(Example.class))).thenReturn(5L);

        // When
        long count = employeeService.countEmployeesByExample(probe);

        // Then
        verify(employeeRepository).count(exampleCaptor.capture());

        assertThat(count).isEqualTo(5L);
        assertThat(exampleCaptor.getValue().getProbe())
                .hasFieldOrPropertyWithValue("department", "IT");
    }

    @Test
    @DisplayName("Should check if employees exist by example")
    void shouldCheckIfEmployeesExistByExample() {
        // Given
        Employee probe = Employee.builder()
                .department("IT")
                .position("Developer")
                .build();

        when(employeeRepository.exists(any(Example.class))).thenReturn(true);

        // When
        boolean exists = employeeService.existsByExample(probe);

        // Then
        verify(employeeRepository).exists(exampleCaptor.capture());

        assertThat(exists).isTrue();
        assertThat(exampleCaptor.getValue().getProbe())
                .hasFieldOrPropertyWithValue("department", "IT")
                .hasFieldOrPropertyWithValue("position", "Developer");
    }

    @Test
    @DisplayName("Should handle empty results for custom matcher")
    void shouldHandleEmptyResultsForCustomMatcher() {
        // Given
        String firstName = "NonExistent";
        String department = "Unknown";

        when(employeeRepository.findAll(any(Example.class))).thenReturn(List.of());

        // When
        List<Employee> result = employeeService.findEmployeesWithCustomMatcher(firstName, department);

        // Then
        verify(employeeRepository).findAll(exampleCaptor.capture());

        assertThat(result).isEmpty();
        assertThat(exampleCaptor.getValue().getProbe())
                .hasFieldOrPropertyWithValue("firstName", "NonExistent")
                .hasFieldOrPropertyWithValue("department", "Unknown");
    }
}
