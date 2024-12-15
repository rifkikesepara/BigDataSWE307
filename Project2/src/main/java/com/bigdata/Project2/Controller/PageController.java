package com.bigdata.Project2.Controller;

import com.bigdata.Project2.Model.Department;
import com.bigdata.Project2.Model.Employee;
import com.bigdata.Project2.Service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class PageController {
    private final EmployeeService employeeService;

    public PageController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    @GetMapping("/table")
    String greetingPage(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployeesWithDepartment());
        return "home";
    }

    @PostMapping("/submit")
    public String submitEmployee(
            @RequestParam String ename,
            @RequestParam String job,
            @RequestParam Integer mgr,
            @RequestParam String hiredate,
            @RequestParam Integer sal,
            @RequestParam(required = false) Integer comm,
            @RequestParam("img") String img,
            @RequestParam Integer deptno,
            Model model
    ) {
        String apiUrl = "http://localhost:8080/save-employee";

//        String imageURL= uploadImageToAPI(img);
        String response=restTemplate.postForObject(apiUrl, new Employee(comm,0,ename,job,mgr,hiredate,sal,img,new Department(deptno,null,null)), String.class);

        model.addAttribute("response",response);
        model.addAttribute("employees", employeeService.getAllEmployeesWithDepartment());
        return "home";
    }

    @PostMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Integer id, Model model) {
        String apiUrl = "http://localhost:8080/delete-employee/"+id;

        restTemplate.delete(apiUrl);
        model.addAttribute("employees", employeeService.getAllEmployeesWithDepartment());
        return "home";
    }

    private String uploadImageToAPI(MultipartFile img)
    {
        if(img.isEmpty())
            return null;
        try{
            String url = "https://ozmen.s3.eu-central-1.amazonaws.com/";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);


            InputStreamResource resource=new InputStreamResource(img.getInputStream()){
                @Override
                public long contentLength()
                {
                    return img.getSize();
                }
                @Override
                public String getFilename()
                {
                    return img.getOriginalFilename();
                }
            };
            headers.setContentLength(img.getSize());


            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            System.out.println(img);
            body.add("key", img.getOriginalFilename());
            body.add("file", resource);

            byte[] test=(img.getBytes());
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            return img.getOriginalFilename();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    @Autowired
    private RestTemplate restTemplate;



}
