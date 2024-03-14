package com.kob.backend.controller.record;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.service.record.GetRecordInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GetRecordInfoController {
    private final GetRecordInfoService getRecordInfoService;

    @Autowired
    public GetRecordInfoController(GetRecordInfoService getRecordInfoService) {
        this.getRecordInfoService = getRecordInfoService;
    }

    @GetMapping("/record/getinfo")
    public JSONObject getRecordInfo(@RequestParam Map<String, String> params) {
        Integer recordId = Integer.parseInt(params.get("record_id"));
        return getRecordInfoService.getRecordInfo(recordId);
    }
}
