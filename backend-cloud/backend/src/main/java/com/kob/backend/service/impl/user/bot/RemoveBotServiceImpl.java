package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserUtil;
import com.kob.backend.service.user.bot.RemoveBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class RemoveBotServiceImpl implements RemoveBotService {
    private final BotMapper botMapper;

    @Autowired
    public RemoveBotServiceImpl(BotMapper botMapper) {
        this.botMapper = botMapper;
    }

    @Override
    public Map<String, String> removeBot(Map<String, String> data) {
        User user = UserUtil.getLoggedinUser();

        int botId = Integer.parseInt(data.get("bot_id"));
        Bot bot = botMapper.selectById(botId);

        Map<String, String> returnInfo = new HashMap<>();

        if (bot == null) {
            returnInfo.put("status_message", "Exception");
            returnInfo.put("exception_message", "Bot不存在或已删除");
            return returnInfo;
        }

        if (!Objects.equals(bot.getUserId(), user.getId())) {
            returnInfo.put("status_message", "Exception");
            returnInfo.put("exception_message", "没有权限删除该Bot");
            return returnInfo;
        }

        botMapper.deleteById(bot.getId());
        returnInfo.put("status_message", "Success");
        return returnInfo;
    }
}
