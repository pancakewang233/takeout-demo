package com.itheima.reggie.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

/**
 * @author 83443
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /*
    * 员工登录
    * @params {request}
    * @params {employee}
    * @return {Object}
    * */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        // * 1,将提交的密码md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));

        // * 2，根据页面提交的username查询数据库
        LambdaQueryWrapper<Employee> employeeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        employeeLambdaQueryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(employeeLambdaQueryWrapper);

        // * 3，如果没有查询到则返回登录失败结果
        if(emp == null){
            return R.error("登录失败");
        }

        // * 4，密码比对，如果不一致则返回登录失败结果
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        // * 5，查看员工状态，如果为已禁用，则返回员工已禁用结果
        if(emp.getStatus() == 0){
            return R.error("员工账号已禁用");
        }

        // * 6，登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /*
    * 员工退出
    * @params {request}
    * @return {String}
    * */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        /*
        * 1，清理session中保存的当前登录员工的id
        * */
        request.getSession().removeAttribute("employee");
        return R.success("推出成功");
    }

    /*
    * 新增员工
    * @params {employee}
    * @return {String}
    * */
    @PostMapping("/add")
    public R<String> save(@RequestBody Employee employee){

        // 初始密码123456，但是进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8)));
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /*
    * 员工信息分页查询
    *
    * */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){

        // 构造分页构造器
        Page pageInfo  = new Page(page, pageSize);

        // 构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        // 添加一个过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        // 添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        // 执行查询
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /*
    * 根据id修改员工信息
    * @params {employee}
    * @return {String}
    * */
    @PostMapping("/update")
    public R<String> update(@RequestBody Employee employee){
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /*
    * 根据id查员工信息
    * @params {id}
    * @return {Object}
    * */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }
}
