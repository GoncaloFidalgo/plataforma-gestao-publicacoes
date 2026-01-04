<template>
  <!--
    Layout Fixes:
    - min-h-screen: Forces the div to take up the full screen height so vertical centering works.
    - bg-gray-50: Adds the "white ghost" (light gray) background color.
    - px-4: Adds breathing room on mobile screens.
    - w-full: Ensures the container takes full width.
  -->
  <div class="flex min-h-screen w-full items-center justify-center bg-gray-50 dark:bg-gray-950 px-4 sm:px-6 lg:px-8">

    <!--
      Card Styling:
      - shadow-xl: Gives it a strong lift/pop.
      - ring-1: Adds a subtle border for better definition.
      - bg-white: Ensures the card is distinctly white against the ghost background.
    -->
    <UCard class="w-full max-w-md shadow-xl ring-1 ring-gray-200 dark:ring-gray-800 bg-white dark:bg-gray-900">

      <template #header>
        <h1 class="text-2xl font-bold text-center text-gray-900 dark:text-white">
          Login
        </h1>
        <p class="text-center text-gray-500 text-sm mt-2">
          Sign in to access your dashboard
        </p>
      </template>

      <!--
        Nuxt UI "v4" (v3) Style:
        - Uses :schema for automatic validation
        - Binds directly to state
      -->
      <UForm
          :schema="schema"
          :state="credentials"
          @submit="handleLogin"
          class="space-y-6"
      >

        <!-- Username Field -->
        <UFormField label="Username" name="username">
          <UInput
              v-model="credentials.username"
              icon="i-heroicons-user"
              placeholder="Enter your username"
              autocomplete="username"
              :disabled="loading"
              size="lg"
              class="w-full"
              :ui="{ icon: { trailing: { pointer: '' } } }"
          />
        </UFormField>

        <!-- Password Field -->
        <UFormField label="Password" name="password">
          <UInput
              v-model="credentials.password"
              type="password"
              icon="i-heroicons-lock-closed"
              placeholder="Enter your password"
              autocomplete="current-password"
              :disabled="loading"
              size="lg"
              class="w-full"
          />
        </UFormField>

        <!-- Error Alert -->
        <UAlert
            v-if="errorMessage"
            icon="i-heroicons-exclamation-circle"
            color="red"
            variant="soft"
            title="Login Failed"
            :description="errorMessage"
        />

        <!-- Submit Button -->
        <UButton
            type="submit"
            :loading="loading"
            block
            size="lg"
            color="primary"
            icon="i-heroicons-arrow-right-on-rectangle"
            class="w-full font-semibold shadow-sm"
        >
          Sign In
        </UButton>
      </UForm>

    </UCard>
  </div>
</template>

<script setup>
import { z } from 'zod'
import { useAuthStore } from '~/stores/auth'

useHead({
  title: 'Login - DAE'
})

// CRITICAL FIX: Set layout to false.
// This prevents 'layouts/default.vue' from wrapping this page
// in a container that might break the full-screen centering.
definePageMeta({
  layout: false
})

const authStore = useAuthStore()
const toast = useToast()

// Define Zod Schema for validation
const schema = z.object({
  username: z.string().min(1, 'Username is required'),
  password: z.string().min(1, 'Password is required')
})

const credentials = ref({
  username: '',
  password: '',
})

const errorMessage = ref('')
const loading = ref(false)

const handleLogin = async () => {
  // UForm handles validation automatically before reaching here
  errorMessage.value = ''
  loading.value = true

  try {
    await authStore.login(credentials.value)

    const role = authStore.currentUser?.role || 'STUDENT'
    const redirectPath = getRedirectPath(role)

    toast.add({ title: 'Welcome back!', color: 'green', icon: 'i-heroicons-check-circle' })

    await navigateTo(redirectPath)

  } catch (error) {
    const serverMessage = error.response?.data?.message
    errorMessage.value = serverMessage || 'Invalid username or password. Please try again.'
  } finally {
    loading.value = false
  }
}

const getRedirectPath = (role) => {
  const roleRedirects = {
    'ADMINISTRATOR': '/administrators',
    'TEACHER': '/teachers',
    'STUDENT': '/students',
  }
  return roleRedirects[role] || '/'
}
</script>