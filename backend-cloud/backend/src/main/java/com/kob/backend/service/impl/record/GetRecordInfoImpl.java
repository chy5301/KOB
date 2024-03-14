package com.kob.backend.service.impl.record;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.pojo.Record;
import com.kob.backend.service.record.GetRecordInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetRecordInfoImpl implements GetRecordInfoService {
    private final RecordMapper recordMapper;

    @Autowired
    public GetRecordInfoImpl(RecordMapper recordMapper) {
        this.recordMapper = recordMapper;
    }

    @Override
    public JSONObject getRecordInfo(Integer recordId) {
        JSONObject returnInfo = new JSONObject();
        Record record = recordMapper.selectById(recordId);
        if (record == null) {
            returnInfo.put("status_message", "Exception");
            returnInfo.put("exception_message", "Record不存在");
        } else {
            returnInfo.put("status_message", "Success");
            returnInfo.put("record_info", record);
        }
        return returnInfo;
    }
}
