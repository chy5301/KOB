<script>
import {ref} from "vue"
import {reactive} from "vue";
import $ from "jquery"
import {useStore} from "vuex";
import {Modal} from "bootstrap/dist/js/bootstrap";
import {VAceEditor} from "vue3-ace-editor";
import ace from "ace-builds"
import 'ace-builds/src-noconflict/mode-c_cpp';
import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/theme-chrome';
import 'ace-builds/src-noconflict/ext-language_tools';

export default {
  components: {
    VAceEditor,
  },
  setup() {
    ace.config.set(
        "basePath",
        "https://cdn.jsdelivr.net/npm/ace-builds@" +
        require("ace-builds").version +
        "/src-noconflict/");

    const store = useStore();
    let botList = ref([]);

    const newBot = reactive({
      title: "",
      description: "",
      content: "",
      exception_message: "",
    })

    // 刷新Bot列表
    const refreshBotList = () => {
      $.ajax({
        url: "http://localhost:3000/user/bot/getlist",
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

    refreshBotList();

    // 创建Bot
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
      });
    }

    // 删除Bot
    const removeBot = (bot) => {
      $.ajax({
        url: "http://localhost:3000/user/bot/remove",
        type: "POST",
        data: {
          bot_id: bot.id,
        },
        headers: {
          Authorization: "Bearer " + store.state.user.jwtToken,
        },
        success(response) {
          if (response.status_message === "Success") {
            refreshBotList();
          } else {
            alert(response.exception_message);
          }
        },
      });
    }

    // 修改bot
    const updateBot = (bot) => {
      bot.exception_message = "";
      $.ajax({
        url: "http://localhost:3000/user/bot/update",
        type: "POST",
        data: {
          bot_id: bot.id,
          title: bot.title,
          description: bot.description,
          content: bot.content,
        },
        headers: {
          Authorization: "Bearer " + store.state.user.jwtToken,
        },
        success(response) {
          if (response.status_message === "Success") {
            refreshBotList();
            Modal.getInstance("#update-bot-modal-" + bot.id).hide();
          } else {
            bot.exception_message = response.exception_message;
          }
        }
      });
    }

    return {
      botList,
      newBot,
      addBot,
      removeBot,
      updateBot,
      refreshBotList,
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
                      <VAceEditor
                          v-model:value="newBot.content"
                          @init="editorInit"
                          lang="c_cpp"
                          theme="textmate"
                          style="height: 400px"
                          :options="{
                              enableBasicAutocompletion: true,  //启用基本自动完成
                              enableSnippets: true,  // 启用代码段
                              enableLiveAutocompletion: true,  // 启用实时自动完成
                              fontSize: 16,  //设置字号
                              tabSize: 4,  // 标签大小
                              showPrintMargin: false,  //去除编辑器里的竖线
                              highlightActiveLine: true,  // 选中行高亮显示
                            }"
                      />
                    </div>
                  </div>
                  <div class="modal-footer">
                    <div class="error-message">{{ newBot.exception_message }}</div>
                    <button type="button" class="btn btn-primary" @click="addBot">提交</button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal" @click="refreshBotList">
                      取消
                    </button>
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
              <tr v-for="bot in botList" :key="bot.id" class="align-middle">
                <td>{{ bot.title }}</td>
                <td>{{ bot.createTime }}</td>
                <td>
                  <button type="button" class="btn btn-primary me-2" data-bs-toggle="modal"
                          :data-bs-target="'#update-bot-modal-'+bot.id">修改
                  </button>
                  <button type="button" class="btn btn-danger" @click="removeBot(bot)">删除</button>
                  <!-- 修改 Bot 的模态框 -->
                  <div class="modal fade" :id="'update-bot-modal-'+bot.id" tabindex="-1">
                    <div class="modal-dialog modal-xl">
                      <div class="modal-content">
                        <div class="modal-header">
                          <h1 class="modal-title fs-5">创建 Bot</h1>
                          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                          <div class="mb-3">
                            <label for="update-bot-title" class="form-label">名称</label>
                            <input v-model="bot.title" type="text" class="form-control" id="update-bot-title">
                          </div>
                          <div class="mb-3">
                            <label for="update-bot-description" class="form-label">简介</label>
                            <textarea v-model="bot.description" type="text" class="form-control"
                                      id="update-bot-description"
                                      rows="3"></textarea>
                          </div>
                          <div class="mb-3">
                            <label for="update-bot-code" class="form-label">代码</label>
                            <VAceEditor
                                v-model:value="bot.content"
                                @init="editorInit"
                                lang="c_cpp"
                                theme="textmate"
                                style="height: 400px"
                                :options="{
                              enableBasicAutocompletion: true,  //启用基本自动完成
                              enableSnippets: true,  // 启用代码段
                              enableLiveAutocompletion: true,  // 启用实时自动完成
                              fontSize: 16,  //设置字号
                              tabSize: 4,  // 标签大小
                              showPrintMargin: false,  //去除编辑器里的竖线
                              highlightActiveLine: true,  // 选中行高亮显示
                            }"
                            />
                          </div>
                        </div>
                        <div class="modal-footer">
                          <div class="error-message">{{ bot.exception_message }}</div>
                          <button type="button" class="btn btn-primary" @click="updateBot(bot)">提交</button>
                          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal"
                                  @click="refreshBotList">取消
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
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