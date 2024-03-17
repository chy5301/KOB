package com.kob.backend.service.impl.user.bot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserUtil;
import com.kob.backend.service.user.bot.AddBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AddBotServiceImpl implements AddBotService {
    private final BotMapper botMapper;

    @Autowired
    public AddBotServiceImpl(BotMapper botMapper) {
        this.botMapper = botMapper;
    }

    @Override
    public Map<String, String> addBot(Map<String, String> data) {
        User user = UserUtil.getLoggedinUser();

        String title = data.get("title");
        String description = data.get("description");
        String content = data.get("content");

        Map<String, String> returnInfo = new HashMap<>();

        QueryWrapper<Bot> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
        Long botCount = botMapper.selectCount(queryWrapper);
        if (botCount >= 10) {
            returnInfo.put("status_message", "Exception");
            returnInfo.put("exception_message", "每个用户只能创建10个Bot");
            return returnInfo;
        }

        if (title == null || title.isEmpty()) {
            returnInfo.put("status_message", "Exception");
            returnInfo.put("exception_message", "标题不能为空");
            return returnInfo;
        }

        if (title.length() > 100) {
            returnInfo.put("status_message", "Exception");
            returnInfo.put("exception_message", "标题长度不能大于100");
            return returnInfo;
        }

        if (description == null || description.isEmpty()) {
            description = "这个用户很懒，什么也没写";
        }

        if (description.length() > 300) {
            returnInfo.put("status_message", "Exception");
            returnInfo.put("exception_message", "描述长度不能大于300");
            return returnInfo;
        }

        if (content == null || content.isEmpty()) {
            returnInfo.put("status_message", "Exception");
            returnInfo.put("exception_message", "代码不能为空");
            return returnInfo;
        }

        if (content.length() > 10000) {
            returnInfo.put("status_message", "Exception");
            returnInfo.put("exception_message", "代码长度不能大于10000");
            return returnInfo;
        }

        Date currentTime = new Date();
        Bot bot = new Bot(null, user.getId(), title, description, content, currentTime, currentTime);
        botMapper.insert(bot);
        returnInfo.put("status_message", "Success");
        return returnInfo;
    }
}
