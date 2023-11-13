import {KOBGameObject} from "@/assets/scripts/KOBGameObject";

export class Wall extends KOBGameObject {
    constructor(r, c, gameMap) {
        super();

        this.r = r;
        this.c = c;
        this.gameMap = gameMap;
        this.color = "#b37226";
    }

    update() {
        this.render();
    }

    render() {
        // 取出GameMap格子的单位长度（一定要动态取，因为L会动态变化）
        const L = this.gameMap.L;
        const ctx = this.gameMap.ctx;

        ctx.fillStyle = this.color;
        ctx.fillRect(this.c * L, this.r * L, L, L);
    }
}