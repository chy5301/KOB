import {KOBGameObject} from "@/assets/scripts/KOBGameObject";
import {Wall} from "@/assets/scripts/Wall";

export class GameMap extends KOBGameObject {
    constructor(ctx, parent) {
        super();

        // 画布
        this.ctx = ctx;
        // 用来动态修改画布的长宽
        this.parent = parent;
        // 每个格子的单位长度（绝对距离）
        this.L = 0;
        // 行数
        this.rows = 13;
        // 列数
        this.cols = 13;
        // 墙体
        this.walls = [];
    }

    // 创建障碍物
    createWalls() {
        const g = [];
        for (let r = 0; r < this.rows; r++) {
            g[r] = [];
            for (let c = 0; c < this.cols; c++)
                g[r][c] = false;
        }

        // 在地图边界设置墙
        for (let r = 0; r < this.rows; r++)
            g[r][0] = g[r][this.cols - 1] = true;
        for (let c = 0; c < this.cols; c++)
            g[0][c] = g[this.rows - 1][c] = true;

        // 渲染所有的墙
        for (let r = 0; r < this.rows; r++)
            for (let c = 0; c < this.cols; c++)
                if (g[r][c])
                    this.walls.push(new Wall(r, c, this));
    }


    // 只执行一次
    start() {
        this.createWalls();
    }

    // 更新Map的边长
    updateSize() {
        // 求格子单位长度
        this.L = Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows);
        // 计算实际宽度
        this.ctx.canvas.width = this.L * this.cols;
        // 计算实际高度
        this.ctx.canvas.height = this.L * this.rows;
    }

    // 除了第一帧之外，每一帧执行一次
    update() {
        // 每一帧计算新的Map边长
        this.updateSize();
        // 每帧渲染一次
        this.render();
    }

    // 渲染
    render() {
        // 定义偶数格子的颜色
        const colorEven = "#aad751";
        // 定义奇数格子的颜色
        const colorOdd = "#a2d149";
        // 循环绘制每一行的格子
        for (let r = 0; r < this.rows; r++) {
            // 循环绘制每一列的格子
            for (let c = 0; c < this.cols; c++) {
                // 判断当前格子是偶数格子还是奇数格子并设置对应的颜色
                if ((r + c) % 2 === 0) {
                    this.ctx.fillStyle = colorEven;
                } else {
                    this.ctx.fillStyle = colorOdd;
                }
                // 绘制格子
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L);
            }
        }
    }

}