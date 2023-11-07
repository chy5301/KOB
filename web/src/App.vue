<!--Add your HTML code here-->
<template>
  <div>
    <div>Bot昵称：{{ botName }}</div>
    <div>Bot战力：{{ botRating }}</div>
  </div>
  <router-view/>
</template>

<!--Add your JavaScript code here-->
<script>
import $ from 'jquery';
import {ref} from 'vue';

export default {
  name: 'App',
  setup: () => {
    let botName = ref("");
    let botRating = ref("");

    // 使用jQuery发送AJAX请求到指定的URL获取Bot信息，在请求成功后执行回调函数
    $.ajax({
      url: "http://localhost:3000/pk/getBotInfo",
      type: "GET",
      success: response => {
        // 将返回的Bot信息赋值给对应的变量
        botName.value = response.name;
        botRating.value = response.rating;
      }
    })

    return {
      botName,
      botRating
    }
  }
}
</script>

<!--Add your CSS code here-->
<style>
body {
  /*添加背景图片并设置背景大小为cover*/
  background-image: url("@/assets/background.png");
  background-size: cover;
}
</style>
