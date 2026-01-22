<template>
  <div class="min-h-screen px-4 sm:px-6 lg:px-8 py-10 bg-gray-50 dark:bg-gray-950">
    <UCard class="max-w-4xl mx-auto shadow-xl ring-1 ring-gray-200 dark:ring-gray-800 bg-white dark:bg-gray-900">
      <template #header>
        <div class="flex items-start justify-between gap-4">
          <div>
            <h1 class="text-2xl font-bold text-gray-900 dark:text-white"><strong>{{ username }}</strong></h1>
            <p class="text-sm text-gray-500 mt-1">
              Tags Subscritas
            </p>
          </div>

          <USelectMenu v-model="selectedArea" :items="areaItems" class="w-full sm:w-56" />

          <UButton
            icon="i-heroicons-arrow-path"
            color="primary"
            variant="soft"
            :loading="tagStore.loadingSubscribedTags"
            @click="refresh"
          >
            Atualizar
          </UButton>
        </div>
      </template>

      <div v-if="tagStore.loadingSubscribedTags" class="py-10 text-center text-gray-500">
        A carregar...
      </div>

      <div v-else>
        <div v-if="filteredTags.length === 0" class="py-10 text-center text-gray-500">
          Este utilizador não tem tags subscritas.
        </div>

        <div v-else class="space-y-3">
          <div
            v-for="t in filteredTags"
            :key="t.name"
            class="flex items-center justify-between gap-4 rounded-xl border border-gray-200 dark:border-gray-800 p-4"
          >
            <div class="min-w-0">
              <div class="flex items-center gap-2">
                <p class="font-semibold text-gray-900 dark:text-white truncatetruncate">
                  {{ t.name }}</p>
                <UBadge v-if="t.scientific_area" color="gray" variant="soft">
                  {{ t.scientific_area }}
                </UBadge>
              </div>

              <p v-if="t.description" class="text-sm text-gray-500 mt-1 line-clamp-2">
                {{ t.description }}
              </p>

              <p class="text-xs text-gray-400 mt-2">
                Subscritores: <strong>{{ t.subscritores_count ?? 0 }}</strong>
              </p>
            </div>
          </div>
        </div>
      </div>
    </UCard>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'


const route = useRoute()
const tagStore = useTagStore()

const username = computed(() => route.params.username)

const selectedArea = ref('Todas as áreas')
const normalize = (s) => (s ?? '').toString().trim()

const refresh = async () => {
  await tagStore.fetchSubscribedTagsOfUser(username.value)
}

onMounted(refresh)

const areaItems = computed(() => {
  const set = new Set()
  for (const t of (tagStore.subscribedTagsOfUser || [])) {
    const a = normalize(t?.scientific_area)
    if (a) set.add(a)
  }
  return ['Todas as áreas', ...Array.from(set).sort((a, b) => a.localeCompare(b))]
})

const filteredTags = computed(() => {
  const area = selectedArea.value === 'Todas as áreas' ? '' : normalize(selectedArea.value)
  return (tagStore.subscribedTagsOfUser || []).filter(t => !area || normalize(t?.scientific_area) === area)
})
</script>
