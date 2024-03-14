package com.kob.backend.controller.record;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.service.record.GetRecordListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GetRecordListController {
    private final GetRecordListService getRecordListService;

    @Autowired
    public GetRecordListController(GetRecordListService getRecordListService) {
        this.getRecordListService = getRecordListService;
    }

    @GetMapping("/record/getlist")
    public JSONObject getRecordList(@RequestParam Map<String, String> params) {
        Integer pageNumber = Integer.parseInt(params.get("page_number"));
        Integer pageSize = Integer.parseInt(params.get("page_size"));
        return getRecordListService.getRecordList(pageNumber, pageSize);
    }
}
