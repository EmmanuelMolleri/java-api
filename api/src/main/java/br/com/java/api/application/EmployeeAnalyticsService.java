package br.com.java.api.application;

import br.com.java.api.domain.entities.Employee;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EmployeeAnalyticsService {

    private final Clock clock;

    public EmployeeAnalyticsService() {
        this(Clock.systemDefaultZone());
    }

    EmployeeAnalyticsService(Clock clock) {
        this.clock = clock;
    }

    public List<Employee> findWithLiquidSalaryLowerThan(List<Employee> employees, BigDecimal threshold) {
        if (employees == null || employees.isEmpty() || threshold == null) {
            return List.of();
        }

        return employees.stream()
            .filter(employee -> employee != null)
            .filter(employee -> employee.getLiquidSalary().compareTo(threshold) < 0)
            .toList();
    }

    public List<String> getFirstNames(List<Employee> employees) {
        if (employees == null || employees.isEmpty()) {
            return List.of();
        }

        return employees.stream()
            .filter(employee -> employee != null)
            .map(Employee::getName)
            .filter(name -> name != null && !name.isBlank())
            .map(this::extractFirstName)
            .toList();
    }

    public int sumAgesInYears(List<Employee> employees) {
        if (employees == null || employees.isEmpty()) {
            return 0;
        }

        LocalDate today = LocalDate.now(clock);

        return employees.stream()
            .filter(employee -> employee != null)
            .map(Employee::getBirthDate)
            .filter(birthDate -> birthDate != null)
            .mapToInt(birthDate -> {
                LocalDate localBirthDate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                return Period.between(localBirthDate, today).getYears();
            })
            .sum();
    }

    private String extractFirstName(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        return parts[0];
    }
}