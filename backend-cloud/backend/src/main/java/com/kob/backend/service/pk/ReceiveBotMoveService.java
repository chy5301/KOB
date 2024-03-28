package com.kob.backend.service.pk;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.util.MultiValueMap;

public interface ReceiveBotMoveService {
    JSONObject receiveBotMove(MultiValueMap<String, String> data);
}
