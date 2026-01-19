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

        <NuxtLink to="/users/create" class="users-create-btn">
          Criar utilizador
        </NuxtLink>
      </header>

      <div class="table-wrapper">
        <UCard :ui="{ body: { padding: '' } }">
          <UTable :data="filteredUsers" :columns="columns" :loading="userStore.loading" class="data-table users-table">
            <!-- Name  -->
            <template #name-cell="{ row }">
              <NuxtLink :to="`/users/${row.original.username}`" class="link-quiet">
                {{ row.original.name }}
              </NuxtLink>
            </template>

            <!--  Username -->
            <template #username-cell="{ row }">
                <span class="font-mono text-xs">
                    {{ row.original.username }}
                </span>
            </template>

            <!--  Role-->
            <template #roleType-cell="{ row }">
                <span class="role-pill">
                  {{ row.original.roleType }}
                </span>
            </template>

            <!--  Status  -->
            <template #active-cell="{ row }">
                <span class="status-pill" :class="row.original.active ? 'status-pill--active' : 'status-pill--suspended'">
                  {{ row.original.active ? 'Ativo' : 'Suspenso' }}
                </span>
            </template>

            <!-- Actions  -->
            <template #actions-cell="{ row }">
              <div class="actions-cell flex items-center justify-end gap-1.5">

                <!-- Edit -->
                <UTooltip text="Editar utilizador">
                  <UButton icon="i-heroicons-pencil-square" size="md" variant="ghost" class="icon-btn"
                           :to="`/users/${row.original.username}/edit`"/>
                </UTooltip>

                <!-- Role -->
                <UTooltip text="Alterar role">
                  <UButton icon="i-heroicons-shield-check" size="md" variant="ghost" class="icon-btn icon-btn--soft"
                           :to="`/users/${row.original.username}/role`"
                  />
                </UTooltip>

                <!-- Change Status -->
                <UTooltip text="Alterar estado">
                  <UButton icon="i-heroicons-arrow-right-end-on-rectangle" size="md" variant="ghost"
                           class="icon-btn icon-btn--warning"
                           :to="`/users/${row.original.username}/status`"/>
                </UTooltip>

                <!-- Remove -->
                <UTooltip text="Remover utilizador">
                  <UButton icon="i-heroicons-trash" size="md" variant="ghost" class="icon-btn icon-btn--danger"
                           @click="confirmDelete(row.original)"/>
                </UTooltip>

              </div>
            </template>
          </UTable>
        </UCard>

      </div>
    </div>
    <ConfirmDeleteModal
        v-model="isDeleteModalOpen"
        type="user"
        :item-name="itemToDelete?.name"
        :loading="deleting"
        @confirm="executeDelete"
    />
  </section>
</template>

<script setup>
import {computed, onMounted} from 'vue'
import {useUserStore} from '~/stores/user.js'

const toast = useToast()

definePageMeta({
  middleware: 'admin'
})
const userStore = useUserStore()

const columns = [
  {accessorKey: 'name', header: 'Nome'},
  {accessorKey: 'username', header: 'Username'},
  {accessorKey: 'email', header: 'Email'},
  {accessorKey: 'roleType', header: 'Role'},
  {accessorKey: 'active', header: 'Estado'},
  {id: 'actions', header: 'Ações', meta: {
      class: {
        th: 'text-center',
      }
    },}
]

onMounted(async () => {
  try {
    await userStore.fetchUsers()
  } catch (e) {
    console.error('Erro a carregar utilizadores:', e)
  }
})
const filteredUsers = computed(() => userStore.users)
const isDeleteModalOpen = ref(false)
const itemToDelete = ref(null)
const deleting = ref(false)

const confirmDelete = (item) => {
  itemToDelete.value = item
  isDeleteModalOpen.value = true
}

const executeDelete = async () => {
  if (!itemToDelete.value) return
  deleting.value = true
  try {
    await userStore.deleteUser(itemToDelete.value.username)
    toast.add({title: 'User deleted', color: 'green'})
    isDeleteModalOpen.value = false
  } catch (error) {
    const msg = error.response?.data?.mensagem || 'Error deleting user'
    toast.add({title: 'Error', description: msg, color: 'red'})
  } finally {
    deleting.value = false
  }
}
</script>

