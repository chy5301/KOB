package com.kob.backend.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserMapper userMapper;

    // 查询所有用户信息
    @GetMapping("/user/all")
    public List<User> getAllUsers() {
        return userMapper.selectList(null);
    }

    // 按id查询用户信息
    @GetMapping("/user/{userId}")
    public User getUser(@PathVariable int userId) {
        // 进行复杂一些的查询需要使用条件构造器QueryWrapper
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("id", userId);
        return userMapper.selectOne(userQueryWrapper);

        // // 查询id大于等于2小于等于3的
        // userQueryWrapper.ge("id", 2).le("id", 3);
        // return userMapper.selectList(userQueryWrapper);
    }

    // 添加新的用户信息，一般使用@PostMapping，为了调试方便还是使用了@GetMapping
    @GetMapping("/user/add/{id}/{username}/{password}")
    public String addUser(
            @PathVariable int id,
            @PathVariable String username,
            @PathVariable String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);
        User newUser = new User(id, username, encodedPassword);
        userMapper.insert(newUser);
        return "Add user: " + newUser + " successfully.";
    }

    // 删除用户，一般也使用@PostMapping
    @GetMapping("/user/delete/{userId}")
    public String deleteUser(@PathVariable int userId) {
        userMapper.deleteById(userId);
        return "Delete user successfully.";
    }
}
