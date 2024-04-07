<script>
import {ref} from "vue";
import {useStore} from "vuex";
import $ from "jquery";

export default {
  setup() {
    const store = useStore();
    let matchButtonInfo = ref("开始匹配");
    let botList = ref([]);
    let selectBot = ref("-1");

    const clickMatchButton = () => {
      if (matchButtonInfo.value === "开始匹配") {
        matchButtonInfo.value = "取消匹配";
        store.state.pk.socket.send(JSON.stringify({
          event: "start-matching",
          bot_id: selectBot.value,
        }));
      } else {
        matchButtonInfo.value = "开始匹配";
        store.state.pk.socket.send(JSON.stringify({
          event: "stop-matching",
        }));
      }
    }

    // 刷新Bot列表
    const refreshBotList = () => {
      $.ajax({
        url: "http://47.95.158.98:8000/api/user/bot/list",
        type: "GET",
        headers: {
          Authorization: "Bearer " + store.state.user.jwtToken,
        },
        success(response) {
          if (response.status_message === "Success") {
            botList.value = response.bot_list;
          }
        }
      });
    }

    // 从服务器获取botList
    refreshBotList();

    return {
      matchButtonInfo,
      clickMatchButton,
      botList,
      selectBot,
    }
  }
}
</script>

<template>
  <div class="matching">
    <div class="row">
      <div class="col-4">
        <div class="user-photo">
          <img :src="$store.state.user.photo" alt="">
        </div>
        <div class="user-username">
          {{ $store.state.user.username }}
        </div>
      </div>
      <div class="col-4">
        <div class="user-select-bot">
          <select v-model="selectBot" class="form-select" :disabled="matchButtonInfo==='取消匹配'">
            <option value="-1" selected>人工操作</option>
            <option v-for="bot in botList" :key="bot.id" :value="bot.id">
              {{ bot.title }}
            </option>
          </select>
        </div>
      </div>
      <div class="col-4">
        <div class="user-photo">
          <img :src="$store.state.pk.opponentPhoto" alt="">
        </div>
        <div class="user-username">
          {{ $store.state.pk.opponentUsername }}
        </div>
      </div>
      <div class="col-12" style="text-align: center; padding-top:15vh">
        <button @click="clickMatchButton" type="button" class="btn btn-primary btn-lg">{{ matchButtonInfo }}</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
div.matching {
  width: 60vw;
  height: 70vh;
  margin: 40px auto;
  background-color: rgba(255, 255, 255, 0.5);
}

div.user-photo {
  text-align: center;
  padding-top: 10vh;
}

div.user-photo > img {
  border-radius: 50%;
  width: 20vh;
}

div.user-username {
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  padding-top: 2vh;
}

div.user-select-bot {
  padding-top: 20vh;
}

div.user-select-bot > select {
  width: 60%;
  margin: 0 auto;
}
</style>