import {KOBGameObject} from "@/assets/scripts/KOBGameObject";

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
    }

    // 只执行一次
    start() {

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
        // 每一帧新Map的边长
        this.updateSize();
        // 每帧渲染一次
        this.render();
    }

    // 渲染
    render() {
        this.ctx.fillStyle = "green";
        this.ctx.fillRect(0, 0, this.ctx.canvas.width, this.ctx.canvas.height);
    }
}