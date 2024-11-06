package dev.danvega.qbe.employee;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Entity
@Table(name = "employees")
public class Employee {

    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String department;
    private String position;
    private BigDecimal salary;

    public Employee() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Employee employee;

        private Builder() {
            employee = new Employee();
        }

        public Builder id(Long id) {
            employee.id = id;
            return this;
        }

        public Builder firstName(String firstName) {
            employee.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            employee.lastName = lastName;
            return this;
        }

        public Builder department(String department) {
            employee.department = department;
            return this;
        }

        public Builder position(String position) {
            employee.position = position;
            return this;
        }

        public Builder salary(BigDecimal salary) {
            employee.salary = salary;
            return this;
        }

        public Employee build() {
            return employee;
        }
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", department='" + department + '\'' +
                ", position='" + position + '\'' +
                ", salary=" + salary +
                '}';
    }

}