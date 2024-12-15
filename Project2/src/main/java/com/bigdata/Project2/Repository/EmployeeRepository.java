package com.bigdata.Project2.Repository;

import com.bigdata.Project2.Model.Employee;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends CrudRepository<Employee, Integer> {
    @Query("SELECT e FROM Employee e LEFT JOIN e.department d ON e.department.deptno = d.deptno WHERE d.deptno IS NOT NULL")
    List<Employee> findAllEmployeesWithDepartment();
}
