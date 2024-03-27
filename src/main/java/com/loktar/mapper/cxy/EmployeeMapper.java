package com.loktar.mapper.cxy;

import com.loktar.domain.cxy.Employee;
import java.util.List;

public interface EmployeeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Employee row);

    Employee selectByPrimaryKey(Integer id);

    List<Employee> selectAll();

    int updateByPrimaryKey(Employee row);

    List<Employee> getNeedNoticeEmployees();

}