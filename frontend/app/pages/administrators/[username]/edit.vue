<template>
  <div class="flex min-h-screen w-full items-start justify-center ">
    <UCard class="w-full max-w-xl shadow-xl ring-1 ring-gray-200 dark:ring-gray-800 bg-white dark:bg-gray-900">
      <template #header>
        <h1 class="text-2xl font-bold text-gray-900 dark:text-white">
          Edit User <strong>{{ user.username }}</strong>
        </h1>
        <p class="text-sm text-gray-500 mt-1">
          Update user details and status
        </p>
      </template>

      <!-- Loading inicial (quando vem direto por URL / refresh) -->
      <div v-if="initialLoading">
        <UAlert color="gray" icon="i-heroicons-arrow-path" title="Loading user..."
          description="Please wait while we load user data." />
      </div>

      <div v-else-if="!user">
        <UAlert color="red" icon="i-heroicons-exclamation-triangle" title="User not found"
          description="The requested user does not exist." />
        <div class="mt-4">
          <UButton to="/administrators" color="gray" variant="soft">
            Back to list
          </UButton>
        </div>
      </div>

      <UForm v-else :schema="schema" :state="form" @submit="handleSubmit" class="space-y-6">
        <!-- Username -->
        <UFormField label="Name" name="name">
          <UInput v-model="form.name" placeholder="Name" :disabled="loading" />
        </UFormField>

        <!-- Email -->
        <UFormField label="Email" name="email">
          <UInput v-model="form.email" type="email" placeholder="Email" :disabled="loading" />
        </UFormField>

        <!-- Active / Status -->
        <UFormField label="Status" name="active">
          <div class="flex items-center gap-3">
            <USwitch v-model="form.active" :disabled="loading" />
            <span class="text-sm text-gray-600 dark:text-gray-300">
              {{ form.active ? 'Active' : 'Suspended' }}
            </span>
          </div>
        </UFormField>

        <!-- Info da role (só leitura, muda-se noutra página) -->
        <UFormField label="Role">
          <p class="text-sm text-gray-500">
            Current role: <strong>{{ user.roleType }}</strong>.
            To change role, use the "Change Role" page.
          </p>
        </UFormField>

        <!-- Error -->
        <UAlert v-if="errorMessage" icon="i-heroicons-exclamation-circle" color="red" variant="soft"
          title="Update failed" :description="errorMessage" />

        <!-- Actions -->
        <div class="flex justify-between items-center">
          <UButton :to="`/administrators`" color="primary" variant="outline" :disabled="loading">
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
import { ref, computed, watchEffect, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUsersStore } from '~/stores/users'
import { useToast } from '#imports'

const route = useRoute()
const router = useRouter()
const usersStore = useUsersStore()
const toast = useToast()

const initialLoading = ref(true)

const user = computed(() =>
  usersStore.findByUsername(String(route.params.username || ''))
)

const form = ref({
  name: '',
  email: '',
  active: true
})

const errorMessage = ref('')
const loading = ref(false)

// schema de validação (Nuxt UI + Zod)
const schema = z.object({
  name: z.string().min(1, 'Name is required'),
  active: z.boolean()
})

// preencher o form com dados do user
watchEffect(() => {
  if (user.value) {
    form.value.name = user.value.name
    form.value.email = user.value.email
    form.value.active = user.value.active
  }
})

// garantir que temos os users carregados (caso entres por URL direto)
onMounted(async () => {
  try {
    if (!usersStore.users.length) {
      await usersStore.fetchUsers()
    }
  } catch (e) {
    console.error('Erro ao carregar utilizadores para edição:', e)
  } finally {
    initialLoading.value = false
  }
})

const handleSubmit = async () => {
  if (!user.value) return

  errorMessage.value = ''
  loading.value = true

  try {
    console.log(user.value)
    await usersStore.updateUser(user.value.username, {
      name: form.value.name,
      email: form.value.email,
      active: form.value.active
    })

    toast.add({
      title: 'User updated',
      description: 'User details were saved successfully.',
      color: 'green',
      icon: 'i-heroicons-check-circle'
    })

    await router.push(`/administrators`)
  } catch (err) {
    console.error(err)
    errorMessage.value = 'Could not update user. Please try again.'
  } finally {
    loading.value = false
  }
}
</script>
