<script>
import ContentField from "@/components/ContentField.vue";
import {useStore} from "vuex";
import {useRouter} from "vue-router"
import {ref} from "vue";
import $ from "jquery";

export default {
  components: {
    ContentField
  },
  setup() {
    const store = useStore();
    const router = useRouter();
    let currentPage = 1;
    // 每页包含的record的数量
    let pageSize = 10;
    let records = ref([]);
    let recordsCount = 0;
    // 可展示的分页有多少个
    let pages = ref([]);

    const turnToPage = pageNumber => {
      if (pageNumber === -2) {
        pageNumber = currentPage - 1;
      } else if (pageNumber === -1) {
        pageNumber = currentPage + 1;
      }
      let maxPageNumber = Math.ceil(recordsCount / pageSize);
      if (pageNumber >= 1 && pageNumber <= maxPageNumber) {
        pullPage(pageNumber, pageSize);
      }
    }

    const updatePages = () => {
      let maxPageNumber = Math.ceil(recordsCount / pageSize);
      let newPages = [];
      for (let i = currentPage - 2; i <= currentPage + 2; i++) {
        if (i >= 1 && i <= maxPageNumber) {
          newPages.push({
            page_number: i,
            is_active: i === currentPage ? "active" : "",
          });
        }
      }
      pages.value = newPages;
    }

    const pullPage = (page, size) => {
      currentPage = page;
      $.ajax({
        url: "http://localhost:3000/api/record/list",
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
          updatePages();
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
        if (record.record_id === recordId) {
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
      pages,
      turnToPage,
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
      <tr v-for="record in records" :key="record.record_id" class="align-middle">
        <td>
          <img :src="record.user1_photo" alt="" class="user-photo me-2">
          <!--&nbsp;(&nbsp;和class="me-2"都可以)-->
          <span class="record-user-username">{{ record.user1_username }}</span>
        </td>
        <td>
          <img :src="record.user2_photo" alt="" class="user-photo me-2">
          <span class="record-user-username">{{ record.user2_username }}</span>
        </td>
        <td> {{ record.result }}</td>
        <td>{{ record.create_time }}</td>
        <td>
          <button @click="openRecordContent(record.record_id)" type="button" class="btn btn-primary">
            查看录像
          </button>
        </td>
      </tr>
      </tbody>
    </table>
    <nav aria-label="Page navigation example">
      <ul class="pagination justify-content-end">
        <li class="page-item" @click="turnToPage(-2)">
          <a class="page-link" href="#" aria-label="Previous">
            <span aria-hidden="true">&laquo;</span>
          </a>
        </li>
        <li :class="'page-item '+ page.is_active" v-for="page in pages" :key="page.page_number"
            @click="turnToPage(page.page_number)">
          <a class="page-link" href="#">{{ page.page_number }}</a>
        </li>
        <li class="page-item" @click="turnToPage(-1)">
          <a class="page-link" href="#" aria-label="Next">
            <span aria-hidden="true">&raquo;</span>
          </a>
        </li>
      </ul>
    </nav>
  </ContentField>
</template>

<style scoped>
img.user-photo {
  width: 4vh;
  height: auto;
  border-radius: 50%;
}
</style>