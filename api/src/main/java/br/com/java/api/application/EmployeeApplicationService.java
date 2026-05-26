package br.com.java.api.application;

import br.com.java.api.application.dto.EmployeeResponse;
import br.com.java.api.application.dto.EmployeeUpsertRequest;
import br.com.java.api.domain.entities.Employee;
import br.com.java.api.infrastructure.Repository.EmployeeRepository;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmployeeApplicationService {

    private final EmployeeRepository employeeRepository;

    public EmployeeApplicationService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> findAll(Integer page, Integer size) {
        int safePage = page == null || page < 1 ? 0 : page - 1;
        int safeSize = size == null || size < 1 ? 10 : Math.min(size, 200);

        List<Employee> employees = employeeRepository
            .findAllBy(PageRequest.of(safePage, safeSize))
            .getContent();

        if (employees.isEmpty()) {
            return List.of();
        }

        Map<Long, List<Long>> subordinatesByManager = loadSubordinateIdsByManager(
            employees.stream().map(Employee::getId).toList()
        );

        return employees
            .stream()
            .map(employee -> toResponse(employee, subordinatesByManager.getOrDefault(employee.getId(), List.of())))
            .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse findById(Long id) {
        Employee employee = findEntityById(id);
        Map<Long, List<Long>> subordinatesByManager = loadSubordinateIdsByManager(List.of(id));
        return toResponse(employee, subordinatesByManager.getOrDefault(id, List.of()));
    }

    @Transactional
    public EmployeeResponse create(EmployeeUpsertRequest request) {
        Employee employee = new Employee();
        applyUpsert(employee, request);
        Employee saved = employeeRepository.save(employee);
        return toResponse(saved, List.of());
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeUpsertRequest request) {
        Employee employee = findEntityById(id);
        applyUpsert(employee, request);
        Employee saved = employeeRepository.save(employee);
        Map<Long, List<Long>> subordinatesByManager = loadSubordinateIdsByManager(List.of(saved.getId()));
        return toResponse(saved, subordinatesByManager.getOrDefault(saved.getId(), List.of()));
    }

    @Transactional
    public void delete(Long id) {
        Employee employee = findEntityById(id);
        employeeRepository.delete(employee);
    }

    @Transactional(readOnly = true)
    public Employee findEntityById(Long id) {
        return employeeRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
    }

    private void applyUpsert(Employee employee, EmployeeUpsertRequest request) {
        employee.setName(request.name());
        employee.setBirthDate(toDate(request.birthDate()));
        employee.setContractType(request.contractType());
        employee.setRole(request.role());
        employee.setSalary(request.salary());

        if (request.managerId() == null) {
            employee.setManager(null);
            return;
        }

        if (employee.getId() != null && employee.getId().equals(request.managerId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee cannot be its own manager");
        }

        Employee manager = findEntityById(request.managerId());
        employee.setManager(manager);
    }

    private EmployeeResponse toResponse(Employee employee, List<Long> subordinateIds) {
        Long managerId = employee.getManager() == null ? null : employee.getManager().getId();

        return new EmployeeResponse(
            employee.getId(),
            employee.getName(),
            toLocalDate(employee.getBirthDate()),
            employee.getContractType(),
            employee.getRole(),
            employee.getGrossSalary(),
            employee.getLiquidSalary(subordinateIds.size()),
            managerId,
            subordinateIds
        );
    }

    private Map<Long, List<Long>> loadSubordinateIdsByManager(Collection<Long> managerIds) {
        if (managerIds == null || managerIds.isEmpty()) {
            return Map.of();
        }

        return employeeRepository.findSubordinateLinksByManagerIds(managerIds)
            .stream()
            .collect(Collectors.groupingBy(
                EmployeeRepository.ManagerSubordinateView::getManagerId,
                Collectors.mapping(EmployeeRepository.ManagerSubordinateView::getSubordinateId, Collectors.toList())
            ));
    }

    private Date toDate(LocalDate value) {
        return value == null ? null : Date.valueOf(value);
    }

    private LocalDate toLocalDate(java.util.Date value) {
        if (value == null) {
            return null;
        }

        if (value instanceof java.sql.Date sqlDate) {
            return sqlDate.toLocalDate();
        }

        return value.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}