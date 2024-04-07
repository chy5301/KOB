<script>
import ContentField from "@/components/ContentField.vue";
import {ref} from "vue";
import router from "@/router/index";
import $ from "jquery";

export default {
  components: {
    ContentField
  },
  setup() {
    let username = ref("");
    let password = ref("");
    let confirmed_password = ref("");
    let exception_message = ref("");

    const register = () => {
      $.ajax({
        url: "http://47.95.158.98:8000/api/user/account/register",
        type: "POST",
        data: {
          username: username.value,
          password: password.value,
          confirmed_password: confirmed_password.value,
        },
        success(response) {
          if (response.status_message === "Success") {
            alert("注册成功，即将跳转到登录页面");
            router.push({name: "user_account_login"});
          }else {
            exception_message.value=response.exception_message;
          }
        },
      });
    }

    return {
      username,
      password,
      confirmed_password,
      exception_message,
      register,
    }
  }
}
</script>

<template>
  <ContentField>
    <div class="row justify-content-md-center">
      <div class="col-3">
        <form @submit.prevent="register">
          <div class="mb-3">
            <label for="username" class="form-label">用户名</label>
            <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
          </div>
          <div class="mb-3">
            <label for="password" class="form-label">密码</label>
            <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
          </div>
          <div class="mb-3">
            <label for="confirmedPassword" class="form-label">确认密码</label>
            <input v-model="confirmed_password" type="password" class="form-control" id="confirmedPassword"
                   placeholder="请再次输入密码">
          </div>
          <div class="error-message">{{ exception_message }}</div>
          <button type="submit" class="btn btn-primary">注册</button>
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