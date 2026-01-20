<template>
  <div class="flex min-h-screen w-full items-start justify-center">
    <UCard class="w-full max-w-xl shadow-xl ring-1 ring-gray-200 dark:ring-gray-800 bg-white dark:bg-gray-900">
      <template #header>
        <h1 class="text-2xl font-bold text-gray-900 dark:text-white">
          My Profile <strong v-if="me?.username">{{ me.username }}</strong>
        </h1>
        <p class="text-sm text-gray-500 mt-1">
          Update your personal details
        </p>
      </template>

      <div v-if="initialLoading">
        <UAlert
          color="gray"
          icon="i-heroicons-arrow-path"
          title="Loading..."
          description="Please wait while we load your profile."
        />
      </div>

      <div v-else-if="!me">
        <UAlert
          color="red"
          icon="i-heroicons-exclamation-triangle"
          title="Could not load profile"
          description="Your user data is not available."
        />
        <div class="mt-4">
          <UButton to="/" color="gray" variant="soft">
            Go home
          </UButton>
        </div>
      </div>

      <UForm
        v-else
        :schema="schema"
        :state="form"
        @submit="handleSubmit"
        class="space-y-6"
      >
        <UFormField label="Name" name="name">
          <UInput v-model="form.name" placeholder="Name" :disabled="loading" />
        </UFormField>

        <UFormField label="Email" name="email">
          <UInput v-model="form.email" type="email" placeholder="Email" :disabled="loading" />
        </UFormField>

        <UAlert
          v-if="errorMessage"
          icon="i-heroicons-exclamation-circle"
          color="red"
          variant="soft"
          title="Update failed"
          :description="errorMessage"
        />

        <div class="flex justify-end items-center gap-2">
          <UButton to="/me" color="primary" variant="outline" :disabled="loading">
            Cancel
          </UButton>

          <UButton type="submit" :loading="loading" color="primary" icon="i-heroicons-check-circle">
            Save changes
          </UButton>
        </div>
      </UForm>
    </UCard>
  </div>
</template>

<script setup>
import { z } from 'zod'
import { ref, computed, onMounted, watchEffect } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '~/stores/auth'

import { useToast } from '#imports'

const router = useRouter()
const authStore = useAuthStore()
const toast = useToast()

const initialLoading = ref(true)
const loading = ref(false)
const errorMessage = ref('')

const me = computed(() => authStore.currentUser)

const form = ref({
  name: '',
  email: ''
})

const schema = z.object({
  name: z.string().min(1, 'Name is required'),
  email: z.string().email('Invalid email')
})

onMounted(async () => {
  try {
    if (!authStore.currentUser) {
      await authStore.authUser()
    }

    form.value.name = me.value?.name || ''
    form.value.email = me.value?.email || ''
  } catch (e) {
    console.error('Erro ao carregar perfil:', e)
  } finally {
    initialLoading.value = false
  }
})


const handleSubmit = async () => {
  errorMessage.value = ''
  loading.value = true

  try {
    // chama PUT /users/me
    await authStore.updateMe({
      name: form.value.name,
      email: form.value.email
  })


    toast.add({
      title: 'Profile updated',
      description: 'Your details were saved successfully.',
      color: 'green',
      icon: 'i-heroicons-check-circle'
    })

    await router.push('/me')
  } catch (err) {
    console.error(err)
    errorMessage.value =
      err?.response?.data?.mensagem ||
      'Could not update your profile. Please try again.'
  } finally {
    loading.value = false
  }
}
</script>
