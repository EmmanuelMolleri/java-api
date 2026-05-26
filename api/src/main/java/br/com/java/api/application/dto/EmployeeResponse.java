package br.com.java.api.application.dto;

import br.com.java.api.domain.Enums.ContractTypeEnum;
import br.com.java.api.domain.Enums.Roles;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record EmployeeResponse(
    Long id,
    String name,
    LocalDate birthDate,
    ContractTypeEnum contractType,
    Roles role,
    BigDecimal salary,
    BigDecimal liquidSalary,
    Long managerId,
    List<Long> subordinateIds
) {
}