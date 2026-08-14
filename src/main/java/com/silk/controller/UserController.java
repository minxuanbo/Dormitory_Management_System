package com.silk.controller;

import com.github.pagehelper.PageInfo;
import com.silk.entity.User;
import com.silk.service.UserService;
import com.silk.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * @author LindaSilk
 * @date 2021年3月03日, 周三
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("create")
    public Result create(@RequestBody User user){       // 映射到Json对象
        int flag = userService.create(user);
        if(flag>0){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @GetMapping("delete")
    public Result delete(String ids){
        int flag = userService.delete(ids);
        if(flag>0){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @PostMapping("update")
    public Result update(@RequestBody User user){
        int flag = userService.updateSelective(user);       // 选择性修改
        if(flag>0){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @PostMapping("update_pwd")
    public Result updatePwd(@RequestBody Map<String, Object> params){       // 个人中心修改密码：id/oldPwd/newPwd
        Integer id = Integer.valueOf(String.valueOf(params.get("id")));
        String oldPwd = (String) params.get("oldPwd");
        String newPwd = (String) params.get("newPwd");
        boolean flag = userService.updatePwd(id, oldPwd, newPwd);
        if(flag){
            return Result.ok();
        }else{
            return Result.fail("原密码不正确");
        }
    }

    @GetMapping("detail")
    public User detail(Integer id){
        return userService.detail(id);
    }

    @PostMapping("query")
    public Map<String,Object> query(User user){            // layui 表格等以表单参数请求
        PageInfo<User> pageInfo = userService.query(user);
        return Result.ok(pageInfo);
    }

}
