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
        this.direction = -1;
        // 蛇的状态，idle表示静止，moving表示正在移动，died表示死亡
        this.status = "idle";
        // 当前回合数
        this.step = 0;
        // 眼睛的方向，左下角的蛇眼睛的初始方向朝上，0、1、2、3表示上右下左
        this.eyesDirection = 0;
        // 右上角的蛇，眼睛的初始方向朝下
        if (this.id === 1)
            this.eyesDirection = 2;

        // 四个方向行列的偏移量
        this.dr = [-1, 0, 1, 0];
        this.dc = [0, 1, 0, -1];
        // 四个方向蛇眼的x，y偏移量，上右下左
        this.eyesDx = [
            [-1, 1],
            [1, 1],
            [1, -1],
            [-1, -1],
        ];
        this.eyesDy = [
            [-1, -1],
            [-1, 1],
            [1, 1],
            [1, -1],
        ];
        // 蛇移动时允许的判定误差
        this.eps = 1e-2;
    }

    start() {

    }

    // 每一帧执行一次
    update() {
        // 如果是在移动状态，就移动蛇
        if (this.status === "moving")
            this.updateMove();
        this.render();
    }

    // 移动
    updateMove() {
        // 每两帧走过的距离
        const moveDistance = this.speed * this.timeDelta / 1000;
        const dx = this.nextCell.x - this.cells[0].x;
        const dy = this.nextCell.y - this.cells[0].y;
        const distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < this.eps) {
            // 如果移动到了目标点，就停下，并把目标点作为新的头存下来
            this.cells[0] = this.nextCell;
            this.nextCell = null;
            this.status = "idle";

            // 如果移动到了目标点且蛇不需要变长，就砍掉尾部
            if (!this.checkTailIncreasing())
                this.cells.pop();
        } else {
            // 如果需要移动，就改变舌头的坐标来进行移动
            this.cells[0].x += moveDistance * dx / distance;
            this.cells[0].y += moveDistance * dy / distance;

            // 如果蛇不需要变长，就移动尾部
            if (!this.checkTailIncreasing()) {
                // 找到需要移动的尾部和目标位置
                const cellCount = this.cells.length;
                const tail = this.cells[cellCount - 1];
                const tailTarget = this.cells[cellCount - 2];
                // 计算需要移动的距离
                const tailDx = tailTarget.x - tail.x;
                const tailDy = tailTarget.y - tail.y;
                tail.x += moveDistance * tailDx / distance;
                tail.y += moveDistance * tailDy / distance;
            }
        }
    }

    // 设置方向
    setDirection(direction) {
        this.direction = direction;
    }

    // 准备好进行下一回合的移动后更新蛇的状态
    nextStep() {
        const direction = this.direction;
        this.nextCell = new Cell(this.cells[0].r + this.dr[direction], this.cells[0].c + this.dc[direction]);
        // 每次下一步时更新眼睛的方向
        this.eyesDirection = direction;
        // 清空操作
        this.direction = -1;
        // 状态从静止切换为移动
        this.status = "moving";
        // 回合数增加
        this.step++;

        const cellCount = this.cells.length;
        // 每个cell向后复制并移动一位，这样就多出了一个头来
        for (let i = cellCount; i > 0; i--)
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i - 1]));
    }

    // 检查当前回合蛇的长度是否需要增加
    checkTailIncreasing() {
        if (this.step <= 10)
            return true;
        return this.step % 3 === 1;
    }

    render() {
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;

        if (this.status === "died")
            this.color = "white";

        // 设置颜色
        ctx.fillStyle = this.color;
        for (const cell of this.cells) {
            // 画圆
            ctx.beginPath();
            // 半径使用0.9倍L防止蛇太粗不美观
            ctx.arc(cell.x * L, cell.y * L, L * 0.9 / 2, 0, Math.PI * 2);
            ctx.fill();
        }

        // 填充蛇的躯体之间的空隙
        for (let i = 1; i < this.cells.length; i++) {
            const front = this.cells[i - 1];
            const back = this.cells[i];

            // // 如果两个点重合就跳过
            // if (Math.abs(front.x - back.x) < this.eps && Math.abs(front.y - back.y) < this.eps)
            //     continue;
            // 如果两段躯体的x坐标相等（竖直排列）
            if (Math.abs(front.x - back.x) < this.eps)
                ctx.fillRect((front.x - 0.45) * L, Math.min(front.y, back.y) * L, L * 0.9, Math.abs(front.y - back.y) * L);
            // 如果两段躯体的y坐标相等（横向排列）
            else
                ctx.fillRect(Math.min(front.x, back.x) * L, (front.y - 0.45) * L, Math.abs(front.x - back.x) * L, L * 0.9);
        }

        // 绘制蛇眼
        ctx.fillStyle = "black";
        // 眼睛大小比例
        const eyesSize = 0.05;
        const eyesDRate = 0.15;
        for (let i = 0; i < 2; i++) {
            const eyeX = (this.cells[0].x + this.eyesDx[this.eyesDirection][i] * eyesDRate) * L;
            const eyeY = (this.cells[0].y + this.eyesDy[this.eyesDirection][i] * eyesDRate) * L;
            ctx.beginPath();
            ctx.arc(eyeX, eyeY, L * eyesSize, 0, Math.PI * 2);
            ctx.fill();
        }
    }
}