package com.kob.backend.service.impl.record;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import com.kob.backend.service.record.GetRecordInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetRecordInfoImpl implements GetRecordInfoService {
    private final RecordMapper recordMapper;
    private final UserMapper userMapper;

    @Autowired
    public GetRecordInfoImpl(RecordMapper recordMapper, UserMapper userMapper) {
        this.recordMapper = recordMapper;
        this.userMapper = userMapper;
    }

    @Override
    public JSONObject getRecordInfo(Integer recordId) {
        JSONObject returnInfo = new JSONObject();
        Record record = recordMapper.selectById(recordId);
        if (record == null) {
            returnInfo.put("status_message", "Exception");
            returnInfo.put("exception_message", "Record不存在");
        } else {
            User player1 = userMapper.selectById(record.getPlayer1Id());
            User player2 = userMapper.selectById(record.getPlayer2Id());
            JSONObject playersInfo = new JSONObject();
            playersInfo.put("player1_username", player1.getUsername());
            playersInfo.put("player1_photo", player1.getPhoto());
            playersInfo.put("player2_username", player2.getUsername());
            playersInfo.put("player2_photo", player2.getPhoto());

            returnInfo.put("status_message", "Success");
            returnInfo.put("record_info", record);
            returnInfo.put("players_info", playersInfo);
        }
        return returnInfo;
    }
}
