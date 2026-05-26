package br.com.java.api.domain.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import br.com.java.api.domain.Enums.ContractTypeEnum;
import br.com.java.api.domain.Enums.Roles;

@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long Id;

    @Column(name = "name", nullable = false, length = 120)
    private String Name;

    @Temporal(TemporalType.DATE)
    @Column(name = "birth_date")
    private Date BirthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type", nullable = false, length = 10)
    private ContractTypeEnum ContractType;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Roles Role;

    @Column(name = "salary", nullable = false, precision = 14, scale = 2)
    private BigDecimal Salary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee Manager;

    @OneToMany(mappedBy = "Manager", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<Employee> Subordinates = new ArrayList<>();
    
    public Long getId() {
        return Id;
    }
    
    public void setId(Long id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Date getBirthDate() {
        return BirthDate;
    }

    public void setBirthDate(Date birthDate) {
        BirthDate = birthDate;
    }

    public ContractTypeEnum getContractType() {
        return ContractType;
    }

    public void setContractType(ContractTypeEnum contractType) {
        ContractType = contractType;
    }

    public Roles getRole() {
        return Role;
    }

    public void setRole(Roles role) {
        Role = role;
    }

    public Employee getManager() {
        return Manager;
    }

    public void setManager(Employee manager) {
        Manager = manager;
    }

    public List<Employee> getSubordinates() {
        return Subordinates;
    }

    public void setSubordinates(List<Employee> subordinates) {
        Subordinates = subordinates == null ? new ArrayList<>() : subordinates;
        for (Employee subordinate : Subordinates) {
            subordinate.setManager(this);
        }
    }

    public int getSubordinatesCount() {
        return Subordinates == null ? 0 : Subordinates.size();
    }

    public void addSubordinate(Employee subordinate) {
        if (subordinate == null) {
            return;
        }

        if (Subordinates == null) {
            Subordinates = new ArrayList<>();
        }

        subordinate.setManager(this);
        Subordinates.add(subordinate);
    }

    public void removeSubordinate(Employee subordinate) {
        if (subordinate == null || Subordinates == null) {
            return;
        }

        if (Subordinates.remove(subordinate)) {
            subordinate.setManager(null);
        }
    }

    public BigDecimal getLiquidSalary() 
    {
        return getLiquidSalary(getSubordinatesCount());
    }

    public BigDecimal getLiquidSalary(int subordinatesCount)
    {
        BigDecimal grossSalary = Salary == null ? BigDecimal.ZERO : Salary;

        BigDecimal discountedSalary = switch (ContractType) {
            case CLT -> grossSalary.multiply(BigDecimal.valueOf(0.70));
            case PJ -> grossSalary.multiply(BigDecimal.valueOf(0.90));
            default -> grossSalary;
        };

        BigDecimal managerBonus = switch (Role) {
            case Manager -> BigDecimal.valueOf(subordinatesCount).multiply(BigDecimal.valueOf(100));
            default -> BigDecimal.ZERO;
        };

        return discountedSalary.add(managerBonus);
    }

    public BigDecimal getGrossSalary() {
        return Salary;
    }

    public void setSalary(BigDecimal salary) {
        Salary = salary;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Employee other = (Employee) obj;
        return Id != null && Id.equals(other.Id);
    }

    
}
