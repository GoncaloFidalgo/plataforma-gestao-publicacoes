<template>
  <div class="min-h-screen flex flex-col">

    <!-- Navbar -->
    <header class="bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700 sticky top-0 z-50">
      <div class="container mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">

        <!-- Left: App Title  -->
        <NuxtLink to="/" class="hover:opacity-80 transition-opacity">
          <span class="text-xl font-bold text-gray-900 dark:text-white">
            Plataforma
          </span>
        </NuxtLink>

        <!-- Right: User Dropdown -->
        <div class="flex items-center gap-4">
          <ClientOnly>
            <template v-if="authStore.currentUser">
              <UDropdownMenu :items="dropdownItems" :content="{ align: 'end', side: 'bottom' }">
                <UButton color="primary" variant="soft" trailing-icon="i-heroicons-chevron-down"
                 class="rounded-full px-3 font-medium text-orange-700 dark:text-orange-200 hover:bg-orange-200/60 dark:hover:bg-orange-500/20">
                  <UAvatar :alt="authStore.currentUser.name" size="xs" class="mr-1" />
                  {{ authStore.currentUser.name }}
                </UButton>
              </UDropdownMenu>
            </template>

            <template v-else>
              <USkeleton class="h-8 w-32" />
            </template>
          </ClientOnly>
        </div>

      </div>
    </header>

    <!-- Main Content -->
    <main class="flex-1 container mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <slot />
    </main>

    <!-- Footer -->
    <footer class="border-t border-gray-200 dark:border-gray-800 bg-white dark:bg-gray-900 py-6 mt-auto">
      <div class="container mx-auto px-4 text-center text-sm text-gray-500">
        &copy; {{ new Date().getFullYear() }} Plataforma DAE
      </div>
    </footer>

  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAuthStore } from '~/stores/auth'
import { useToast } from '#imports'

const authStore = useAuthStore()
const toast = useToast()
const dashboardEntry = computed(() => {
  const user = authStore.currentUser

  // Só admins e responsaveis têm dashboard
  if (!user || (!authStore.isAdmin && !authStore.isResponsavel)) {
    return null
  }

  // Itens visiveis às duas roles (Itens do responsavel são também acedidos pelo admin)
  // Estão adicionados por defeito
  const children = [
    {
      label: 'Tags',
      icon: 'i-heroicons-tag',
      onSelect: () => navigateTo('/tags')
    }
  ]

  // Itens só para o admin
  // Adicionados apenas se o user for admin
  if (authStore.isAdmin) {
    children.push({
      label: 'Users',
      icon: 'i-heroicons-user',
      onSelect: () => navigateTo('/users')
    })
  }

  return {
    label: `Dashboard`,
    icon: 'i-heroicons-table-cells',
    children: children
  }
})

const dropdownItems = computed(() => {
  // Aqui definem-se os itens disponiveis para todas as roles, pois se está disponivel para um colaborador, está para as outras duas roles.
  const mainGroup = [
    {
      label: 'My Account',
      icon: 'i-heroicons-user-circle',
      onSelect: () => navigateTo('/account')
    }
  ]

  // Adicionar a dashboard dos resp e admins
  if (dashboardEntry.value) {
    mainGroup.push({ type: 'separator' })
    mainGroup.push(dashboardEntry.value)
  }

  return [
    mainGroup,
    [{
      label: 'Logout',
      icon: 'i-heroicons-arrow-left-on-rectangle',
      color: 'error',
      onSelect: () => authStore.logout()
    }]
  ]
})

</script>