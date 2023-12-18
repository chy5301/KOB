package com.kob.backend.consumer.utils;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;


public class GameMapUtil {
    private final Integer size;
    private final Integer innerWallsCount;
    private final Block[][] gameMapBlocks;

    static class Block {
        public Point parentPoint;
        public boolean isWall;

        public Block(int row, int column) {
            parentPoint = new Point(row, column);
            isWall = false;
        }
    }

    public GameMapUtil(Integer size, Integer innerWallsCount) {
        this.size = size;
        this.innerWallsCount = innerWallsCount;
        this.gameMapBlocks = new Block[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                gameMapBlocks[i][j] = new Block(i, j);
    }

    // 绘制地图
    public void createGameMap() {
        Point startPoint = new Point(this.size - 2, 1);
        int rootCount = 0;
        int AttemptCount = 100000;
        // 创建墙体直到满足条件
        do {
            createWalls();
            rootCount = floodFill(startPoint);
            AttemptCount--;
        } while (rootCount > 1 && AttemptCount > 0);
    }

    // 返回代表墙体的int数组
    public int[][] getGameMapArray() {
        int[][] result = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (gameMapBlocks[i][j].isWall)
                    result[i][j] = 1;
                else
                    result[i][j] = 0;
            }
        }
        return result;
    }

    // 判断point点对应的block是否为并查集的根
    private boolean isRoot(Point point) {
        return gameMapBlocks[point.x][point.y].parentPoint.equals(point);
    }

    // 找到startPoint对应的block在并查集中的根
    private Point findRoot(Point startPoint) {
        Point point = startPoint.getLocation();
        while (!isRoot(point)) {
            point = gameMapBlocks[point.x][point.y].parentPoint.getLocation();
        }
        return point;
    }

    // 在并查集中合并firstPoint和secondPoint对应的block
    private void merge(Point firstPoint, Point secondPoint) {
        Point firstRootPoint = findRoot(firstPoint);
        Point secondRootPoint = findRoot(secondPoint);

        if (!firstRootPoint.equals(secondRootPoint))
            gameMapBlocks[secondRootPoint.x][secondRootPoint.y].parentPoint = firstRootPoint;
    }

    // 判断连通性
    private int floodFill(Point startPoint) {
        Point oldPoint, newPoint;
        Queue<Point> queue = new LinkedList<>();
        // 初始化方向
        int[] orient = {0, -1, 0, 1, 0};
        // 将起点加入队列
        queue.add(startPoint.getLocation());

        while (!queue.isEmpty()) {
            oldPoint = queue.remove();
            for (int i = 0; i < 4; i++) {
                int newX = oldPoint.x + orient[i];
                int newY = oldPoint.y + orient[i + 1];
                // 如果越界就跳过
                if (newX < 0 || newY < 0 || newX >= size || newY >= size)
                    continue;
                newPoint = new Point(newX, newY);
                // 如果是墙体或已合并，就跳过
                if (gameMapBlocks[newX][newY].isWall || !isRoot(newPoint))
                    continue;
                // 如果不是墙体，合并oldPoint和newPoint，并将newPoint加入队列
                merge(oldPoint, newPoint);
                queue.add(newPoint);
            }
        }
        // 统计根的数量
        int rootCount = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (gameMapBlocks[i][j].isWall)
                    continue;
                if (isRoot(new Point(i, j)))
                    rootCount++;
            }
        }
        return rootCount;
    }

    // 创建障碍物
    private void createWalls() {
        // 初始化blocks
        for (int x = 0; x < size; x++)
            for (int y = 0; y < size; y++)
                gameMapBlocks[x][y] = new Block(x, y);
        // 在地图边界设置墙
        for (int x = 0; x < size; x++) {
            gameMapBlocks[x][0].isWall = true;
            gameMapBlocks[x][size - 1].isWall = true;
        }
        for (int y = 0; y < size; y++) {
            gameMapBlocks[0][y].isWall = true;
            gameMapBlocks[size - 1][y].isWall = true;
        }
        // 创建随机障碍物（关于对角线对称）
        for (int i = 0; i < innerWallsCount / 2; i++) {
            for (int j = 0; j < 1000; j++) {
                Random random = new Random();
                int x = random.nextInt(size);
                int y = random.nextInt(size);
                // 如果随机到已经是墙的位置，就重新随机
                if (gameMapBlocks[x][y].isWall || gameMapBlocks[y][x].isWall)
                    continue;
                // 如果随机到边界，就重新随机
                if (x == size - 2 && y == 1 || x == 1 && y == size - 2)
                    continue;
                // 设置墙体
                gameMapBlocks[x][y].isWall = true;
                gameMapBlocks[y][x].isWall = true;
                break;
            }
        }
    }
}
