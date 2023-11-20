import {KOBGameObject} from "@/assets/scripts/KOBGameObject";
import {Wall} from "@/assets/scripts/Wall";

// 坐标点
class Point {
    constructor(r, c) {
        this.r = r;
        this.c = c;
    }

    equals(other) {
        return this.r === other.r && this.c === other.c;
    }
}

// 区块
class Block {
    constructor(r, c) {
        this.parentPoint = new Point(r, c);
        this.isWall = false;
    }
}

export class GameMap extends KOBGameObject {
    constructor(ctx, parent) {
        super();

        // 画布
        this.ctx = ctx;
        // 用来动态修改画布的长宽
        this.parent = parent;
        // 每个格子的单位长度（绝对距离）
        this.L = 0;
        // 行列数
        this.size = 16;
        // 内部墙体半数（为了保证地图公平，以对角线为分界进行随机分配，所以只随机一般）
        this.innerWallsCount = 48;
        // 墙体
        this.walls = [];
        // 所有区块
        this.blocks = [];
        for (let r = 0; r < this.size; r++) {
            this.blocks[r] = [];
            for (let c = 0; c < this.size; c++)
                this.blocks[r][c] = new Block(r, c);
        }
    }

    // 判断point点对应的block的是否为并查集的根
    isRoot(point) {
        return this.blocks[point.r][point.c].parentPoint.equals(point);
    }

    // 找到start点对应的block在并查集中的根
    findRoot(start) {
        let point = new Point(start.r, start.c);
        while (!this.isRoot(point)) {
            point = this.blocks[point.r][point.c].parentPoint;
        }
        return point;
    }

    // 在并查集中合并point1点和point2点对应的block
    merge(point1, point2) {
        const rootPoint1 = this.findRoot(point1);
        const rootPoint2 = this.findRoot(point2);

        if (rootPoint1.equals(rootPoint2))
            return;
        this.blocks[rootPoint2.r][rootPoint2.c].parentPoint = rootPoint1;
    }

    // 创建障碍物
    createWalls() {
        // 初始化blocks
        for (let r = 0; r < this.size; r++)
            for (let c = 0; c < this.size; c++)
                this.blocks[r][c] = new Block(r, c);
        // 在地图边界设置墙
        for (let r = 0; r < this.size; r++)
            this.blocks[r][0].isWall = this.blocks[r][this.size - 1].isWall = true;
        for (let c = 0; c < this.size; c++)
            this.blocks[0][c].isWall = this.blocks[this.size - 1][c].isWall = true;

        // 创建随机障碍物（关于对角线对称）
        for (let i = 0; i < this.innerWallsCount / 2; i++)
            for (let j = 0; j < 1000; j++) {
                let r = Math.floor(Math.random() * this.size);
                let c = Math.floor(Math.random() * this.size);
                // 如果随机到已经是墙的位置，就重新随机
                if (this.blocks[r][c].isWall || this.blocks[c][r].isWall)
                    continue;
                // 如果随机到边界，就重新随机
                if (r === this.size - 2 && c === 1 || r === 1 && c === this.size - 2)
                    continue;
                this.blocks[r][c].isWall = this.blocks[c][r].isWall = true;
                break;
            }
    }

    floodFill(start) {
        let oldPoint, newPoint;
        const queue = [];
        // 初始化方向
        const orient = [0, -1, 0, 1, 0];
        // 将起点加入队列
        queue.push(new Point(start.r, start.c));

        while (queue.length > 0) {
            oldPoint = queue.shift();
            // 遍历四个方向
            for (let i = 0; i < 4; i++) {
                const newR = oldPoint.r + orient[i];
                const newC = oldPoint.c + orient[i + 1];
                // 如果越界就跳过
                if (newR < 0 || newC < 0 || newR === this.size || newC === this.size)
                    continue;


                newPoint = new Point(newR, newC);
                // 如果是墙体或已经被合并，就跳过
                if (this.blocks[newR][newC].isWall || !this.isRoot(newPoint))
                    continue;

                // 如果不是墙体，合并oldPoint和newPoint，并将newPoint加入队列
                this.merge(oldPoint, newPoint);
                queue.push(newPoint);
            }
        }

        // 统计根的数量
        let rootCount = 0;
        for (let i = 0; i < this.size; i++)
            for (let j = 0; j < this.size; j++) {
                if (this.blocks[i][j].isWall)
                    continue;
                if (this.isRoot(new Point(i, j)))
                    rootCount++;
            }
        return rootCount;
    }

    // 将所有的墙添加到walls[]并初始化（到Object列表中）
    pushWalls() {
        for (let r = 0; r < this.size; r++)
            for (let c = 0; c < this.size; c++)
                if (this.blocks[r][c].isWall)
                    this.walls.push(new Wall(r, c, this));
    }

    // 只在启动时执行一次
    start() {
        // 以左下角为起点
        const startPoint = new Point(this.size - 2, 1);
        let rootCount;
        // 创建墙体直到满足条件
        do {
            this.createWalls();
            rootCount = this.floodFill(startPoint);
        } while (rootCount > 1);

        this.pushWalls();
    }

    // 更新Map的边长
    updateSize() {
        // 求格子单位长度
        this.L = Math.floor(Math.min(this.parent.clientWidth / this.size, this.parent.clientHeight / this.size));
        // 计算实际宽度
        this.ctx.canvas.width = this.L * this.size;
        // 计算实际高度
        this.ctx.canvas.height = this.L * this.size;
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
        for (let r = 0; r < this.size; r++) {
            // 循环绘制每一列的格子
            for (let c = 0; c < this.size; c++) {
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