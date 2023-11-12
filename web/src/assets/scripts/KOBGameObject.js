const KOB_GAME_OBJECTS = [];

export class KOBGameObject {
    constructor() {
        KOB_GAME_OBJECTS.push(this);
        this.timeDelta = 0;
        this.hasCalledStart = false;
    }

    // 只执行一次
    start() {

    }

    // 除了第一帧之外，每一帧执行一次
    update() {

    }

    // 删除之前执行
    onDestroy() {

    }

    // 删除对象
    destroy() {
        this.onDestroy();

        for (let i in KOB_GAME_OBJECTS) {
            const obj = KOB_GAME_OBJECTS[i];
            if (obj === this) {
                KOB_GAME_OBJECTS.splice(i);
                break;
            }
        }
    }
}

// 上一次执行的时间
let lastTimestamp;
const step = timestamp => {

    // 遍历所有的KOB_GAME_OBJECTS对象
    for (let obj of KOB_GAME_OBJECTS) {
        // 如果该对象没有调用过start方法
        if (!obj.hasCalledStart) {
            // 设置hasCalledStart为true
            obj.hasCalledStart = true;
            // 调用对象的start方法
            obj.start();
        } else {
            // 如果该对象已经调用过start方法，则计算时间差
            obj.timeDelta = timestamp - lastTimestamp;
            // 调用对象的update方法
            obj.update();
        }
    }

    // 更新lastTimestamp为当前时间戳
    lastTimestamp = timestamp;
    // 请求动画帧，继续执行step函数
    requestAnimationFrame(step)
}


requestAnimationFrame(step)