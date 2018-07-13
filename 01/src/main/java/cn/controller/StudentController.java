package cn.controller;


import cn.model.Student;
import cn.service.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(value = "学生接口", description = "学生增添该")
@RestController
public class StudentController {
    @Autowired
    private StudentService studentService;


    @PostMapping("/students")
    @ApiOperation(value = "添加学生")
    @ApiImplicitParam(name = "student", value = "学生",dataType = "Student")//如果name与respondy接收不一致则有两个接收
    public String add(@RequestBody Student student) {
        studentService.add(student);
        return "success";
    }
    @GetMapping("/students/{id}")
    @ApiOperation(value = "按Id查询")
    @ApiImplicitParam(name = "id", value = "学生id",paramType = "path",dataType = "Integer")
    public Student add1(@PathVariable(value = "id") Integer id) {
       return studentService.get(id);

    }
}
