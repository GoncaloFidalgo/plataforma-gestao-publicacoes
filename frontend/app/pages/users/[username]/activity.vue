<template>
  <div class="min-h-screen px-4 sm:px-6 lg:px-8 py-10 bg-gray-50 dark:bg-gray-950">
    <UCard class="max-w-4xl mx-auto shadow-xl ring-1 ring-gray-200 dark:ring-gray-800 bg-white dark:bg-gray-900">
      <template #header>
        <div class="flex items-start justify-between gap-4">
          <div>
            <h1 class="text-2xl font-medium text-gray-900 dark:text-white">Atividade do <strong>{{ userId }}</strong></h1>
          </div>

          <div class="flex items-center gap-3">
            <USelectMenu v-model="selectedType" :items="typeItems" class="w-48" />
            <UButton
              icon="i-heroicons-arrow-path"
              color="primary"
              variant="soft"
              :loading="activityStore.loadingUserActivity"
              @click="refresh"
            >
              Atualizar
            </UButton>
          </div>
        </div>
      </template>

      <div v-if="activityStore.loadingUserActivity" class="py-10 text-center text-gray-500">
        A carregar...
      </div>

      <div v-else>
        <div v-if="filteredActivity.length === 0" class="py-10 text-center text-gray-500">
          Sem atividade para mostrar.
        </div>

        <div v-else class="space-y-3">
          <div
            v-for="(a, idx) in filteredActivity"
            :key="a.id ?? `${a.tipo}-${a.publicacaoId ?? ''}-${idx}`"
            class="rounded-xl border border-gray-200 dark:border-gray-800 p-4"
          >
            <div class="flex items-start justify-between gap-4">
              <div class="min-w-0">
                <div class="flex items-center gap-2">
                  <p class="font-semibold text-gray-900 dark:text-white">
                    {{ titleFor(a) }}
                  </p>
                </div>

                <p class="text-sm text-gray-500 mt-1">
                  {{ a.descricao || '' }}
                </p>

                <div class="flex items-center gap-3 mt-2 text-xs text-gray-400">
                  <span v-if="a.data">📅 {{ a.data }}</span>
                  <span v-if="a.publicacaoId">📄 Pub: #{{ a.publicacaoId }}</span>
                  <span v-if="a.comentarioId">💬 Coment: #{{ a.comentarioId }}</span>
                  <span v-if="a.rating != null">⭐ Rating: {{ a.rating }}</span>
                </div>
              </div>

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
import { useActivityStore } from '~/stores/activity'

const route = useRoute()
const activityStore = useActivityStore()

const userId = computed(() => route.params.username) // ou route.params.id

const selectedType = ref('Todos os tipos')

const normalize = (s) => (s ?? '').toString().trim()

const refresh = async () => {
  await activityStore.fetchUserActivity(userId.value)
}

onMounted(refresh)

const typeItems = computed(() => {
  const set = new Set()
  for (const a of (activityStore.userActivity || [])) {
    const t = normalize(a?.tipo)
    if (t) set.add(t)
  }
  return ['Todos os tipos', ...Array.from(set).sort((a, b) => a.localeCompare(b))]
})

const filteredActivity = computed(() => {
  const t = selectedType.value === 'Todos os tipos' ? '' : normalize(selectedType.value)
  return (activityStore.userActivity || []).filter(a => !t || normalize(a?.tipo) === t)
})

const titleFor = (a) => {
  switch (a?.tipo) {
    case 'upload': return 'Upload'
    case 'comentario': return 'Comentário'
    case 'rating': return 'Rating'
    default: return 'Atividade'
  }
}
</script>
