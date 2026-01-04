<template>
  <UApp>
    <NuxtLayout>
      <NuxtPage />
    </NuxtLayout>
  </UApp>
</template>

<script setup>
import {useAuthStore} from "~/stores/auth.js";
import {useAPIStore} from "~/stores/api.js";

const authStore = useAuthStore()
const apiStore = useAPIStore()


if (apiStore.token && !authStore.currentUser) {
  await authStore.getUser()
}
</script>
