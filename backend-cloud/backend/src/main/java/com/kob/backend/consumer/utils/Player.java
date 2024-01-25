package com.kob.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private Integer id;
    private Integer startX;
    private Integer startY;
    private List<Integer> steps;
    private List<Cell> cells;

    // 初始化 Player 并将起始位置加入 cells
    public Player(Integer id, Integer startX, Integer startY) {
        this.id = id;
        this.startX = startX;
        this.startY = startY;
        steps = new ArrayList<>();
        cells = new LinkedList<>();
        // 将起始位置加入 cells
        cells.add(new Cell(startX, startY));
    }

    // 更新 steps 和 cells
    public void nextStep(Integer nextStep) {
        // 将下一步操作添加到 steps
        steps.add(nextStep);
        // 更新 cells
        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        cells.add(new Cell(cells.getLast().x + dx[nextStep], cells.getLast().y + dy[nextStep]));
        if (checkTailNotIncreasing(steps.size()))
            cells.removeFirst();
    }

    // 检查当前回合蛇的长度是否需要增加，不需要增加返回 true，需要增加返回 false
    private boolean checkTailNotIncreasing(int step) {
        if (step <= 10)
            return false;
        return step % 3 != 1;
    }
}
