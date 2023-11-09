<!--Add your HTML code here-->
<template>
  <NavBar/>
  <router-view/>
</template>

<!--Add your JavaScript code here-->
<script>
// 导入NavBar组件
import NavBar from "@/components/NavBar.vue"
// 导入bootstrap的css和js
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"

export default {
  components: {
    NavBar
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
