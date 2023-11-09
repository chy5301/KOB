<script>
import ContentField from "@/components/ContentField.vue";

export default {
  components: {
    ContentField
  }
}
</script>

<template>
  <ContentField>
    <h2>Not Found</h2>
    <p>The requested URL {{ this.$route.path }} was not found on this server.</p></ContentField>
</template>

<style scoped>

</style>