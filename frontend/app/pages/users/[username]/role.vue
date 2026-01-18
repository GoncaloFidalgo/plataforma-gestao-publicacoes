<template>
  <div class="flex min-h-screen w-full items-start justify-center">
    <UCard class="w-full max-w-xl shadow-xl ring-1 ring-gray-200 dark:ring-gray-800 bg-white dark:bg-gray-900">
      <template #header>
        <h1 class="text-2xl font-bold text-gray-900 dark:text-white">
          Change User Role
        </h1>
        <p class="text-sm text-gray-500 mt-1" v-if="user">
          User: <strong>{{ user.username }}</strong> (current role: {{ user.roleType }})
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
        <!-- Novo role -->
        <UFormField label="New Role" name="role">
          <USelect
            v-model="form.role"
            :items="roles"
            placeholder="Select role"
            :disabled="loading"
          />
        </UFormField>

        <!-- Confirmação visual -->
        <UAlert
          color="yellow"
          variant="soft"
          icon="i-heroicons-exclamation-triangle"
          title="Confirm role change"
          :description="`You are about to change role from ${user.roleType} to ${newRoleType}. This may affect permissions.`"
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
            Confirm role change
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

// o que é enviado para a API
const form = ref({
  role: 3 // default, depois é substituído pelo watch
})

// opções do dropdown
const roles = [
  { label: 'Administrator', value: 1 },
  { label: 'Responsavel', value: 2 },
  { label: 'Colaborador', value: 3 }
]

// texto bonito para o novo role
const newRoleType = computed(() => {
  const found = roles.find(r => Number(r.value) === Number(form.value.role))
  return found ? found.label : ''
})

const errorMessage = ref('')
const loading = ref(false)

const schema = z.object({
  // se quiseres ter a certeza que é número:
  role: z.coerce.number().int().min(1, 'Role must be >= 1')
})

// sincronizar com o user atual
watchEffect(() => {
  if (user.value) {
    form.value.role = user.value.role
  }
})

const handleSubmit = async () => {
  if (!user.value) return

  errorMessage.value = ''
  loading.value = true

  try {
    const ok = confirm(
      `Are you sure you want to change role of "${user.value.username}" ` +
      `from ${user.value.roleType} (${user.value.role}) ` +
      `to ${newRoleType.value} (${form.value.role})?`
    )

    if (!ok) {
      loading.value = false
      return
    }

    // garantir que vai número para a store/API
    const newRole = Number(form.value.role)

    await usersStore.setUserRole(user.value.username, newRole)

    toast.add({
      title: 'Role updated',
      description: `User role changed to ${newRoleType.value} (${newRole}).`,
      color: 'green',
      icon: 'i-heroicons-check-circle'
    })

    await router.push(`/users`)
  } catch (err) {
    console.error(err)
    errorMessage.value = 'Could not change role. Please try again.'
  } finally {
    loading.value = false
  }
}
</script>
