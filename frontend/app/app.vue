<template>
  <NuxtLayout>
    <NuxtPage />
  </NuxtLayout>
</template>

<script setup>
import { onMounted } from 'vue'
import { useAuthStore } from '/pages/stores/auth-store'

onMounted(() => {
  const authStore = useAuthStore()
  authStore.restoreAuth()
})
</script>