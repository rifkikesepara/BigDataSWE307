package com.example.Repository;

import com.example.Model.Employee;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends CrudRepository<Employee, Integer> {
    @Query("SELECT e FROM Employee e LEFT JOIN e.department d ON e.department.deptno = d.deptno WHERE d.deptno IS NOT NULL")
    List<Employee> findAllEmployeesWithDepartment();

    @Query("SELECT e.empno FROM Employee e")
    List<Integer> getAllEmployeesID();
}
