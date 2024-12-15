package com.example.Service;

import com.example.Config.KafkaConsumer;
import com.example.DTO.EmployeeResponse;
import com.example.Model.Employee;
import com.example.Repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
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

            DecimalFormat df = new DecimalFormat("0.00");
            KafkaConsumer.expenses.stream().filter(item->item._1.equals(employeeResponse.getEmpno()))
                    .findFirst().ifPresent(item->{
                employeeResponse.setTotal(df.format(item._2));
            });
            response.add(employeeResponse);
        }
        return response;
    }


    public void save(Employee employee) {
        employeeRepository.save(employee);
    }

    public void update(Employee employeeDTO) {
        employeeRepository.save(employeeDTO);
    }

    public void delete(Integer id) {
        employeeRepository.deleteById(id);
    }
}
