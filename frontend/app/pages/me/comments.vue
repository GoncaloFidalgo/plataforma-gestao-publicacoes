<template>
    <div class="min-h-screen w-full px-4 sm:px-6 lg:px-8 py-10">
        <div class="mx-auto max-w-6xl space-y-6">

            <div class="flex items-start justify-between gap-4">
                <div>
                    <h1 class="text-2xl font-bold text-gray-900 dark:text-white">
                        My Comments
                    </h1>
                    <p class="text-sm text-gray-500 mt-1">
                        History of comments you have posted on publications
                    </p>
                </div>

                <UButton icon="i-heroicons-arrow-path" color="gray" variant="soft" :loading="loading" @click="refresh">
                    Refresh
                </UButton>
            </div>

            <UAlert v-if="loading && !comments.length" color="gray" icon="i-heroicons-arrow-path"
                title="Loading comments..." description="Please wait." />

            <UAlert v-else-if="error" color="red" variant="soft" icon="i-heroicons-exclamation-triangle" title="Error"
                :description="String(error)" />

            <UCard v-else-if="!comments.length" class="bg-gray-50 dark:bg-gray-800">
                <div class="py-10 text-center">
                    <h2 class="text-lg font-semibold text-gray-900 dark:text-white">
                        No comments found
                    </h2>
                    <p class="text-sm text-gray-500 mt-1">
                        You haven't commented on any publications yet.
                    </p>
                </div>
            </UCard>

            <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                <UCard v-for="c in comments" :key="c.id" class="flex flex-col h-full hover:shadow-md transition-shadow">
                    
                    <template #header>
                        <div class="flex items-center justify-between gap-3">
                            <div class="flex items-center text-xs text-gray-500 dark:text-gray-400">
                                <UIcon name="i-heroicons-calendar" class="w-4 h-4 mr-1" />
                                {{ formatDate(c.date) }}
                            </div>

                            <UBadge v-if="c.hidden" color="red" variant="soft" size="xs">
                                Hidden
                            </UBadge>
                            <UBadge v-else color="green" variant="soft" size="xs">
                                Visible
                            </UBadge>
                        </div>
                    </template>

                    <div class="space-y-3">
                        <div class="text-sm text-gray-700 dark:text-gray-200 italic relative pl-4 border-l-2 border-gray-200 dark:border-gray-700">
                           "{{ c.comment }}"
                        </div>
                        
                        <div v-if="c.hidden && c.motive" class="text-xs text-red-500 mt-2 bg-red-50 dark:bg-red-900/10 p-2 rounded">
                            <span class="font-semibold">Reason hidden:</span> {{ c.motive }}
                        </div>
                    </div>

                    <template #footer>
                        <div class="flex items-center justify-end gap-2">
                             <UTooltip text="View Details (Coming Soon)">
                                <UButton icon="i-heroicons-eye" color="gray" variant="ghost" size="xs" />
                            </UTooltip>
                        </div>
                    </template>
                </UCard>
            </div>

        </div>
    </div>
</template>

<script setup>
import { useCommentsStore } from '~/stores/comments.js'

// Using the store we fixed earlier
const commentsStore = useCommentsStore()
const comments = computed(() => commentsStore.comments)
const loading = ref(false)
const error = ref(null)

onMounted(async () => {
    await refresh()
})

const refresh = async () => {
    loading.value = true
    error.value = null
    try {
        await commentsStore.fetchMyComments()
    } catch (e) {
        error.value = 'Could not load comments'
    } finally {
        loading.value = false
    }
}

const formatDate = (dateString) => {
    if (!dateString) return ''
    return new Date(dateString).toLocaleDateString('pt-PT', {
        year: 'numeric',
        month: 'short', 
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    })
}

definePageMeta({
  layout: 'default',
})
</script>