package br.com.java.api.infrastructure.Repository;

import br.com.java.api.domain.entities.Employee;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	@EntityGraph(attributePaths = { "Manager" })
	Page<Employee> findAllBy(Pageable pageable);

	@Query("""
		select e.Manager.id as managerId, e.id as subordinateId
		from Employee e
		where e.Manager.id in :managerIds
		""")
	List<ManagerSubordinateView> findSubordinateLinksByManagerIds(@Param("managerIds") Collection<Long> managerIds);

	interface ManagerSubordinateView {
		Long getManagerId();
		Long getSubordinateId();
	}
}