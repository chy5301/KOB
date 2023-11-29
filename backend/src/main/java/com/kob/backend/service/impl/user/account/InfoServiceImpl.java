package com.kob.backend.service.impl.user.account;

import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.account.InfoService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class InfoServiceImpl implements InfoService {
    /**
     * 获取信息的方法
     *
     * @return 包含消息、id、用户名和照片的Map对象
     */
    @Override
    public Map<String, String> getInfo() {

        // 获取当前认证对象
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        // 获取当前登录用户的详情
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        // 获取用户对象
        User user = loginUser.getUser();

        // 创建返回信息的Map对象
        Map<String, String> returnInfo = new HashMap<>();
        // 设置消息为"success"
        returnInfo.put("status_message", "Success");
        // 设置id
        returnInfo.put("id", user.getId().toString());
        // 设置用户名
        returnInfo.put("username", user.getUsername());
        // 设置照片
        returnInfo.put("photo", user.getPhoto());

        // 返回包含信息的Map对象
        return returnInfo;
    }
}

