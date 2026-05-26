package br.com.java.api.controller;

import br.com.java.api.application.EmployeeApplicationService;
import br.com.java.api.application.dto.EmployeeResponse;
import br.com.java.api.application.dto.EmployeeUpsertRequest;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeApplicationService employeeApplicationService;

    public EmployeeController(EmployeeApplicationService employeeApplicationService) {
        this.employeeApplicationService = employeeApplicationService;
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAll(@RequestParam(defaultValue = "1") Integer page,
                                                          @RequestParam(defaultValue = "10") Integer size) {
        var employees = employeeApplicationService.findAll(page, size);

        if(employees.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getById(@PathVariable Long id) {
        var employee = employeeApplicationService.findById(id);
        if (employee == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/{id}/liquid-salary")
    public ResponseEntity<BigDecimal> getLiquidSalary(@PathVariable Long id) {
        var employee = employeeApplicationService.findById(id);
        if (employee == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(employee.liquidSalary());
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@RequestBody EmployeeUpsertRequest request) {
        EmployeeResponse created = employeeApplicationService.create(request);
        return ResponseEntity
            .created(URI.create("/employees/" + created.id()))
            .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> update(@PathVariable Long id, @RequestBody EmployeeUpsertRequest request) {
        var updated = employeeApplicationService.update(id, request);
        
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        var employee = employeeApplicationService.findById(id);
        
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }
        
        employeeApplicationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}