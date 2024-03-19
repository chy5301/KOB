package com.kob.backend.service.user.account;

import com.alibaba.fastjson2.JSONObject;

public interface RegisterService {
    JSONObject register(String username, String password, String confirmPassword);
}
