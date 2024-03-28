<script>
import PlayGround from "@/components/PlayGround.vue";
import {useStore} from "vuex";
import {useRoute, useRouter} from "vue-router"
import $ from "jquery";

export default {
  components: {
    PlayGround,
  },
  setup() {
    const store = useStore();
    const route = useRoute();
    const router = useRouter();
    let recordId = route.params.recordId;

    const getRecordInfo = recordId => {
      $.ajax({
        url: "http://localhost:3000/record/getinfo",
        /*
        这里需要将$.ajax请求设置成同步的，否则子组件PlayGround的加载会在请求获取到信息之前完成，
        导致等请求获取到record_info时，PlayGround组件已经先进行加载，获取到了错误的信息。
        */
        async: false,
        data: {
          record_id: recordId,
        },
        type: "GET",
        headers: {
          Authorization: "Bearer " + store.state.user.jwtToken,
        },
        success(response) {
          if (response.status_message === "Success") {
            setReplyInfo(response.record_info, response.players_info);
          } else {
            alert(response.exception_message);
            router.push({
              name: "record_index",
            });
          }
        },
        error(response) {
          console.log(response);
        },
      });
    }

    const setReplyInfo = (record_info, players_info) => {
      // 设置isRecord=true
      store.commit("updateIsRecord", true);
      // 将当前的游戏信息设置为要回放的游戏录像的信息
      store.commit("updateGame", {
        game_map: JSON.parse(record_info.map),
        player1_id: record_info.player1Id,
        player1_startX: record_info.player1StartX,
        player1_startY: record_info.player1StartY,
        player2_id: record_info.player2Id,
        player2_startX: record_info.player2StartX,
        player2_startY: record_info.player2StartY,
      });
      store.commit("updateRecordPlayersInfo", players_info)
      // 设置要回放的游戏录像的操作信息
      store.commit("updateRecordInfo", {
        player1Steps: JSON.parse(record_info.player1Steps),
        player2Steps: JSON.parse(record_info.player2Steps),
        recordLoser: record_info.loser,
      });
    }

    // 获取record信息
    getRecordInfo(recordId);
  }
}
</script>

<template>
  <PlayGround/>
</template>

<style scoped>

</style>