<script>
import {GameMap} from "@/assets/scripts/GameMap";
import {onMounted, ref} from "vue"
import {useStore} from "vuex";
import ContentField from "@/components/ContentField.vue";


export default {
  components: {
    ContentField
  },

  setup() {
    const store = useStore();
    let parent = ref(null);
    let canvas = ref(null);
    let playerInfo = {
      playerUsername: "",
      playerPhoto: "",
      opponentUsername: "",
      opponentPhoto: "",
    };
    const red = "#f94848";
    const blue = "#4876ec";
    let playerUsernameColor = {
      player1Color: "color: " + blue,
      player2Color: "color: " + red,
    };

    onMounted(() => {
      store.commit(
          "updateGameObject",
          new GameMap(canvas.value.getContext("2d"), parent.value, store)
      );
    });

    if (store.state.record.isRecord) {
      playerInfo.playerUsername = store.state.record.player1Username;
      playerInfo.playerPhoto = store.state.record.player1Photo;
      playerInfo.opponentUsername = store.state.record.player2Username;
      playerInfo.opponentPhoto = store.state.record.player2Photo;
    } else {
      if (store.state.user.id == store.state.pk.player2Id) {
        playerUsernameColor.player1Color = "color: " + red;
        playerUsernameColor.player2Color = "color: " + blue;
      }
      playerInfo.playerUsername = store.state.user.username;
      playerInfo.playerPhoto = store.state.user.photo;
      playerInfo.opponentUsername = store.state.pk.opponentUsername;
      playerInfo.opponentPhoto = store.state.pk.opponentPhoto;
    }

    return {
      store,
      parent,
      canvas,
      playerInfo,
      playerUsernameColor,
    }
  }
}
</script>

<template>
  <div class="container">
    <ContentField class="content">
      <img :src="playerInfo.playerPhoto" alt="" class="user-photo me-2">
      <span class="player1-username me-4" :style=playerUsernameColor.player1Color>{{ playerInfo.playerUsername }}</span>
      <span class="me-4">对战</span>
      <img :src="playerInfo.opponentPhoto" alt="" class="user-photo me-2">
      <span class="player2-username" :style=playerUsernameColor.player2Color>{{ playerInfo.opponentUsername }}</span>
    </ContentField>
  </div>

  <div ref="parent" class="gamemap">
    <canvas ref="canvas" tabindex="0"></canvas>
  </div>
</template>

<style scoped>
div.container {
  margin-top: -20px;
  width: 380px;
  text-align: center;
}

div.gamemap {
  margin-top: 10px;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

img.user-photo {
  width: 4vh;
  height: auto;
  border-radius: 50%;
}
</style>