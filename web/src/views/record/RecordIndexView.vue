<script>
import ContentField from "@/components/ContentField.vue";
import {useStore} from "vuex";
import {ref} from "vue";
import $ from "jquery";

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
          console.log(records);
        },
        error(response) {
          console.log(response);
        },
      });
    }

    pullPage(currentPage, pageSize);

    return {
      records,
      recordsCount,
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
          <button @click="open_record_content(record.record.id)" type="button" class="btn btn-primary">
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