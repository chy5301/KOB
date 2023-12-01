<script>
import ContentField from "@/components/ContentField.vue";
import {useStore} from "vuex";
import {ref} from "vue";
import router from "@/router/index";

export default {
  components: {
    ContentField
  },
  setup() {
    const store = useStore();
    let username = ref("");
    let password = ref("");
    let exception_message = ref("");

    const login = () => {
      exception_message.value = "";
      store.dispatch("login", {
        username: username.value,
        password: password.value,
        success() {
          store.dispatch("getInfo", {
            success() {
              router.push({name: "home"})
            },
          })
        },
        error(response) {
          exception_message.value = response.exception_message;
        }
      })
    }

    return {
      username,
      password,
      exception_message,
      login,
    }
  }
}
</script>

<template>
  <ContentField>
    <div class="row justify-content-md-center">
      <div class="col-3">
        <form @submit.prevent="login">
          <div class="mb-3">
            <label for="username" class="form-label">用户名</label>
            <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
          </div>
          <div class="mb-3">
            <label for="password" class="form-label">密码</label>
            <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
          </div>
          <div class="error-message">{{ exception_message }}</div>
          <button type="submit" class="btn btn-primary">登录</button>
        </form>
      </div>
    </div>
  </ContentField>
</template>

<style scoped>
button {
  width: 100%;
}

div.error-message {
  color: red;
}
</style>