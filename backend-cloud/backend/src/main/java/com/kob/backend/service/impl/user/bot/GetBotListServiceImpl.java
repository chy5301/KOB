package com.kob.backend.service.impl.user.bot;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserUtil;
import com.kob.backend.service.user.bot.GetBotListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetBotListServiceImpl implements GetBotListService {
    private final BotMapper botMapper;

    @Autowired
    public GetBotListServiceImpl(BotMapper botMapper) {
        this.botMapper = botMapper;
    }

    @Override
    public JSONObject getBotList() {
        User user = UserUtil.getLoggedinUser();

        JSONObject returnInfo = new JSONObject();

        QueryWrapper<Bot> botQueryWrapper = new QueryWrapper<>();
        botQueryWrapper.eq("user_id", user.getId());
        List<Bot> botList = botMapper.selectList(botQueryWrapper);

        returnInfo.put("status_message", "Success");
        returnInfo.put("bot_list", botList);
        return returnInfo;
    }
}
