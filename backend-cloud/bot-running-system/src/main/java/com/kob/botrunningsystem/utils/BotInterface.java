package com.kob.botrunningsystem.utils;

import java.util.List;

public interface BotInterface {
    Integer nextStep(
            int[][] gameMap,
            Integer thisPlayerStartX,
            Integer thisPlayerStartY,
            List<Integer> thisPlayerSteps,
            Integer anotherPlayerStartX,
            Integer anotherPlayerStartY,
            List<Integer> anotherPlayerSteps
    );
}
