package com.kob.backend.service.impl.ranklist;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.ranklist.GetRankListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetRankListServiceImpl implements GetRankListService {
    private final UserMapper userMapper;

    @Autowired
    public GetRankListServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public JSONObject getRankList(Integer pageNumber, Integer pageSize) {
        IPage<User> userIPage = new Page<>(pageNumber, pageSize);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("rating");
        List<User> userList = userMapper.selectPage(userIPage, queryWrapper).getRecords();
        List<JSONObject> userInfos = new ArrayList<>();
        for (User user : userList) {
            JSONObject userInfo = new JSONObject();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("photo", user.getPhoto());
            userInfo.put("rating", user.getRating());
            userInfos.add(userInfo);
        }
        JSONObject returnInfo = new JSONObject();
        returnInfo.put("status_message", "Success");
        returnInfo.put("users", userInfos);
        returnInfo.put("users_count", userMapper.selectCount(null));
        return returnInfo;
    }
}
