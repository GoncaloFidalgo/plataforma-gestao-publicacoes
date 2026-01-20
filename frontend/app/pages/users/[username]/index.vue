<template>
  <div class="flex justify-center py-10">
    <UCard class="w-full max-w-lg shadow-xl ring-1 ring-gray-200 dark:ring-gray-800">
      <template #header>
        <h1 class="text-2xl font-bold">
          User Details
        </h1>

        <p class="text-sm text-gray-500 mt-1" v-if="user">
          Viewing profile of <strong>{{ user.username }}</strong>
        </p>
      </template>

      <!-- USER NOT FOUND -->
      <div v-if="!user">
        <UAlert
          color="red"
          icon="i-heroicons-exclamation-triangle"
          title="User not found"
          description="The requested user does not exist."
        />

        <div class="pt-4">
          <UButton
            to="/users"
            color="orange"
            variant="outline"
            icon="i-heroicons-arrow-left"
          >
            Return to list
          </UButton>
        </div>
      </div>

      <!-- USER DETAILS -->
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
          <strong>Role:</strong> {{ user.role }}
        </div>

        <div>
          <strong>Status: </strong>
          <span
            :class="user.active ? 'text-green-600' : 'text-red-600'"
          >
            {{ user.active ? 'Active' : 'Suspended' }}
          </span>
        </div>

        <!-- ACTION BUTTONS -->
        <div class="flex gap-3 pt-5 justify-between">
          <UButton
            to="/users"
            variant="outline"
            color="orange"
            icon="i-heroicons-arrow-left"
          >
            Return to list
          </UButton>

          <div class="flex gap-2">
            <UButton
              :to="`/users/${user.username}/role`"
              color="primary" variant="outline"
              icon="i-heroicons-shield-check"
            >
              Change Role
            </UButton>

            <UButton
              :to="`/users/${user.username}/edit`"
              color="primary"
              icon="i-heroicons-pencil-square"
            >
              Edit
            </UButton>
          </div>
        </div>
      </div>
    </UCard>
  </div>
</template>


<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '~/stores/user.js'

const route = useRoute()
const usersStore = useUserStore()

const username = computed(() => route.params.username)

const user = computed(() =>
  usersStore.findByUsername(username.value)
)
</script>
