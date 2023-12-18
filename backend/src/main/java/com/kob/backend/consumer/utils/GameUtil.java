package com.kob.backend.consumer.utils;

import lombok.Getter;

import java.util.ArrayList;

public class GameUtil {
    private final GameMapUtil gameMap;
    @Getter
    private final Player player1;
    @Getter
    private final Player player2;

    public GameUtil(Integer size, Integer innerWallsCount, Integer player1Id, Integer player2Id) {
        gameMap = new GameMapUtil(size, innerWallsCount);
        player1 = new Player(player1Id, size - 2, 1, new ArrayList<>());
        player2 = new Player(player2Id, 1, size - 2, new ArrayList<>());
    }

    public void createMap() {
        gameMap.createGameMap();
    }

    public int[][] getMap() {
        return gameMap.getGameMapArray();
    }
}
