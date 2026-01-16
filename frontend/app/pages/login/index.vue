<template>
  <div class="login-page">
    <UCard class="login-card">

      <template #header>
        <div class="flex flex-col items-center gap-2">
          <h1 class="login-title">
            Login
          </h1>
          <p class="login-subtitle">
            Sign in to access your dashboard
          </p>
        </div>
      </template>

      <UForm
        :schema="schema"
        :state="credentials"
        @submit="handleLogin"
        class="space-y-6 login-form"
      >
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

        <UAlert
          v-if="errorMessage"
          icon="i-heroicons-exclamation-circle"
          color="red"
          variant="soft"
          title="Login Failed"
          :description="errorMessage"
        />

        <UButton
          type="submit"
          :loading="loading"
          block
          size="lg"
          icon="i-heroicons-arrow-right-on-rectangle"
          class="login-submit font-semibold shadow-sm"
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

// This prevents 'layouts/default.vue' from wrapping this page
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


    const role = authStore.currentUser?.role || 'ADMINISTRATOR'


    const redirectPath = getRedirectPath(role)

    toast.add({ title: 'Welcome back!', color: 'green', icon: 'i-heroicons-check-circle' })

    await navigateTo(redirectPath)

  }  catch (error) {


  const serverMessage =
    error.response?.data?.message ||
    error.response?.data?.error ||
    error.response?.data

  errorMessage.value =
    typeof serverMessage === 'string'
      ? serverMessage
      : 'Invalid username or password. Please try again.'
} finally {
  loading.value = false
}
}

const getRedirectPath = (role) => {
  console.log("role:", role)
  const roleRedirects = {
      1: '/administrators',
      2: '/teachers',
      3: '/students',
  }
  return roleRedirects[role] || '/'
}
</script>