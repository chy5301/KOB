<script>
import {useStore} from "vuex";

export default {
  setup() {
    const store = useStore();
    const restart = () => {
      store.commit("updateStatus", "matching");
      store.commit("updateLoser", "none");
      store.commit("updateOpponent", {
        username: "我的对手",
        photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
      })
    }
    return {
      restart
    };
  }
}
</script>

<template>
  <div class="result-board">
    <div class="result-board-text" v-if="$store.state.pk.loser==='all'">
      平局
    </div>
    <div class="result-board-text"
         v-else-if="$store.state.pk.loser==='player1'&&$store.state.pk.player1Id===parseInt($store.state.user.id)">
      你输了
    </div>
    <div class="result-board-text"
         v-else-if="$store.state.pk.loser==='player2'&&$store.state.pk.player2Id===parseInt($store.state.user.id)">
      你输了
    </div>
    <div class="result-board-text" v-else>
      你赢了
    </div>
    <div class="result-board-button">
      <button @click="restart" type="button" class="btn btn-primary btn-lg">
        重新开始
      </button>
    </div>
  </div>
</template>

<style scoped>
div.result-board {
  height: 30vh;
  width: 30vw;
  background-color: rgba(255, 255, 255, 0.5);
  position: absolute;
  top: 30vh;
  left: 35vw;
}

div.result-board-text {
  text-align: center;
  font-size: 50px;
  font-weight: bold;
  font-style: italic;
  padding-top: 5vh;
}

div.result-board-button {
  text-align: center;
  padding-top: 7vh;
}
</style>