package br.com.java.api.application;

import br.com.java.api.domain.Enums.ContractTypeEnum;
import br.com.java.api.domain.Enums.Roles;
import br.com.java.api.domain.entities.Employee;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeeAnalyticsServiceTest {

    @Test
    void rq07_shouldListOnlyEmployeesWithLiquidSalaryLowerThanThreeThousand() {
        EmployeeAnalyticsService service = new EmployeeAnalyticsService(fixedClock());

        Employee lowOne = createEmployee("Ana Silva", ContractTypeEnum.CLT, Roles.Administrative, "3000.00");
        Employee lowTwo = createEmployee("Bruno Mendes", ContractTypeEnum.PJ, Roles.Technician, "2500.00");
        Employee high = createEmployee("Carla Souza", ContractTypeEnum.CLT, Roles.Manager, "5000.00");
        high.addSubordinate(createEmployee("Sub 1", ContractTypeEnum.CLT, Roles.Technician, "1200.00"));
        high.addSubordinate(createEmployee("Sub 2", ContractTypeEnum.CLT, Roles.Technician, "1200.00"));

        List<Employee> result = service.findWithLiquidSalaryLowerThan(
            List.of(lowOne, lowTwo, high),
            new BigDecimal("3000.00")
        );

        assertEquals(2, result.size());
        assertEquals("Ana Silva", result.get(0).getName());
        assertEquals("Bruno Mendes", result.get(1).getName());
    }

    @Test
    void rq08_shouldReturnOnlyFirstNames() {
        EmployeeAnalyticsService service = new EmployeeAnalyticsService(fixedClock());
        Employee one = createEmployee("Ana Silva", ContractTypeEnum.CLT, Roles.Administrative, "2000.00");
        Employee two = createEmployee("  Bruno   Mendes   ", ContractTypeEnum.PJ, Roles.Technician, "2200.00");
        Employee three = createEmployee("Carla", ContractTypeEnum.CLT, Roles.Manager, "4000.00");

        List<String> firstNames = service.getFirstNames(List.of(one, two, three));

        assertEquals(List.of("Ana", "Bruno", "Carla"), firstNames);
    }

    @Test
    void rq09_shouldSumAgesInYearsOfAllEmployees() {
        Clock clock = fixedClock();
        EmployeeAnalyticsService service = new EmployeeAnalyticsService(clock);

        Employee one = createEmployeeWithBirthDate("Ana Silva", LocalDate.of(1990, 1, 1));
        Employee two = createEmployeeWithBirthDate("Bruno Mendes", LocalDate.of(2000, 1, 1));
        Employee three = createEmployeeWithBirthDate("Carla Souza", LocalDate.of(1985, 1, 1));

        int sumAges = service.sumAgesInYears(List.of(one, two, three));

        assertEquals(35 + 25 + 40, sumAges);
    }

    private Employee createEmployee(String name, ContractTypeEnum contractType, Roles role, String salary) {
        Employee employee = new Employee();
        employee.setName(name);
        employee.setBirthDate(toDate(LocalDate.of(1990, 1, 1)));
        employee.setContractType(contractType);
        employee.setRole(role);
        employee.setSalary(new BigDecimal(salary));
        return employee;
    }

    private Employee createEmployeeWithBirthDate(String name, LocalDate birthDate) {
        Employee employee = new Employee();
        employee.setName(name);
        employee.setBirthDate(toDate(birthDate));
        employee.setContractType(ContractTypeEnum.CLT);
        employee.setRole(Roles.Technician);
        employee.setSalary(new BigDecimal("1000.00"));
        return employee;
    }

    private Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Clock fixedClock() {
        return Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneId.of("UTC"));
    }
}