<template>
  <div class="flex justify-center py-10">
    <UCard class="w-full max-w-lg">
      <template #header>
        <h1 class="text-2xl font-bold">
          My Account
        </h1>
        <p class="text-sm text-gray-500 mt-1" v-if="user">
          Logged in as <strong>{{ user.username }}</strong>
        </p>
      </template>

      <div v-if="!user">
        <UAlert
          color="red"
          icon="i-heroicons-exclamation-triangle"
          title="Not authenticated"
          description="Please log in to view your account."
        />
      </div>

      <div v-else class="space-y-3">
        <div>
          <strong>Name:</strong> {{ user.name }}
        </div>
        <div>
          <strong>Username:</strong> {{ user.username }}
        </div>
        <div>
          <strong>Email:</strong> {{ user.email }}
        </div>
        <div>
          <strong>Role:</strong> {{ user.roleType }}
        </div>
        <div>
          <strong>Status: </strong>
          <span :class="user.active ? 'text-green-600' : 'text-red-600'">
            {{ user.active ? 'Active' : 'Suspended' }}
          </span>
        </div>
         <div class="flex justify-end items-center gap-2">
          <UButton
              :to="`/me/change-password`"
              color="primary" variant="outline"
              icon="i-heroicons-key"
            >
              Change Password
            </UButton>

            <UButton
              :to="`/me/edit`"
              color="primary"
              icon="i-heroicons-pencil-square"
            >
              Edit
            </UButton>
        </div>
      </div>
    </UCard>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAuthStore } from '~/stores/auth'

const authStore = useAuthStore()

const user = computed(() => authStore.currentUser)

</script>
