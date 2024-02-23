package com.kob.backend.consumer.utils;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CommonBeanProvider {
    public static RestTemplate restTemplate;
    public static BotMapper botMapper;
    public static RecordMapper recordMapper;
    public static UserMapper userMapper;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        CommonBeanProvider.restTemplate = restTemplate;
    }

    @Autowired
    public void setBotMapper(BotMapper botMapper) {
        CommonBeanProvider.botMapper = botMapper;
    }

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        CommonBeanProvider.recordMapper = recordMapper;
    }

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        CommonBeanProvider.userMapper = userMapper;
    }
}