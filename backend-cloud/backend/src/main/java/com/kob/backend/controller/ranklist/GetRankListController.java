package com.kob.backend.controller.ranklist;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.service.ranklist.GetRankListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GetRankListController {
    private final GetRankListService getRankListService;

    @Autowired
    public GetRankListController(GetRankListService getRankListService) {
        this.getRankListService = getRankListService;
    }

    @GetMapping("/api/ranklist")
    public JSONObject getRankList(@RequestParam Map<String,String> params){
        Integer pageNumber=Integer.parseInt(params.get("page_number"));
        Integer pageSize=Integer.parseInt(params.get("page_size"));
        return getRankListService.getRankList(pageNumber,pageSize);
    }
}
