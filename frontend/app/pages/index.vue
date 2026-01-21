<template> 
  <div>
    <h1 v-if="authStore.isAdmin">Academics Management System</h1>
  </div>
</template>
<script setup>
const authStore = useAuthStore()

</script>