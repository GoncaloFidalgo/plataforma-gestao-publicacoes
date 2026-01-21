<template>
  <div class="flex min-h-screen w-full items-start justify-center">
    <UCard class="w-full max-w-xl shadow-xl ring-1 ring-gray-200 dark:ring-gray-800 bg-white dark:bg-gray-900">
      <template #header>
        <h1 class="text-2xl font-bold text-gray-900 dark:text-white">
          Criar utilizador
        </h1>
        <p class="text-sm text-gray-500 mt-1">
          Introduz os detalhes do novo utilizador.
        </p>
      </template>

      <UForm
        :state="form"
        @submit="onSubmit"
        class="space-y-6"
      >
        <!-- Nome -->
        <UFormField label="Nome" name="name">
          <UInput
            v-model="form.name"
            placeholder="Nome completo"
          />
        </UFormField>

        <!-- Username -->
        <UFormField label="Username" name="username">
          <UInput
            v-model="form.username"
            placeholder="Username"
          />
        </UFormField>

        <!-- Password -->
        <UFormField label="Password" name="password">
          <UInput
            v-model="form.password"
            type="password"
            placeholder="Password"
          />
        </UFormField>

        <!-- Email -->
        <UFormField label="Email" name="email">
          <UInput
            v-model="form.email"
            type="email"
            placeholder="email@exemplo.com"
          />
        </UFormField>

        <!-- Role (numérica) -->
        <UFormField label="New Role" name="role">
          <USelect
            v-model="form.role"
            :items="roles"
            placeholder="Select role"
          />
        </UFormField>

        <!-- Ativo -->
        <UFormField label="Estado" name="active">
          <div class="flex items-center gap-3">
            <USwitch v-model="form.active" />
            <span class="text-sm text-gray-600 dark:text-gray-300">
              {{ form.active ? 'Ativo' : 'Suspenso' }}
            </span>
          </div>
        </UFormField>

        <!-- Actions -->
        <div class="flex justify-between items-center">
          <UButton :to="`/users`" color="primary" variant="outline">
            Cancel
          </UButton>

          <UButton
            type="submit"
            color="primary"
            icon="i-heroicons-plus-circle"
          >
            Criar utilizador
          </UButton>
        </div>
      </UForm>
    </UCard>
  </div>
</template>


<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '~/stores/user.js'

const router = useRouter()
const usersStore = useUserStore()

const form = reactive({
  name: '',
  username: '',
  password: '',
  role: 3,
  email: '',
  active: true
})

// opções do dropdown
const roles = [
  { label: 'Administrator', value: 1 },
  { label: 'Responsavel', value: 2 },
  { label: 'Colaborador', value: 3 }
]


const onSubmit = async () => {

  try {
    const payload = { ...form, role: Number(form.role) }

    await usersStore.createUser(payload)



    await router.push(`/users`)
  } catch (error) {
    console.error('Erro ao criar utilizador:', error?.response?.data || error)
    alert('Erro ao criar utilizador.')
  }
}
</script>
