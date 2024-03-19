<script>
import PlayGround from "@/components/PlayGround.vue"
import MatchingComponent from "@/components/MatchingComponent.vue"
import ResultBoard from "@/components/ResultBoard.vue"
import {onMounted} from "vue";
import {onUnmounted} from "vue";
import {useStore} from "vuex";

export default {
  components: {
    PlayGround,
    MatchingComponent,
    ResultBoard,
  },
  setup() {
    const store = useStore();
    // 这里是`不是单引号,字符串中有${}表达式操作的话，需要用`，不能用引号。
    const socketUrl = `ws://localhost:3000/websocket/${store.state.user.jwtToken}`;

    // 每次进入pk页面，输赢状态重置
    store.commit("updateLoser", "none");
    // 每次进入pk页面，设置isRecord=false
    store.commit("updateIsRecord", false);

    let socket = null;
    onMounted(() => {
      store.commit("updateOpponent", {
        username: "我的对手",
        photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
      })
      socket = new WebSocket(socketUrl);

      socket.onopen = () => {
        console.log("Connected!");
        store.commit("updateSocket", socket);
      }

      socket.onmessage = message => {
        const data = JSON.parse(message.data);
        // 如果匹配成功
        if (data.event === "matching-success") {
          store.commit("updateOpponent", {
            username: data.opponent_username,
            photo: data.opponent_photo,
          });
          setTimeout(() => {
            store.commit("updateStatus", "playing");
          }, 2000);
          store.commit("updateGame", data.game_info);
        } else if (data.event === "move") {
          const game = store.state.pk.gameObject;
          const [snake1, snake2] = game.snakes;
          snake1.setDirection(data.player1_direction);
          snake2.setDirection(data.player2_direction);
        } else if (data.event === "result") {
          const game = store.state.pk.gameObject;
          const [snake1, snake2] = game.snakes;

          // 分别判断两条蛇是否死亡
          if (data.loser === "all" || data.loser === "player1") {
            snake1.status = "died";
          }
          if (data.loser === "all" || data.loser === "player2") {
            snake2.status = "died";
          }
          store.commit("updateLoser", data.loser);
        }
      }

      socket.onclose = () => {
        console.log("Disconnected!");
      }
    });

    onUnmounted(() => {
      socket.close();
      store.commit("updateStatus", "matching");
    });
  }
}
</script>

<template>
  <PlayGround v-if="$store.state.pk.status === 'playing'"/>
  <MatchingComponent v-if="$store.state.pk.status ==='matching'"/>
  <ResultBoard v-if="$store.state.pk.loser !=='none'"/>
</template>

<style scoped>

</style>