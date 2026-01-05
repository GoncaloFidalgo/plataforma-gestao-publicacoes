<template>
  <section class="app-page users-page">
    <div class="users-card">
      <header class="users-header">
        <div>
          <h1 class="users-title">
            Gestão de Utilizadores
          </h1>
          <p class="users-subtitle">
            Administra contas, roles e estados de acesso à plataforma.
          </p>
        </div>

        <NuxtLink to="/administrators/create" class="users-create-btn">
          Criar utilizador
        </NuxtLink>
      </header>

      <div class="table-wrapper">
        <table class="data-table users-table">
          <thead>
            <tr>
              <th>Nome</th>
              <th>Username</th>
              <th>Email</th>
              <th>Role</th>
              <th>Estado</th>
              <th class="text-right pr-4">Ações</th>
            </tr>
          </thead>

          <tbody>
            <tr v-for="user in users" :key="user.username">
              <td>
                <NuxtLink :to="`/administrators/${user.username}`" class="link-quiet">
                  {{ user.name }}
                </NuxtLink>
              </td>
              <td class="font-mono text-xs">
                {{ user.username }}
              </td>
              <td>
                {{ user.email }}
              </td>
              <td>
                <span class="role-pill">
                  {{ user.roleType}}
                </span>
              </td>
              <td>
                <span
                  class="status-pill"
                  :class="user.active ? 'status-pill--active' : 'status-pill--suspended'"
                >
                  {{ user.active ? 'Ativo' : 'Suspenso' }}
                </span>
              </td>
             <td class="actions-cell">
                <!-- Edit -->
                <NuxtLink :to="`/administrators/${user.username}/edit`" class="icon-btn" title="Editar utilizador">
                  <UIcon name="i-heroicons-pencil-square" class="w-5 h-5" />
                </NuxtLink>

                <!-- Role -->
                <NuxtLink :to="`/administrators/${user.username}/role`" class="icon-btn icon-btn--soft" title="Alterar role">
                  <UIcon name="i-heroicons-shield-check" class="w-5 h-5" />
                </NuxtLink>

                <!-- Change Status -->
                <NuxtLink :to="`/administrators/${user.username}/status`" class="icon-btn icon-btn--warning" title="Alterar estado">
                  <UIcon name="i-heroicons-arrow-right-end-on-rectangle" class="w-5 h-5" />
                </NuxtLink>

                <!-- Remove -->
                <button @click="remove(user)" class="icon-btn icon-btn--danger" title="Remover utilizador">
                  <UIcon name="i-heroicons-trash" class="w-5 h-5" />
                </button>
              </td>
            </tr>

            <tr v-if="!users || users.length === 0">
              <td colspan="6" class="empty-state">
                Ainda não existem utilizadores registados.
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useUsersStore } from '~/stores/users'

const usersStore = useUsersStore()

// ligar a tabela ao estado do store
const users = computed(() => usersStore.users)
onMounted(async () => {
  try {
    await usersStore.fetchUsers()
  } catch (e) {
    console.error('Erro a carregar utilizadores:', e)
  }
})

async function toggleStatus(user) {
  await usersStore.setUserStatus(user.id, !user.active, 'Alterado pela listagem')
}

async function remove(user) {
  if (confirm(`Remover o utilizador ${user.username}?`)) {
    await usersStore.deleteUser(user.username)
  }
}
</script>

