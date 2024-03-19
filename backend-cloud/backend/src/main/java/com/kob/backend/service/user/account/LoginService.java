package com.kob.backend.service.user.account;

import com.alibaba.fastjson2.JSONObject;

public interface LoginService {
    JSONObject getToken(String username, String password) ;
}
