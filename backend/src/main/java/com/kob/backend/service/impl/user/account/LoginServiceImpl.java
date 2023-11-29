package com.kob.backend.service.impl.user.account;

import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.account.LoginService;
import com.kob.backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 获取Token
     *
     * @param username 用户名
     * @param password 密码
     * @return 包含Token信息的Map对象
     */
    @Override
    public Map<String, String> getToken(String username, String password) {
        // 创建一个用于存储返回信息的Map
        Map<String, String> returnInfo = new HashMap<>();
        // 创建一个带有用户名和密码的UsernamePasswordAuthenticationToken对象
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        // 使用认证管理器对authenticationToken进行认证，如果认证失败会抛出异常
        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            // 返回异常信息
            returnInfo.put("status_message", "Exception");
            returnInfo.put("exception_class_name", e.getClass().getName());
            returnInfo.put("exception_message", e.getMessage());
            return returnInfo;
        }

        // 将认证后的用户信息转换为UserDetailsImpl对象并赋值给loginUser
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticate.getPrincipal();
        // 获取loginUser对应的User对象
        User user = loginUser.getUser();
        // 使用JwtUtil工具类创建JWT Token，传入user的id作为参数
        String jwtToken = JwtUtil.createJWT(user.getId().toString());

        // 返回token信息
        returnInfo.put("status_message", "Success");
        returnInfo.put("token", jwtToken);

        // 返回map对象
        return returnInfo;
    }
}
