package br.com.java.api.domain.entities;

import br.com.java.api.domain.Enums.ContractTypeEnum;
import br.com.java.api.domain.Enums.Roles;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class EmployeeTest {

    @Test
    void rq01_shouldStoreRequiredEmployeeData() {
        Employee employee = new Employee();
        Date birthDate = toDate(LocalDate.of(1990, 5, 10));

        employee.setName("Ana Silva");
        employee.setBirthDate(birthDate);
        employee.setContractType(ContractTypeEnum.CLT);
        employee.setSalary(new BigDecimal("5000.00"));

        assertEquals("Ana Silva", employee.getName());
        assertEquals(birthDate, employee.getBirthDate());
        assertEquals(ContractTypeEnum.CLT, employee.getContractType());
        assertBigDecimalEquals("5000.00", employee.getGrossSalary());
    }

    @Test
    void rq02_shouldSupportAdministrativeTechnicianAndManagerRoles() {
        Employee employee = new Employee();

        employee.setRole(Roles.Administrative);
        assertSame(Roles.Administrative, employee.getRole());

        employee.setRole(Roles.Technician);
        assertSame(Roles.Technician, employee.getRole());

        employee.setRole(Roles.Manager);
        assertSame(Roles.Manager, employee.getRole());
    }

    @Test
    void rq03_shouldAllowBaseSalaryChange() {
        Employee employee = createEmployee("Bruno Lima", ContractTypeEnum.CLT, Roles.Technician, "2500.00");

        employee.setSalary(new BigDecimal("4200.00"));

        assertBigDecimalEquals("4200.00", employee.getGrossSalary());
    }

    @Test
    void rq04_shouldCalculateCltLiquidSalaryWithThirtyPercentDiscount() {
        Employee employee = createEmployee("Carlos Gomes", ContractTypeEnum.CLT, Roles.Technician, "1000.00");

        assertBigDecimalEquals("700.00", employee.getLiquidSalary());
    }

    @Test
    void rq05_shouldCalculatePjLiquidSalaryWithTenPercentDiscount() {
        Employee employee = createEmployee("Diana Reis", ContractTypeEnum.PJ, Roles.Administrative, "1000.00");

        assertBigDecimalEquals("900.00", employee.getLiquidSalary());
    }

    @Test
    void rq06_shouldCalculateManagerLiquidSalaryWithBonusPerSubordinate() {
        Employee manager = createEmployee("Eduardo Costa", ContractTypeEnum.CLT, Roles.Manager, "2000.00");
        manager.addSubordinate(createEmployee("Sub 1", ContractTypeEnum.CLT, Roles.Technician, "1500.00"));
        manager.addSubordinate(createEmployee("Sub 2", ContractTypeEnum.PJ, Roles.Technician, "1600.00"));
        manager.addSubordinate(createEmployee("Sub 3", ContractTypeEnum.CLT, Roles.Administrative, "1700.00"));

        assertBigDecimalEquals("1700.00", manager.getLiquidSalary());
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

    private Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private void assertBigDecimalEquals(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }
}