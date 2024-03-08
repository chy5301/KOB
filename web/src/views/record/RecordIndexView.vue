<script>
import ContentField from "@/components/ContentField.vue";
import {useStore} from "vuex";
import {ref} from "vue";
import $ from "jquery";
import router from "@/router/index";

export default {
  components: {
    ContentField
  },
  setup() {
    const store = useStore();
    let currentPage = 1;
    let pageSize = 20;
    let records = ref([]);
    let recordsCount = 0;

    const pullPage = (page, size) => {
      currentPage = page;
      $.ajax({
        url: "http://localhost:3000/record/getlist",
        data: {
          page_number: page,
          page_size: size,
        },
        type: "GET",
        headers: {
          Authorization: "Bearer " + store.state.user.jwtToken,
        },
        success(response) {
          records.value = response.records;
          recordsCount = response.records_count;
        },
        error(response) {
          console.log(response);
        },
      });
    }
    // 获取对局列表
    pullPage(currentPage, pageSize);

    // 打开record页面
    const openRecordContent = recordId => {
      for (const record of records.value) {
        if (record.record.id === recordId) {
          // 设置isRecord=true
          store.commit("updateIsRecord", true);
          // 将当前的游戏信息设置为要播放的游戏录像的信息
          console.log(JSON.parse(record.record.map));
          store.commit("updateGame", {
            game_map: JSON.parse(record.record.map),
            player1_id: record.record.player1Id,
            player1_startX: record.record.player1StartX,
            player1_startY: record.record.player1StartY,
            player2_id: record.record.player2Id,
            player2_startX: record.record.player2StartX,
            player2_startY: record.record.player2StartY,
          });
          store.commit("updateRecordInfo", {
            player1Steps: JSON.parse(record.record.player1Steps),
            player2Steps: JSON.parse(record.record.player2Steps),
            recordLoser: record.record.loser,
          });
          console.log("isArray: " + Array.isArray(store.state.pk.gameMap));
          console.log(store.state.pk.gameMap);
          // 跳转页面
          router.push({
            name: "record_content",
            params: {
              recordId,
            },
          });
          break;
        }
      }
    }

    return {
      records,
      recordsCount,
      openRecordContent,
    }
  }
}
</script>

<template>
  <ContentField>
    <table class="table table-hover">
      <thead>
      <tr>
        <th>Player1</th>
        <th>Player2</th>
        <th>对战结果</th>
        <th>对战时间</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="record in records" :key="record.record.id" class="align-middle">
        <td>
          <img :src="record.user1_photo" alt="" class="record-user-photo me-2">
          <!--&nbsp;(&nbsp;和class="me-2"都可以)-->
          <span class="record-user-username">{{ record.user1_username }}</span>
        </td>
        <td>
          <img :src="record.user2_photo" alt="" class="record-user-photo me-2">
          <span class="record-user-username">{{ record.user2_username }}</span>
        </td>
        <td> {{ record.result }}</td>
        <td>{{ record.record.createTime }}</td>
        <td>
          <button @click="openRecordContent(record.record.id)" type="button" class="btn btn-primary">
            查看录像
          </button>
        </td>
      </tr>
      </tbody>
    </table>
  </ContentField>
</template>

<style scoped>
img.record-user-photo {
  width: 4vh;
  height: auto;
  border-radius: 50%;
}
</style>