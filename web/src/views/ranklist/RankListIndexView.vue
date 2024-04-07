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
    // 每页包含的record的数量
    let pageSize = 10;
    let users = ref([]);
    let usersCount = 0;
    // 可展示的分页有多少个
    let pages = ref([]);

    const turnToPage = pageNumber => {
      if (pageNumber === -2) {
        pageNumber = currentPage - 1;
      } else if (pageNumber === -1) {
        pageNumber = currentPage + 1;
      }
      let maxPageNumber = Math.ceil(usersCount / pageSize);
      if (pageNumber >= 1 && pageNumber <= maxPageNumber) {
        pullPage(pageNumber, pageSize);
      }
    }

    const updatePages = () => {
      let maxPageNumber = Math.ceil(usersCount / pageSize);
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
        url: "http://47.95.158.98:8000/api/ranklist",
        data: {
          page_number: page,
          page_size: size,
        },
        type: "GET",
        headers: {
          Authorization: "Bearer " + store.state.user.jwtToken,
        },
        success(response) {
          users.value = response.users;
          usersCount = response.users_count;
          updatePages();
        },
        error(response) {
          console.log(response);
        },
      });
    }
    // 获取对局列表
    pullPage(currentPage, pageSize);

    return {
      users,
      usersCount,
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
        <th>玩家</th>
        <th>天梯分</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="user in users" :key="user.record_id" class="align-middle">
        <td>
          <img :src="user.photo" alt="" class="user-photo me-2">
          <!--&nbsp;(&nbsp;和class="me-2"都可以)-->
          <span class="record-user-username">{{ user.username }}</span>
        </td>
        <td> {{ user.rating }}</td>
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