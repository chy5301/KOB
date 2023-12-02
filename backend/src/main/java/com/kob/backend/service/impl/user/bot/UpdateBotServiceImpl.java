package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.UpdateBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class UpdateBotServiceImpl implements UpdateBotService {

    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> updateBot(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        int botId = Integer.parseInt(data.get("bot_id"));
        String title = data.get("title");
        String description = data.get("description");
        String content = data.get("content");
        Bot bot = botMapper.selectById(botId);

        Map<String, String> returnInfo = new HashMap<>();

        if (bot == null) {
            returnInfo.put("status_message", "Exception");
            returnInfo.put("exception_message", "Bot不存在或已删除");
            return returnInfo;
        }

        if (!Objects.equals(bot.getUserId(), user.getId())) {
            returnInfo.put("status_message", "Exception");
            returnInfo.put("exception_message", "没有权限修改该Bot");
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
        Bot newBot = new Bot(
                bot.getId(),
                bot.getUserId(),
                title,
                description,
                content,
                bot.getRating(),
                bot.getCreateTime(),
                currentTime
        );
        botMapper.updateById(newBot);
        returnInfo.put("status_message", "Success");
        return returnInfo;
    }
}
