import {KOBGameObject} from "@/assets/scripts/KOBGameObject";
import {Cell} from "@/assets/scripts/Cell";

export class Snake extends KOBGameObject {
    constructor(info, gamemap) {
        super();

        this.id = info.id;
        this.color = info.color;
        this.gamemap = gamemap;
        // 存放蛇的身体，cells[0]存放蛇头
        this.cells = [new Cell(info.r, info.c)];
        // 下一步的目标位置
        this.nextCell = null;
        // 蛇的速度，每秒钟行动的格子数量
        this.speed = 5;
        // 下一步指令，-1表示没有指令，0、1、2、3表示上右下左
        this.deriction = -1;
        // 蛇的状态，idle表示静止，moving表示正在移动，died表示死亡
        this.status = "idle";
        // 当前回合数
        this.step = 0;

        // 四个方向行列的偏移量
        this.dr = [-1, 0, 1, 0];
        this.dc = [0, 1, 0, -1];
    }

    start() {

    }

    update() {
        this.updateMove();
        this.render();
    }

    updateMove() {
        this.cells[0].x += this.speed * this.timeDelta / 1000;
    }

    // 准备好进行下一回合的移动后更新蛇的状态
    nextStep() {
        const d = this.deriction;
        this.nextCell = new Cell(this.cells[0] + this.dr[d], this.cells[0] + this.dc[d]);
        // 清空操作
        this.deriction = -1;
        // 状态从静止切换为移动
        this.status = "moving";
        // 回合数增加
        this.step++;
    }

    render() {
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;

        // 设置颜色
        ctx.fillStyle = this.color;
        for (const cell of this.cells) {
            // 画圆
            ctx.beginPath();
            ctx.arc(cell.x * L, cell.y * L, L / 2, 0, Math.PI * 2);
            ctx.fill();
        }

    }
}