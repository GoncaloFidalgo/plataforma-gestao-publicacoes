<template>
  <div class="flex justify-center py-10">
    <UCard class="w-full max-w-lg">
      <template #header>
        <h1 class="text-2xl font-bold">
          Change Password
        </h1>
        <p class="text-sm text-gray-500 mt-1">
          Update your account password
        </p>
      </template>

      <UForm :schema="schema" :state="form" @submit="handleSubmit" class="space-y-5">
        <UFormField label="Old password" name="oldPassword">
          <UInput v-model="form.oldPassword" type="password" autocomplete="current-password" :disabled="loading" />
        </UFormField>

        <UFormField label="New password" name="newPassword">
          <UInput v-model="form.newPassword" type="password" autocomplete="new-password" :disabled="loading" />
        </UFormField>

        <UFormField label="Confirm new password" name="confirmPassword">
          <UInput v-model="form.confirmPassword" type="password" autocomplete="new-password" :disabled="loading" />
        </UFormField>


        <UAlert v-if="errorMessage" color="red" variant="soft" icon="i-heroicons-exclamation-circle"
          title="Change password failed" :description="errorMessage" />

        <div class="flex justify-between items-center pt-2">
          <UButton to="/account" color="gray" variant="ghost" :disabled="loading">
            Cancel
          </UButton>

          <UButton type="submit" :loading="loading" color="primary" icon="i-heroicons-key">
            Save new password
          </UButton>
        </div>
      </UForm>
    </UCard>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { z } from 'zod'
import { useRouter } from 'vue-router'
import { useAuthStore } from '~/stores/auth'
import { useToast } from '#imports'

const authStore = useAuthStore()
const router = useRouter()
const toast = useToast()

const form = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})


const loading = ref(false)
const errorMessage = ref('')

const schema = z.object({
  oldPassword: z.string().min(1, 'Old password is required'),
  newPassword: z.string().min(4, 'New password must have at least 4 characters'),
  confirmPassword: z.string().min(4, 'Please confirm the new password')
}).refine(data => data.newPassword === data.confirmPassword, {
  message: 'Passwords do not match',
  path: ['confirmPassword']
})

const handleSubmit = async () => {
  errorMessage.value = ''
  loading.value = true

  try {
    const payload = { ...form.value }
    const result = await authStore.changePassword(payload)

    toast.add({
      title: 'Password changed',
      description: result?.mensagem || 'Your password was updated successfully.',
      color: 'green',
      icon: 'i-heroicons-check-circle'
    })

    // opcional: voltar à página de conta
    await router.push('/account')
  } catch (error) {
    console.error('Change password error:', error.response?.data || error)

    const serverMessage =
      error.response?.data?.mensagem ||
      error.response?.data?.message ||
      error.response?.data

    errorMessage.value =
      typeof serverMessage === 'string'
        ? serverMessage
        : 'Could not change password. Please try again.'
  } finally {
    loading.value = false
  }
}
</script>
