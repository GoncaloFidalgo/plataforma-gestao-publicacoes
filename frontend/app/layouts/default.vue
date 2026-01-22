<template>
  <div class="flex h-screen w-full bg-gray-50 dark:bg-gray-900 overflow-hidden">

    <!-- DASHBOARD SIDEBAR -->
    <UDashboardSidebar
        resizable
        class="bg-white dark:bg-gray-900 border-r border-gray-200 dark:border-gray-800"
        :ui="{ footer: 'border-t border-gray-200 dark:border-gray-800' }"
    >

      <!-- HEADER: LOGO -->
      <template #header>
        <div class="flex items-center w-full px-2">
          <NuxtLink to="/" class="flex items-center gap-2 font-bold text-xl text-orange-500 min-w-0">
            <UIcon name="i-heroicons-cube-transparent" class="w-8 h-8 flex-shrink-0" />
            <span class="truncate">Plataforma</span>
          </NuxtLink>
        </div>
      </template>

      <!-- BODY: NAVIGATION LINKS -->
      <template #default>

        <!-- Group 1: General -->
        <UNavigationMenu
            :items="mainLinks"
            orientation="vertical"
        />

        <!-- Group 2: Management (Separator + Links) -->
        <div v-if="managementLinks.length > 0" class="mt-4">
          <div class="px-3 mb-2 text-xs font-semibold text-gray-500 uppercase tracking-wider">
            Management
          </div>
          <UNavigationMenu
              :items="managementLinks"
              orientation="vertical"
          />
        </div>

      </template>

      <!-- FOOTER: USER PROFILE -->
      <template #footer>
        <ClientOnly>
          <template v-if="authStore.currentUser">
            <!-- User Dropdown Menu -->
            <UDropdownMenu
                :items="userDropdownItems"
                :content="{ align: 'start', side: 'right', sideOffset: 8 }"
            >
              <UButton
                  color="neutral"
                  variant="ghost"
                  class="w-full justify-start"
              >
                <template #leading>
                  <UAvatar :alt="authStore.currentUser.name" size="xs" />
                </template>

                <div class="text-left truncate">
                  <p class="font-medium text-sm text-gray-900 dark:text-white truncate">
                    {{ authStore.currentUser.name }}
                  </p>
                  <p class="text-xs text-gray-500 truncate">
                    {{ authStore.currentUser.roleType }}
                  </p>
                </div>

                <template #trailing>
                  <UIcon name="i-heroicons-ellipsis-vertical" class="w-5 h-5 ml-auto text-gray-400" />
                </template>
              </UButton>
            </UDropdownMenu>
          </template>

          <template v-else>
            <UButton
                to="/login"
                icon="i-heroicons-arrow-right-on-rectangle"
                label="Sign In"
                color="neutral"
                variant="ghost"
                block
            />
          </template>
        </ClientOnly>
      </template>

    </UDashboardSidebar>

    <!-- MAIN CONTENT AREA -->
    <div class="flex-1 flex flex-col min-w-0 overflow-hidden">
      <!-- Scrollable content -->
      <main class="flex-1 overflow-y-auto p-4 sm:p-6 lg:p-8">
        <slot />
      </main>

      <!-- Page Footer -->
<!--    <footer class="border-t border-gray-200 dark:border-gray-800 bg-white dark:bg-gray-900 py-3 px-6 text-center text-xs text-gray-500 flex-shrink-0">-->
<!--        &copy; {{ new Date().getFullYear() }} Plataforma DAE-->
<!--      </footer>-->

      <footer/>
    </div>

  </div>
</template>

<script setup lang="ts">
import type { NavigationMenuItem } from '@nuxt/ui'

const authStore = useAuthStore()

// 1. General Links
const mainLinks = computed<NavigationMenuItem[]>(() => [
  {
    label: 'Home',
    icon: 'i-heroicons-home',
    to: '/'
  },
  {
    label: 'Publications',
    icon: 'i-heroicons-document-text',
    to: '/publications'
  },
  {
    label: 'Tags',
    icon: 'i-heroicons-tag',
    to: '/tags'
  },
  {
    label: 'Upload New',
    icon: 'i-heroicons-cloud-arrow-up',
    to: '/publications/create'
  }
])

// 2. Management Links (Filtered by Role)
const managementLinks = computed<NavigationMenuItem[]>(() => {
  const links: NavigationMenuItem[] = []

  if (authStore.isAdmin || authStore.isResponsavel) {
    links.push({
      label: 'Tags Managment',
      icon: 'i-heroicons-tag',
      to: '/tags/managment'
    })
    links.push({
      label: 'Pub Types',
      icon: 'i-heroicons-document-duplicate',
      to: '/types'
    })

    links.push({
      label: 'Scientific Areas',
      icon: 'i-heroicons-academic-cap',
      to: '/areas'
    })
  }

  if (authStore.isAdmin) {
    links.push({
      label: 'Users',
      icon: 'i-heroicons-user-group',
      to: '/users'
    })
  }


  links.push({
    label: 'My Publications',
    icon: 'i-heroicons-document-magnifying-glass',
    to: '/me/publications'   //tive de trocar isto para to, porque estava a dar problemas
  })

  links.push({
    label: 'My Comments',
    icon: 'i-heroicons-chat-bubble-left-right',
    to: '/me/comments'      
  })

  links.push({
    label: 'My Tags',
    icon: 'i-heroicons-tag',
    to: '/me/tags'      
  })

  return links
})

// 3. User Dropdown (Logout)
const userDropdownItems = computed(() => [
  {
    label: 'My Account',
    icon: 'i-heroicons-user-circle',
    onSelect: () => navigateTo('/me')
  },
  {
    type: 'separator'
  },
  {
    label: 'Logout',
    icon: 'i-heroicons-arrow-left-on-rectangle',
    color: 'error',
    onSelect: () => authStore.logout()
  }
])
</script>