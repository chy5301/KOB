<script>
import {ref} from "vue";
import {useStore} from "vuex";

export default {
  setup() {
    const store = useStore();
    let matchButtonInfo = ref("开始匹配");

    const clickMatchButton = () => {
      if (matchButtonInfo.value === "开始匹配") {
        matchButtonInfo.value = "取消匹配";
        store.state.pk.socket.send(JSON.stringify({
          event: "start-matching",
        }));
      } else {
        matchButtonInfo.value = "开始匹配";
        store.state.pk.socket.send(JSON.stringify({
          event: "stop-matching",
        }));
      }
    }

    return {
      matchButtonInfo,
      clickMatchButton,
    }
  }
}
</script>

<template>
  <div class="matching">
    <div class="row">
      <div class="col-6">
        <div class="user-photo">
          <img :src="$store.state.user.photo" alt="">
        </div>
        <div class="user-username">
          {{ $store.state.user.username }}
        </div>
      </div>
      <div class="col-6">
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
</style>