package br.com.java.api.application.dto;

import br.com.java.api.domain.Enums.ContractTypeEnum;
import br.com.java.api.domain.Enums.Roles;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeUpsertRequest(
    String name,
    LocalDate birthDate,
    ContractTypeEnum contractType,
    Roles role,
    BigDecimal salary,
    Long managerId
) {
}