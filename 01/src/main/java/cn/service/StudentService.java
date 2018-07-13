package cn.service;

import cn.model.Student;

public interface StudentService {

     int save ();
     void add(Student student);

    Student get(Integer id);
}
