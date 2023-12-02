package com.kob.backend.service.impl.user.bot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserUtil;
import com.kob.backend.service.user.bot.GetBotListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GetBotListServiceImpl implements GetBotListService {

    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, Object> getBotList() {
        User user= UserUtil.getLoggedinUser();

        Map<String, Object> returnInfo = new HashMap<>();

        QueryWrapper<Bot> botQueryWrapper = new QueryWrapper<>();
        botQueryWrapper.eq("user_id", user.getId());
        List<Bot> botList = botMapper.selectList(botQueryWrapper);

        returnInfo.put("status_message", "Success");
        returnInfo.put("bot_list", botList);
        return returnInfo;
    }
}
