package com.kob.backend.service.record;

import com.alibaba.fastjson2.JSONObject;

public interface GetRecordInfoService {
    JSONObject getRecordInfo(Integer recordId);
}
