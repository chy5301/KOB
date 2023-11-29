package com.kob.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private UserMapper userMapper; // 用户数据访问对象

    @Autowired
    private PasswordEncoder passwordEncoder; // 密码编码器

    /**
     * 注册新用户
     *
     * @param username 用户名
     * @param password 密码
     * @param confirmPassword 确认密码
     * @return 注册结果信息
     */
    @Override
    public Map<String, String> register(String username, String password, String confirmPassword) {

        Map<String, String> returnInfo = new HashMap<>(); // 返回结果信息

        if (username == null) { // 判断用户名是否为空
            returnInfo.put("status_message", "Invalid input"); // 设置状态信息为无效输入
            returnInfo.put("exception_message", "用户名不能为空"); // 设置异常信息为用户名不能为空
            return returnInfo; // 返回结果信息
        }

        if (password == null || confirmPassword == null) { // 判断密码是否为空
            returnInfo.put("status_message", "Invalid input"); // 设置状态信息为无效输入
            returnInfo.put("exception_message", "密码不能为空"); // 设置异常信息为密码不能为空
            return returnInfo; // 返回结果信息
        }

        username = username.trim(); // 去除用户名两边的空格
        if (username.isEmpty()) { // 判断用户名是否为空
            returnInfo.put("status_message", "Invalid input"); // 设置状态信息为无效输入
            returnInfo.put("exception_message", "用户名不能为空"); // 设置异常信息为用户名不能为空
            return returnInfo; // 返回结果信息
        }

        if (password.isEmpty() || confirmPassword.isEmpty()) { // 判断密码是否为空
            returnInfo.put("status_message", "Invalid input"); // 设置状态信息为无效输入
            returnInfo.put("exception_message", "密码不能为空"); // 设置异常信息为密码不能为空
            return returnInfo; // 返回结果信息
        }

        if (username.length() > 100) { // 判断用户名长度是否超过限制
            returnInfo.put("status_message", "Invalid input"); // 设置状态信息为无效输入
            returnInfo.put("exception_message", "用户名长度不能大于100"); // 设置异常信息为用户名长度不能大于100
            return returnInfo; // 返回结果信息
        }

        if (password.length() > 100 || confirmPassword.length() > 100) { // 判断密码长度是否超过限制
            returnInfo.put("status_message", "Invalid input"); // 设置状态信息为无效输入
            returnInfo.put("exception_message", "密码长度不能大于100"); // 设置异常信息为密码长度不能大于100
            return returnInfo; // 返回结果信息
        }

        if (!password.equals(confirmPassword)) { // 判断密码和确认密码是否一致
            returnInfo.put("status_message", "Invalid input"); // 设置状态信息为无效输入
            returnInfo.put("exception_message", "两次输入的密码不一致"); // 设置异常信息为两次输入的密码不一致
            return returnInfo; // 返回结果信息
        }

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>(); // 查询条件
        userQueryWrapper.eq("username", username); // 设置查询字段和值
        List<User> users = userMapper.selectList(userQueryWrapper); // 查询用户名是否已存在
        if (!users.isEmpty()) { // 判断用户名是否已存在
            returnInfo.put("status_message", "Invalid input"); // 设置状态信息为无效输入
            returnInfo.put("exception_message", "用户名已存在"); // 设置异常信息为用户名已存在
            return returnInfo; // 返回结果信息
        }

        String encodedPassword = passwordEncoder.encode(password); // 编码密码
        String photoUrl = "https://cdn.acwing.com/media/user/profile/photo/319703_lg_e70ad07d8d.png"; // 用户头像URL
        User user = new User(null, username, encodedPassword, photoUrl); // 创建新用户对象
        userMapper.insert(user); // 插入用户数据到数据库
        returnInfo.put("status_message", "Success"); // 设置状态信息为成功
        return returnInfo; // 返回结果信息
    }
}