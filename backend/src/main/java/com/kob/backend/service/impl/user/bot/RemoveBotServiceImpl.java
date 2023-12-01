package com.kob.backend.service.impl.user.bot;

import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.RemoveBotService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RemoveBotServiceImpl implements RemoveBotService {
    @Override
    public Map<String, String> removeBot(Map<String, String> data) {


        return null;
    }
}
