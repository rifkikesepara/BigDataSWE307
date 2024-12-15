package com.bigdata.Project2.Controller;

import com.bigdata.Project2.DTO.EmployeeResponse;
import com.bigdata.Project2.Model.Employee;
import com.bigdata.Project2.Repository.EmployeeRepository;
import com.bigdata.Project2.Service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
public class EmployeeController {
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeRepository employeeRepository, EmployeeService employeeService) {
        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
    }

    @GetMapping("/employees")
    public ResponseEntity<Iterable<Employee>> getEmployees() {
        return new ResponseEntity<>(employeeRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesandDept() {
        return new ResponseEntity<>(employeeService.getAllEmployeesWithDepartment(), HttpStatus.OK);
    }

    @PostMapping("/save-employee")
    void save(@RequestBody Employee employee ) {

        employeeService.save(employee);
    }

    @PutMapping("/update-employee")
    void update(@RequestBody Employee employee ) {

        employeeService.update(employee);
    }

    @DeleteMapping("/delete-employee/{id}")
    void delete(@PathVariable Integer id) {

        employeeService.delete(id);
    }

}
