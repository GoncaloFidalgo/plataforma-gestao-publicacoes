<template>
  <div class="min-h-screen w-full px-4 sm:px-6 lg:px-8 py-10">
    <div class="mx-auto max-w-6xl space-y-6">

      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-gray-900 dark:text-white">
            My Ratings
          </h1>
          <p class="text-sm text-gray-500 mt-1">
            History of ratings you have given to publications
          </p>
        </div>

        <div class="flex items-center gap-4">
          <div v-if="!loading && !error" class="bg-primary-50 dark:bg-primary-900/10 border border-primary-200 dark:border-primary-800 rounded-lg px-4 py-2 flex items-center gap-3">
            <div class="flex flex-col">
              <span class="text-xs text-primary-600 dark:text-primary-400 font-bold uppercase tracking-wider">Average Given</span>
              <div class="flex items-center gap-1.5">
                <span class="text-2xl font-bold text-primary-600 dark:text-primary-400 leading-none">
                  {{ averageRating }}
                </span>
                <UIcon name="i-heroicons-star-solid" class="w-5 h-5 text-yellow-400" />
              </div>
            </div>
          </div>

          <UButton icon="i-heroicons-arrow-path" color="gray" variant="soft" :loading="loading" @click="refresh">
            Refresh
          </UButton>
        </div>
      </div>

      <UAlert v-if="loading && !ratingsList.length" color="gray" icon="i-heroicons-arrow-path"
              title="Loading ratings..." description="Please wait." />

      <UAlert v-else-if="error" color="red" variant="soft" icon="i-heroicons-exclamation-triangle" title="Error"
              :description="String(error)" />

      <UCard v-else-if="!ratingsList.length" class="bg-gray-50 dark:bg-gray-800">
        <div class="py-10 text-center">
          <h2 class="text-lg font-semibold text-gray-900 dark:text-white">
            No ratings found
          </h2>
          <p class="text-sm text-gray-500 mt-1">
            You haven't rated any publications yet.
          </p>
        </div>
      </UCard>

      <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        <UCard v-for="r in ratingsList" :key="r.id" class="flex flex-col h-full hover:shadow-md transition-shadow">
          
          <template #header>
            <div class="flex items-start justify-between gap-3">
              <div class="min-w-0">
                <h3 class="font-semibold text-gray-900 dark:text-white truncate" :title="r.tituloPublicacao">
                  {{ r.tituloPublicacao }}
                </h3>
                <p class="text-xs text-gray-500 mt-1">
                  ID Pub: {{ r.publicacaoId }}
                </p>
              </div>
              <UIcon name="i-heroicons-document-text" class="w-5 h-5 text-gray-400 flex-shrink-0" />
            </div>
          </template>

          <div class="flex items-center gap-1 py-2">
             <UIcon 
                v-for="star in 5" 
                :key="star"
                :name="star <= r.rating ? 'i-heroicons-star-solid' : 'i-heroicons-star'"
                class="w-6 h-6"
                :class="star <= r.rating ? 'text-yellow-400' : 'text-gray-300 dark:text-gray-600'"
             />
             <span class="ml-2 text-sm font-medium text-gray-700 dark:text-gray-300">
               {{ r.rating }}/5
             </span>
          </div>

          <template #footer>
            <div class="flex items-center justify-between text-xs text-gray-500 dark:text-gray-400">
              <div class="flex items-center">
                <UIcon name="i-heroicons-calendar" class="w-4 h-4 mr-1" />
                {{ r.data }}
              </div>
            </div>
          </template>
        </UCard>
      </div>

    </div>
  </div>
</template>

<script setup>
import { useAPIStore } from '~/stores/api.js'

definePageMeta({
  layout: 'default',
})

const apiStore = useAPIStore()
const loading = ref(false)
const error = ref(null)

const averageRating = ref(0)
const ratingsList = ref([])

onMounted(async () => {
  await refresh()
})

const refresh = async () => {
  loading.value = true
  error.value = null
  try {
    const data = await apiStore.getMyRatings()
    
    if (data) {
      averageRating.value = data.ratingMedio || 0
      ratingsList.value = data.ratings || []
    }
  } catch (e) {
    console.error(e)
    error.value = e?.response?.data?.mensagem || 'Could not load ratings'
  } finally {
    loading.value = false
  }
}
</script>