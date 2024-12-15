package com.bigdata.Project2.Service;

import com.bigdata.Project2.DTO.EmployeeResponse;
import com.bigdata.Project2.Model.Employee;
import com.bigdata.Project2.Repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<EmployeeResponse> getAllEmployeesWithDepartment() {
        List<Employee> test= employeeRepository.findAllEmployeesWithDepartment();
        
        List<EmployeeResponse> response = new ArrayList<EmployeeResponse>(test.size());
        for(int i=0;i<test.size();i++)
        {
            EmployeeResponse employeeResponse = new EmployeeResponse();
            employeeResponse.setComm(test.get(i).getComm());
            employeeResponse.setImg(test.get(i).getImg());
            employeeResponse.setEname(test.get(i).getEname());
            employeeResponse.setDeptno(test.get(i).getDepartment().getDeptno());
            employeeResponse.setEmpno(test.get(i).getEmpno());
            employeeResponse.setHiredate(test.get(i).getHiredate());
            employeeResponse.setJob(test.get(i).getJob());
            employeeResponse.setSal(test.get(i).getSal());
            employeeResponse.setMgr(test.get(i).getMgr());
            employeeResponse.setLoc(test.get(i).getDepartment().getLoc());
            employeeResponse.setDname(test.get(i).getDepartment().getDname());
            response.add(employeeResponse);
        }
        return response;
    }


    public void save(Employee employee) {

        employeeRepository.save(employee);
    }

    public void update(Employee employeeDTO) {

//        Employee employee = employeeRepository.findById(employee.getEmpno()).orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        employeeRepository.save(employeeDTO);
    }

    public void delete(Integer id) {

        employeeRepository.deleteById(id);
    }
}
