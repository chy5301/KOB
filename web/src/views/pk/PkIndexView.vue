<script>
import PlayGround from "@/components/PlayGround.vue"
import MatchingComponent from "@/components/MatchingComponent.vue"
import {onMounted} from "vue";
import {onUnmounted} from "vue";
import {useStore} from "vuex";

export default {
  components: {
    PlayGround,
    MatchingComponent,
  },
  setup() {
    const store = useStore();
    // 这里是`不是单引号,字符串中有${}表达式操作的话，需要用`，不能用引号。
    const socketUrl = `ws://localhost:3000/websocket/${store.state.user.jwtToken}`;

    let socket = null;
    onMounted(() => {
      store.commit("updateOpponent", {
        username: "我的对手",
        photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
      })
      socket = new WebSocket(socketUrl);

      socket.onopen = () => {
        console.log("Connected!");
        console.log(socketUrl);
        store.commit("updateSocket", socket);
      }

      socket.onmessage = message => {
        const data = JSON.parse(message.data);
        console.log(data);
        // 如果匹配成功
        if (data.event === "matching-success") {
          console.log("updateOpponent")
          store.commit("updateOpponent", {
            username: data.opponent_username,
            photo: data.opponent_photo,
          });
          setTimeout(() => {
            store.commit("updateStatus", "playing");
          }, 2000);
          store.commit("updateGame", data.game_info);
        } else if (data.event === "move") {
          console.log(data);
          const game = store.state.pk.gameObject;
          const [snake1, snake2] = game.snakes;
          snake1.setDirection(data.player1_direction);
          snake2.setDirection(data.player2_direction);
        } else if (data.event === "result") {
          console.log(data);
          const game = store.state.pk.gameObject;
          const [snake1, snake2] = game.snakes;

          // 分别判断两条蛇是否死亡
          if (data.loser === "all" || data.loser === "player1") {
            snake1.status = "died";
          }
          if (data.loser === "all" || data.loser === "player2") {
            snake2.status = "died";
          }
        }
      }

      socket.onclose = () => {
        console.log("Disconnected!");
        console.log(socketUrl);
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
</template>

<style scoped>

</style>