package com.example.Controller;

import com.example.Config.KafkaConsumer;
import com.example.DTO.DataGenerator;
import com.example.Model.Department;
import com.example.Model.Employee;
import com.example.Repository.EmployeeRepository;
import com.example.Service.EmployeeService;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.jetbrains.annotations.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;


@CrossOrigin("*")
@Controller
public class EmployeeController {
    @Autowired
    private RestTemplate restTemplate;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;

    public EmployeeController(KafkaTemplate<String, String> kafkaTemplate, EmployeeRepository employeeRepository,EmployeeService employeeService) {
        this.kafkaTemplate = kafkaTemplate;
        this.employeeRepository = employeeRepository;
        this.employeeService=employeeService;
    }

    @GetMapping("/getUsers")
    public ResponseEntity<?> getUsers() {
        return new ResponseEntity<>(employeeService.getAllEmployeesWithDepartment(), HttpStatus.OK);
    }

    @PostMapping(value = "/send")
    public ResponseEntity<?> sendData()
    {
        RestTemplate restTemplate = new RestTemplate();
        String uri="https://ozmen-api-worker.senturk123.workers.dev/random";

        HttpEntity<String> entity = new HttpEntity<>("parameters");
        ResponseEntity<DataGenerator> result =
                restTemplate.exchange(uri, HttpMethod.GET, entity, DataGenerator.class);

        List<Integer> employeesID = employeeRepository.getAllEmployeesID();

        Random random = new Random();
        int randomNumber=random.nextInt(employeesID.size());

        try{
            kafkaTemplate.send("user_"+employeesID.get(randomNumber),result.getBody().toString()).get();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        return result;
    }

    @PostMapping("/submit")
    public String submitEmployee(
            @RequestParam String ename,
            @RequestParam String job,
            @RequestParam Integer mgr,
            @RequestParam String hiredate,
            @RequestParam Integer sal,
            @RequestParam(required = false) Integer comm,
            @RequestParam("img") MultipartFile img,
            @RequestParam Integer deptno,
            Model model
    ) throws IOException {
        String apiUrl = "http://localhost:8090/save-employee";

//        String imageURL= uploadImageToAPI(img);
        String response=restTemplate.postForObject(apiUrl, new Employee(comm,0,ename,job,mgr,hiredate,sal,img.getOriginalFilename(),new Department(deptno,null,null)), String.class);
        uploadFile(img);
        model.addAttribute("employees", employeeService.getAllEmployeesWithDepartment());
        return "redirect:/";
    }

    @GetMapping("/")
    public String getEmps(Model model) {
        sendData();
        model.addAttribute("employees", employeeService.getAllEmployeesWithDepartment());
        model.addAttribute("expenses", KafkaConsumer.expenses);

        return "index";
    }

    @GetMapping("/addEmployee")
    public String addEmp(Model model) {
        return "add";
    }

    @PostMapping("/save-employee")
    public String save(@RequestBody Employee employee ) {
        employeeService.save(employee);
        return "redirect:/";
    }

    @PutMapping("/update-employee")
    void update(@RequestBody Employee employee ) {

        employeeService.update(employee);
    }

    @DeleteMapping("/delete-employee/{id}")
    void delete(@PathVariable Integer id) {

        employeeService.delete(id);
    }

    public void uploadFile(MultipartFile file) throws IOException {
        String filename=file.getOriginalFilename();

        Configuration conf = new Configuration();
        conf.set("fs.defaultFS","hdfs://127.0.0.1:9000");

        try{
            FileSystem fs = FileSystem.get(conf);
            Path hdfsWritePath=new Path("/user/hadoop/images/"+filename);
            FSDataOutputStream fsDataOutputStream=fs.create(hdfsWritePath,true);
            InputStream inputStream=file.getInputStream();
            IOUtils.copyBytes(inputStream,fsDataOutputStream,0);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
