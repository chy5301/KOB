<script>
import {ref} from "vue"
import {reactive} from "vue";
import $ from "jquery"
import {useStore} from "vuex";
import {Modal} from "bootstrap/dist/js/bootstrap";

export default {
  setup() {
    const store = useStore();
    let bots = ref([]);

    const newBot = reactive({
      title: "",
      description: "",
      content: "",
      exception_message: "",
    })

    const refreshBotList = () => {
      $.ajax({
        url: "http://localhost:3000/user/bot/getlist",
        type: "GET",
        headers: {
          Authorization: "Bearer " + store.state.user.jwtToken,
        },
        success(response) {
          if (response.status_message === "Success") {
            bots.value = response.bot_list;
          }
        }
      })
    }

    refreshBotList();

    const addBot = () => {
      newBot.exception_message = "";
      $.ajax({
        url: "http://localhost:3000/user/bot/add",
        type: "POST",
        data: {
          title: newBot.title,
          description: newBot.description,
          content: newBot.content,
        },
        headers: {
          Authorization: "Bearer " + store.state.user.jwtToken,
        },
        success(response) {
          if (response.status_message === "Success") {
            refreshBotList();
            Modal.getInstance("#add-bot-modal").hide();
            newBot.title = "";
            newBot.description = "";
            newBot.content = "";
          } else {
            newBot.exception_message = response.exception_message;
          }
        }
      })
    }

    return {
      bots,
      newBot,
      addBot,
    }
  }
}
</script>

<template>
  <div class="container">
    <div class="row">
      <div class="col-3">
        <div class="card">
          <div class="card-body">
            <img :src="$store.state.user.photo" alt="" style="width:100%">
          </div>
        </div>
      </div>
      <div class="col-9">
        <dic class="card">
          <div class="card-header d-flex justify-content-between align-items-center">
            <span style="font-size:120%;">我的Bot</span>
            <!-- 点击创建按钮打开模态框 -->
            <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#add-bot-modal">
              创建Bot
            </button>
            <!-- 创建 Bot 的模态框 -->
            <div class="modal fade" id="add-bot-modal" tabindex="-1">
              <div class="modal-dialog modal-xl">
                <div class="modal-content">
                  <div class="modal-header">
                    <h1 class="modal-title fs-5">创建 Bot</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                  </div>
                  <div class="modal-body">
                    <div class="mb-3">
                      <label for="add-bot-title" class="form-label">名称</label>
                      <input v-model="newBot.title" type="text" class="form-control" id="add-bot-title"
                             placeholder="请输入Bot名称">
                    </div>
                    <div class="mb-3">
                      <label for="add-bot-description" class="form-label">简介</label>
                      <textarea v-model="newBot.description" type="text" class="form-control" id="add-bot-description"
                                rows="3"
                                placeholder="请输入Bot简介"></textarea>
                    </div>
                    <div class="mb-3">
                      <label for="add-bot-code" class="form-label">代码</label>
                      <textarea v-model="newBot.content" type="text" class="form-control" id="add-bot-code" rows="10"
                                placeholder="请编写Bot代码"></textarea>
                    </div>
                  </div>
                  <div class="modal-footer">
                    <div class="error-message">{{ newBot.exception_message }}</div>
                    <button type="button" class="btn btn-primary" @click="addBot">提交</button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="card-body">
            <table class="table table-hover">
              <thead>
              <tr>
                <th>名称</th>
                <th>创建时间</th>
                <th>操作</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="bot in bots" :key="bot.id" class="align-middle">
                <td>{{ bot.title }}</td>
                <td>{{ bot.createTime }}</td>
                <td>
                  <button type="button" class="btn btn-primary me-2">修改</button>
                  <button type="button" class="btn btn-danger">删除</button>
                </td>
              </tr>
              </tbody>
            </table>
          </div>
        </dic>
      </div>
    </div>
  </div>
</template>

<style scoped>
div.container {
  margin-top: 20px
}

div.error-message {
  color: red;
}
</style>