<template>
  <div class="min-h-screen px-4 sm:px-6 lg:px-8 py-10 bg-gray-50 dark:bg-gray-950">
    <UCard class="max-w-4xl mx-auto shadow-xl ring-1 ring-gray-200 dark:ring-gray-800 bg-white dark:bg-gray-900">
      <template #header>
        <div class="flex items-start justify-between gap-4">
          <div>
            <h1 class="text-2xl font-bold text-gray-900 dark:text-white">Histórico de edições</h1>
            <p class="text-sm text-gray-500 mt-1">
              Publicação #<strong>{{ pubId }}</strong>
            </p>
          </div>

          <div class="flex items-center gap-2">
            <UButton icon="i-heroicons-arrow-left" color="gray" variant="ghost" :to="`/publications`" />
            <UButton
              icon="i-heroicons-arrow-path"
              color="primary"
              variant="soft"
              :loading="loading"
              @click="fetchHistory"
            >
              Atualizar
            </UButton>
          </div>
        </div>
      </template>

      <div v-if="loading" class="py-10 text-center text-gray-500">
        A carregar...
      </div>

      <div v-else>
        <UAlert
          v-if="error"
          color="red"
          variant="soft"
          icon="i-heroicons-exclamation-triangle"
          title="Erro"
          :description="error"
          class="mb-4"
        />

        <div v-if="history.length === 0" class="py-10 text-center text-gray-500">
          Sem histórico disponível.
        </div>

        <div v-else class="space-y-3">
          <div
            v-for="(h, idx) in history"
            :key="h.modifiedAt ?? idx"
            class="rounded-xl border border-gray-200 dark:border-gray-800 p-4"
          >
            <div class="flex items-start justify-between gap-4">
              <div class="min-w-0">
                <p class="font-semibold text-gray-900 dark:text-white">
                  {{ h.description || 'Atualização' }}
                </p>

                <div class="flex flex-wrap items-center gap-3 mt-2 text-xs text-gray-400">
                  <span v-if="h.editor">👤 {{ h.editor }}</span>
                  <span v-if="h.modifiedAt">📅 {{ h.modifiedAt }}</span>
                </div>
              </div>

              <UBadge color="gray" variant="soft">
                Editado
              </UBadge>
            </div>
          </div>
        </div>
      </div>
    </UCard>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAPIStore } from '~/stores/api'

definePageMeta({ layout: 'default' })

const route = useRoute()
const apiStore = useAPIStore()

const pubId = computed(() => route.params.id)

const history = ref([])
const loading = ref(false)
const error = ref(null)

const fetchHistory = async () => {
  loading.value = true
  error.value = null
  try {
    const data = await apiStore.getPublicationHistory(pubId.value)

    // ordenar por data desc (se vierem várias)
    history.value = (Array.isArray(data) ? data : []).sort((a, b) => {
      const da = new Date(a.modifiedAt || 0).getTime()
      const db = new Date(b.modifiedAt || 0).getTime()
      return db - da
    })
  } catch (e) {
    error.value = e?.response?.data?.mensagem || e?.response?.data || e?.message || 'Erro ao carregar histórico.'
  } finally {
    loading.value = false
  }
}

onMounted(fetchHistory)
</script>
