<template>
    <div class="min-h-screen w-full px-4 sm:px-6 lg:px-8 py-10">
        <div class="mx-auto max-w-6xl space-y-6">

            <!-- Header -->
            <div class="flex items-start justify-between gap-4">
                <div>
                    <h1 class="text-2xl font-bold text-gray-900 dark:text-white">
                        My Publications
                    </h1>
                    <p class="text-sm text-gray-500 mt-1">
                        Publications you created or co-authored
                    </p>
                </div>

            </div>

            <!-- Loading -->
            <UAlert v-if="loading && !publications.length" color="gray" icon="i-heroicons-arrow-path"
                title="Loading publications..." description="Please wait." />

            <!-- Error -->
            <UAlert v-else-if="error" color="red" variant="soft" icon="i-heroicons-exclamation-triangle" title="Error"
                :description="error" />

            <!-- Empty -->
            <UCard v-else-if="!publications.length" class="login-card">
                <div class="py-10 text-center">
                    <h2 class="text-lg font-semibold text-gray-900 dark:text-white">
                        No publications found
                    </h2>
                    <p class="text-sm text-gray-500 mt-1">
                        {{route.params.username}} doesn't have any publications yet.
                    </p>
                </div>
            </UCard>

            <!-- GRID -->
            <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                <UCard v-for="p in publications" :key="p.id" class="login-card">
                    <!-- Header -->
                    <template #header>
                        <div class="flex items-start justify-between gap-3">
                            <div class="min-w-0">
                                <h2 class="font-bold text-gray-1200 dark:text-white truncate">
                                    {{ p.titulo }}
                                </h2>

                                <p class="text-sm text-gray-600 dark:text-gray-300 line-clamp-3">
                                    {{ p.areaCientifica }}
                                </p>
                                <p class="text-xs text-gray-500 mt-1">
                                    {{ formatDate(p.createdAt) }}
                                </p>
                            </div>

                            <UBadge color="primary" variant="soft">
                                {{ p.tipo }}
                            </UBadge>
                        </div>
                    </template>

                    <!-- Body -->
                    <div class="space-y-3">
                        <div class="text-s text-gray-500">
                            <span >
                                {{ p.descricao }}
                            </span>
                        </div>

                        <div class="text-xs text-gray-500">
                            <span class="font-medium">Authors:</span>
                            <span class="ml-1">
                                {{p.autores.map(a => a.name).join(', ')}}
                            </span>
                        </div>

                        <!-- Tags -->
                        <div v-if="p.tags?.length" class="flex flex-wrap gap-1">
                            <UBadge v-for="tag in p.tags" :key="tag" size="xs" color="gray" variant="outline">
                                {{ tag }}
                            </UBadge>
                        </div>

                        <!-- Rating -->
                        <div class="flex items-center gap-2 text-xs text-gray-500">
                            <span>⭐ {{ p.ratingAverage.toFixed(1) }}</span>
                        </div>
                    </div>

                    <!-- Footer -->
                    <template #footer>
                        <div class="flex items-center justify-end gap-2">
                            <UTooltip text="Download File">
                                <UButton icon="i-heroicons-document-arrow-down" color="primary" variant="soft"
                                    :loading="p.file.id"
                                    @click="handleDownload(p)" />
                            </UTooltip>

                        </div>
                    </template>
                </UCard>
            </div>

        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAPIStore } from '~/stores/api'
const pubStore = usePublicationStore()
const apiStore = useAPIStore()
const toast = useToast()
const publications = ref([])
const loading = ref(false)
const error = ref('')
const route = useRoute()

const load = async () => {
  const username = String(route.params.username || '')
  if (!username) return
  publications.value = await pubStore.fetchPublicationsByUser(username)

  
}

onMounted(load)

const formatDate = (iso) => {
    return new Date(iso).toLocaleDateString('pt-PT', {
        year: 'numeric',
        month: 'short',
        day: '2-digit'
    })
}

const downloadingId = ref(null)


const handleDownload = async (publications) => {
  downloadingId.value = publications.id
  try {
    await pubStore.downloadFile(publications.id, publications.titulo, publications.fileType)
    
    toast.add({ title: 'Download started', color: 'green' })
  } catch (error) {
    console.log(error);
    toast.add({ title: 'Download failed', description: 'File not found or server error', color: 'red' })
  } finally {
    downloadingId.value = null
  }
}
</script>
