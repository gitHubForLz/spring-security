package cn.service;

import cn.mapper.StudentMapper;
import cn.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentMapper mapper;

    @Override
    public void add(Student student) {
        mapper.insert(student);
    }

    @Override
    public Student get(Integer id) {
        return mapper.selectByPrimaryKey(id);
    }

    @Override
    public int save (){
        return 101;
    }
}
