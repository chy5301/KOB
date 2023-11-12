<script>
import GameMap from "@/components/GameMap.vue"

export default {
  components: {
    GameMap,
  }
}
</script>

<template>
  <div class="playground">
    <GameMap/>
  </div>
</template>

<style scoped>
div.playground {
  width: 60vw;
  height: 70vh;
  margin: 40px auto;
  background:lightcyan;
}
</style>