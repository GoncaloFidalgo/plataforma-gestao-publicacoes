<template>
  <div class="min-h-screen px-4 sm:px-6 lg:px-8 py-10 bg-gray-50 dark:bg-gray-950">
    <UCard class="max-w-4xl mx-auto shadow-xl ring-1 ring-gray-200 dark:ring-gray-800 bg-white dark:bg-gray-900">
      <template #header>
        <div class="flex items-start justify-between gap-4">
          <div>
            <h1 class="text-2xl font-bold text-gray-900 dark:text-white">As minhas tags</h1>
            <p class="text-sm text-gray-500 mt-1">Tags que estás a seguir</p>
          </div>

          <div class="flex flex-col sm:flex-row sm:items-center gap-3 w-full sm:w-auto">
            <USelectMenu
              v-model="selectedArea"
              :items="areaItems"
              class="w-full sm:w-56"
            />

          </div>

          <UButton
            icon="i-heroicons-arrow-path"
            color="primary"
            variant="soft"
            :loading="loading"
            @click="refresh"
          >
            Atualizar
          </UButton>
        </div>
      </template>

      <div v-if="loading" class="py-10 text-center text-gray-500">
        A carregar...
      </div>

      <div v-else>
        <div v-if="filteredSubscribed.length === 0" class="py-10 text-center text-gray-500">
          Não tens tags subscritas.
        </div>

        <div v-else class="space-y-3">
          <div
            v-for="t in filteredSubscribed"
            :key="t.name"
            class="flex items-center justify-between gap-4 rounded-xl border border-gray-200 dark:border-gray-800 p-4"
          >
            <div class="min-w-0">
              <div class="flex items-center gap-2">
                <p class="font-semibold text-gray-900 dark:text-white truncate">
                  {{ t.name }}
                </p>
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

            <div class="flex items-center gap-2 shrink-0">
              <UTooltip text="Remover subscrição">
                <UButton
                  icon="i-heroicons-x-mark"
                  color="red"
                  variant="soft"
                  @click="openConfirm(t.name)"
                />
              </UTooltip>
            </div>
          </div>
        </div>
      </div>
    </UCard>

    <!-- Confirm modal -->
    <UModal
      v-model:open="confirmOpen"
      title="Remover subscrição"
      :description="`Queres remover a subscrição da tag “${confirmTag}”?`"
    >
      <template #body>
        <p class="text-sm text-gray-600 dark:text-gray-300">
          Esta ação vai remover a tua subscrição de <span class="font-semibold">“{{ confirmTag }}”</span>.
        </p>
      </template>

      <template #footer>
        <div class="flex justify-end gap-2">
          <UButton color="primary" variant="outline" @click="confirmOpen = false">
            Cancelar
          </UButton>
          <UButton color="primary" :loading="actionLoading" @click="confirmUnsubscribe">
            Remover
          </UButton>
        </div>
      </template>
    </UModal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useToast } from '#imports'
import { useTagStore } from '~/stores/tags'

const tagStore = useTagStore()
const toast = useToast()

const loading = ref(false)
const actionLoading = ref(false)

const selectedArea = ref('Todas as áreas')

const confirmOpen = ref(false)
const confirmTag = ref('')

const normalize = (s) => (s ?? '').toString().trim()

const refresh = async () => {
  loading.value = true
  try {
    // EP28
    await tagStore.fetchMySubscribedTags()
  } catch (e) {
    toast.add({
      title: 'Erro',
      description: e?.response?.data?.mensagem || e?.response?.data || 'Não foi possível carregar as tags subscritas.',
      color: 'red'
    })
  } finally {
    loading.value = false
  }
}

onMounted(refresh)

// Dropdown areas (a partir das tags subscritas)
const areaItems = computed(() => {
  const set = new Set()
  for (const t of (tagStore.mySubscribedTags || [])) {
    const a = normalize(t?.scientific_area)
    if (a) set.add(a)
  }
  return ['Todas as áreas', ...Array.from(set).sort((a, b) => a.localeCompare(b))]
})

const filteredSubscribed = computed(() => {
  const area = selectedArea.value === 'Todas as áreas' ? '' : normalize(selectedArea.value)

  return (tagStore.mySubscribedTags || []).filter(t => {
    return !area || normalize(t?.scientific_area) === area
  })
})


const openConfirm = (tagName) => {
  confirmTag.value = tagName
  confirmOpen.value = true
}

const confirmUnsubscribe = async () => {
  actionLoading.value = true
  try {
    await tagStore.unsubscribeTag(confirmTag.value) // EP27
    toast.add({ title: 'Subscrição removida', description: `Removeste a subscrição de “${confirmTag.value}”.` })
    confirmOpen.value = false
    await refresh()
  } catch (e) {
    toast.add({
      title: 'Erro',
      description: e?.response?.data?.mensagem || e?.response?.data || 'Não foi possível remover a subscrição.',
      color: 'red'
    })
  } finally {
    actionLoading.value = false
  }
}
</script>
