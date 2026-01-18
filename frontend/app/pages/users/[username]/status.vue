<template>
  <div class="flex min-h-screen w-full items-start justify-center">
    <UCard class="w-full max-w-xl shadow-xl ring-1 ring-gray-200 dark:ring-gray-800 bg-white dark:bg-gray-900">
      <template #header>
        <h1 class="text-2xl font-bold text-gray-900 dark:text-white">
          Change User Status
        </h1>
        <p class="text-sm text-gray-500 mt-1" v-if="user">
          User:
          <strong>{{ user.username }}</strong>
          (current status: {{ user.active ? 'Active' : 'Suspended' }})
        </p>
      </template>

      <div v-if="!user">
        <UAlert
          color="red"
          icon="i-heroicons-exclamation-triangle"
          title="User not found"
          description="The requested user does not exist."
        />
        <div class="mt-4">
          <UButton to="/users" color="gray" variant="soft">
            Back to list
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
        <!-- Status com USwitch -->
        <UFormField label="Status" name="active">
          <div class="flex items-center gap-3">
            <USwitch v-model="form.active" :disabled="loading" />
            <span class="text-sm text-gray-600 dark:text-gray-300">
              {{ form.active ? 'Active' : 'Suspended' }}
            </span>
          </div>
        </UFormField>

        <!-- Confirmação visual -->
        <UAlert
          color="yellow"
          variant="soft"
          icon="i-heroicons-exclamation-triangle"
          :title="form.active ? 'Activate user' : 'Suspend user'"
          :description="`You are about to change status from ${user.active ? 'Active' : 'Suspended'} to ${form.active ? 'Active' : 'Suspended'}.`"
        />

        <!-- Error -->
        <UAlert
          v-if="errorMessage"
          icon="i-heroicons-exclamation-circle"
          color="red"
          variant="soft"
          title="Update failed"
          :description="errorMessage"
        />

        <!-- Actions -->
        <div class="flex justify-between items-center">
          <UButton :to="`/users`" color="primary" variant="outline" :disabled="loading">
            Cancel
          </UButton>

          <UButton
            type="submit"
            :loading="loading"
            color="primary"
            icon="i-heroicons-shield-exclamation"
          >
            Confirm status change
          </UButton>
        </div>
      </UForm>
    </UCard>
  </div>
</template>

<script setup>
import { z } from 'zod'
import { useRoute, useRouter } from 'vue-router'
import { useUsersStore } from '~/stores/users.js'
import { useToast } from '#imports'

const route = useRoute()
const router = useRouter()
const usersStore = useUsersStore()
const toast = useToast()

const user = computed(() =>
  usersStore.findByUsername(route.params.username)
)

const form = ref({
  active: true
})

const errorMessage = ref('')
const loading = ref(false)

const schema = z.object({
  active: z.boolean()
})

watchEffect(() => {
  if (user.value) {
    form.value.active = user.value.active
  }
})

const handleSubmit = async () => {
  if (!user.value) return

  errorMessage.value = ''
  loading.value = true

  try {
    const from = user.value.active ? 'Active' : 'Suspended'
    const to = form.value.active ? 'Active' : 'Suspended'

    const ok = confirm(
      `Are you sure you want to change status of "${user.value.username}" from ${from} to ${to}?`
    )

    if (!ok) {
      loading.value = false
      return
    }

    // EP10 – usa o ID que a API espera no /users/{id}/status
    await usersStore.setUserStatus(
      user.value.username,
      form.value.active,
      'Changed from status page'
    )

    toast.add({
      title: 'Status updated',
      description: `User status changed to ${to} successfully.`,
      color: 'green',
      icon: 'i-heroicons-check-circle'
    })

    await router.push(`/users`)
  } catch (err) {
    console.error(err)
    errorMessage.value = 'Could not change status. Please try again.'
  } finally {
    loading.value = false
  }
}
</script>
