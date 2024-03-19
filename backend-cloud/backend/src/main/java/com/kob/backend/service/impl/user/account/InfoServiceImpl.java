package com.kob.backend.service.impl.user.account;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserUtil;
import com.kob.backend.service.user.account.InfoService;
import org.springframework.stereotype.Service;

@Service
public class InfoServiceImpl implements InfoService {
    /**
     * 获取信息的方法
     *
     * @return 包含消息、id、用户名和照片的JSONObject
     */
    @Override
    public JSONObject getInfo() {
        // 获取当前登录用户
        User user= UserUtil.getLoggedinUser();

        // 创建返回信息的JSONObject
        JSONObject returnInfo=new JSONObject();
        // 设置消息为"success"
        returnInfo.put("status_message", "Success");
        // 设置id
        returnInfo.put("id", user.getId().toString());
        // 设置用户名
        returnInfo.put("username", user.getUsername());
        // 设置照片
        returnInfo.put("photo", user.getPhoto());

        // 返回包含信息的JSONObject
        return returnInfo;
    }
}

