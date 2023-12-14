package com.kob.backend.consumer.utils;

public class GameUtil {
    private final GameMapUtil gameMap;

    public GameUtil(Integer size, Integer innerWallsCount) {
        gameMap = new GameMapUtil(size, innerWallsCount);
    }

    public void createMap() {
        gameMap.createGameMap();
    }

    public int[][] getMap() {
        return gameMap.getGameMapArray();
    }

}
