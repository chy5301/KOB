package com.kob.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/*
 待修改，
 可以将cells的计算变为每一步计算一次，
 整合到nextStep函数中，
 这样在WebSocketServer中的move函数也需要同步修改
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private Integer id;
    private Integer startX;
    private Integer startY;
    private List<Integer> steps;

    // 检查当前回合蛇的长度是否需要增加
    private boolean checkTailIncreasing(int step) {
        if (step <= 10)
            return true;
        return step % 3 == 1;
    }

    // 获取蛇，蛇头在List的尾部，蛇尾在List的头部
    public List<Cell> getCells() {
        List<Cell> result = new LinkedList<>();

        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        int x = startX, y = startY;
        int step = 0;
        result.add(new Cell(x, y));
        for (int d : steps) {
            x += dx[d];
            y += dy[d];
            result.add(new Cell(x, y));
            if (!checkTailIncreasing(++step)) {
                result.removeFirst();
            }
        }

        return result;
    }
}
