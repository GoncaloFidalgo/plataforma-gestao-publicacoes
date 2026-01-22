<template>
  <div class="min-h-screen px-4 sm:px-6 lg:px-8 py-10 bg-gray-50 dark:bg-gray-950">
    <UCard class="max-w-4xl mx-auto shadow-xl ring-1 ring-gray-200 dark:ring-gray-800 bg-white dark:bg-gray-900">
      <template #header>
        <div class="flex items-start justify-between gap-4">
          <div>
            <h1 class="text-2xl font-bold text-gray-900 dark:text-white">Tags</h1>
          </div>
          <div class="flex flex-col sm:flex-row sm:items-center gap-3 w-full sm:w-auto">
            <USelectMenu v-model="selectedArea" :items="areaItems" class="w-full sm:w-56" />


            <UInput v-model="search" icon="i-heroicons-magnifying-glass" placeholder="Procurar tag pelo nome..."
              class="w-full sm:w-80" />
            <UButton color="gray" variant="soft" icon="i-heroicons-x-mark" @click="clearFilters">
              Limpar
            </UButton>
          </div>

          <UButton icon="i-heroicons-arrow-path" color="primary" variant="soft" :loading="tagStore.loading"
            @click="tagStore.refreshAll()">
            Atualizar
          </UButton>
        </div>
      </template>

      <div v-if="tagStore.loading" class="py-10 text-center text-gray-500">
        A carregar...
      </div>

      <div v-else class="space-y-3">
        <div v-for="t in filteredTags" :key="t.name"
          class="flex items-center justify-between gap-4 rounded-xl border border-gray-200 dark:border-gray-800 p-4">
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
            <UTooltip :text="isSubscribed(t.name) ? 'Remover subscrição' : 'Subscrever'">
              <template v-if="isSubscribed(t.name)">
                <UButton :key="`unsub-${t.name}`" icon="i-heroicons-x-mark" color="red" variant="soft"
                  @click="openConfirm(t.name)" />
              </template>
              <template v-else>
                <UButton :key="`sub-${t.name}`" icon="i-heroicons-check" color="primary" variant="soft"
                  @click="openConfirm(t.name)" />
              </template>
            </UTooltip>

          </div>
        </div>
      </div>
    </UCard>

    <!-- Modal de confirmação -->
    <UModal v-model:open="confirmOpen" :title="confirmMode === 'subscribe' ? 'Subscrever tag' : 'Remover subscrição'"
      :description="`Confirma a ação para a tag “${confirmTag}”.`">
      <template #body>
        <p class="text-sm text-gray-600 dark:text-gray-300">
          Tens a certeza que queres
          <strong>{{ confirmMode === 'subscribe' ? 'subscrever ' : 'remover a subscrição de ' }}</strong>
          <span class="font-semibold">“{{ confirmTag }}”</span>?
        </p>
      </template>

      <template #footer>
        <div class="flex justify-end gap-2">
          <UButton color="primary" variant="outline" @click="confirmOpen = false">
            Cancelar
          </UButton>

          <UButton :color="confirmMode === 'subscribe' ? 'primary' : 'primary'" :loading="actionLoading"
            @click="confirmAction">
            Confirmar
          </UButton>
        </div>
      </template>
    </UModal>

  </div>
</template>

<script setup>
import { ref, onMounted, computed, watchEffect } from 'vue'
import { useToast } from '#imports'
import { useTagStore } from '~/stores/tags'

const tagStore = useTagStore()
const toast = useToast()
const search = ref('')
const selectedArea = ref('Todas as áreas')
const confirmOpen = ref(false)
const confirmTag = ref('')
const confirmMode = ref('subscribe') // 'subscribe' | 'unsubscribe'
const actionLoading = ref(false)

const subscribedNames = computed(() =>
  new Set((tagStore.mySubscribedTags || []).map(t => (t?.name || '').trim().toLowerCase()))
)

const isSubscribed = (tagName) =>
  subscribedNames.value.has((tagName || '').trim().toLowerCase())



const openConfirm = (tagName) => {
  confirmTag.value = tagName
  confirmMode.value = isSubscribed(tagName) ? 'unsubscribe' : 'subscribe'
  confirmOpen.value = true
}

const areaItems = computed(() => {
  const set = new Set()

  for (const t of (tagStore.tags || [])) {
    const a = (t?.scientific_area ?? '').toString().trim()
    if (a) set.add(a)
  }

  return ['Todas as áreas', ...Array.from(set).sort((a, b) => a.localeCompare(b))]
})



const filteredTags = computed(() => {
  const q = search.value.trim().toLowerCase()
  const area = selectedArea.value === 'Todas as áreas' ? '' : (selectedArea.value || '').trim()

  return (tagStore.tags || []).filter(t => {
    const nameOk = !q || (t?.name || '').toLowerCase().includes(q)
    const areaOk = !area || (t?.scientific_area || '').toString().trim() === area
    return nameOk && areaOk
  })
})



const clearFilters = () => {
  search.value = ''
  selectedArea.value = 'Todas as áreas'
}

const confirmAction = async () => {
  actionLoading.value = true
  try {
    if (confirmMode.value === 'subscribe') {
      await tagStore.subscribeTag(confirmTag.value)
      toast.add({ title: 'Subscrito', description: `Agora estás subscrito em “${confirmTag.value}”.` })
    } else {
      await tagStore.unsubscribeTag(confirmTag.value)
      toast.add({ title: 'Subscrição removida', description: `Removeste a subscrição de “${confirmTag.value}”.` })
    }
    confirmOpen.value = false
  } catch (e) {
    toast.add({
      title: 'Erro',
      description: e?.response?.data?.mensagem || e?.response?.data || 'Ocorreu um erro.',
      color: 'red'
    })
  } finally {
    actionLoading.value = false
  }
}

onMounted(async () => {
  await tagStore.refreshAll()
})
</script>
