package com.kob.backend.service.impl.record;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import com.kob.backend.service.record.GetRecordListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetRecordListServiceImpl implements GetRecordListService {
    private final RecordMapper recordMapper;
    private final UserMapper userMapper;

    @Autowired
    public GetRecordListServiceImpl(RecordMapper recordMapper, UserMapper userMapper) {
        this.recordMapper = recordMapper;
        this.userMapper = userMapper;
    }

    @Override
    public JSONObject getRecordList(Integer pageNumber, Integer pageSize) {
        IPage<Record> recordIPage = new Page<>(pageNumber, pageSize);
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        List<Record> records = recordMapper.selectPage(recordIPage, queryWrapper).getRecords();

        List<JSONObject> items = new ArrayList<>();
        for (Record record : records) {
            User user1 = userMapper.selectById(record.getPlayer1Id());
            User user2 = userMapper.selectById(record.getPlayer2Id());
            JSONObject item = new JSONObject();
            item.put("user1_photo", user1.getPhoto());
            item.put("user1_username", user1.getUsername());
            item.put("user2_photo", user2.getPhoto());
            item.put("user2_username", user2.getUsername());
            String result = "平局";
            if ("player1".equals(record.getLoser()))
                result = "player1胜";
            else if ("player2".equals(record.getLoser()))
                result = "player2胜";
            item.put("result", result);
            item.put("record", record);
            items.add(item);
        }
        JSONObject returnInfo = new JSONObject();
        returnInfo.put("records", items);
        // 数据库中record总数
        returnInfo.put("records_count", recordMapper.selectCount(null));
        return returnInfo;
    }
}
