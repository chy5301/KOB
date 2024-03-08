<script>
import PlayGround from "@/components/PlayGround.vue";

export default {
  components: {
    PlayGround,
  },
  setup() {
    /*
    目标是在没访问过/record页面时，也能通过/record/:recordId访问对应的游戏记录，
    在/record页面获取recordList时，可以不用一并获取每个record的全部信息，
    可以只获取两名玩家的username，photo，create_time和id，等到访问/record/:recordId页面时，再获取对应的record的详细信息
    需要修改后端api
    */
    // const store = useStore();

  }
}
</script>

<template>
  <PlayGround/>
</template>

<style scoped>

</style>