package com.kob.botrunningsystem.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class Bot implements Supplier<Integer> {
    private final int[][] gameMap;
    private final Integer thisPlayerStartX;
    private final Integer thisPlayerStartY;
    private final List<Integer> thisPlayerSteps;
    private final Integer anotherPlayerStartX;
    private final Integer anotherPlayerStartY;
    private final List<Integer> anotherPlayerSteps;

    public Bot(
            int[][] gameMap,
            Integer thisPlayerStartX,
            Integer thisPlayerStartY,
            List<Integer> thisPlayerSteps,
            Integer anotherPlayerStartX,
            Integer anotherPlayerStartY,
            List<Integer> anotherPlayerSteps
    ) {
        this.gameMap = gameMap;
        this.thisPlayerStartX = thisPlayerStartX;
        this.thisPlayerStartY = thisPlayerStartY;
        this.thisPlayerSteps = thisPlayerSteps;
        this.anotherPlayerStartX = anotherPlayerStartX;
        this.anotherPlayerStartY = anotherPlayerStartY;
        this.anotherPlayerSteps = anotherPlayerSteps;
    }

    private static final int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};

    private static class Cell {
        public int x;
        public int y;

        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private boolean checkTailNotIncreasing(int step) {
        if (step <= 10)
            return false;
        return step % 3 != 1;
    }

    private List<Cell> getCells(int startX, int startY, List<Integer> steps) {
        List<Cell> cells = new LinkedList<>();


        int x = startX, y = startY;
        int step = 0;
        cells.add(new Cell(x, y));
        for (int direction : steps) {
            x += dx[direction];
            y += dy[direction];
            cells.add(new Cell(x, y));
            if (checkTailNotIncreasing(++step)) {
                cells.removeFirst();
            }
        }

        return cells;
    }

    @Override
    public Integer get() {
        List<Cell> thisPlayerCells = getCells(thisPlayerStartX, thisPlayerStartY, thisPlayerSteps);
        List<Cell> anotherPlayerCells = getCells(anotherPlayerStartX, anotherPlayerStartY, anotherPlayerSteps);

        for (Cell cell : thisPlayerCells) {
            gameMap[cell.x][cell.y] = 1;
        }
        for (Cell cell : anotherPlayerCells) {
            gameMap[cell.x][cell.y] = 1;
        }

        Integer direction = 0;
        List<Integer> validDirection = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            int x = thisPlayerCells.getLast().x + dx[i];
            int y = thisPlayerCells.getLast().y + dy[i];
            if (x >= 0 && x < gameMap[0].length && y >= 0 && y < gameMap.length && gameMap[x][y] == 0) {
                validDirection.add(i);
            }
        }
        if (!validDirection.isEmpty()) {
            Random random = new Random();
            return validDirection.get(random.nextInt(validDirection.size()));
        }

        return direction;
    }
}
