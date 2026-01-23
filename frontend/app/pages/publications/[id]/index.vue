<template>
    <div class="min-h-screen px-4 sm:px-6 lg:px-8 py-10 bg-gray-50 dark:bg-gray-950">
        <UCard class="max-w-xl mx-auto shadow-xl ring-1 ring-gray-200 dark:ring-gray-800 bg-white dark:bg-gray-900"
            :ui="{ body: { padding: 'p-0', base: 'flex flex-col min-h-0' } }">
            <!-- Header -->
            <div
                class="p-4 border-b border-gray-200 dark:border-gray-800 flex justify-between items-start bg-gray-50 dark:bg-gray-800/50 rounded-t-lg">
                <h1 class="text-lg font-bold text-gray-900 dark:text-white truncate pr-2 leading-tight">
                    {{ pub?.titulo || 'Publicação' }}
                </h1>

                <div class="flex items-center gap-2">
                    <UButton label="Download" icon="i-heroicons-arrow-down-tray" size="xs" color="primary"
                        variant="soft" :loading="downloadingId === pub?.id" @click="handleDownload(pub)" />
                    <UButton color="gray" variant="ghost" icon="i-heroicons-arrow-left" size="xs" to="/publications" />
                </div>
            </div>


            <!-- Tabs -->
            <UTabs :items="tabs" class="flex flex-col flex-1 min-h-0"
                :ui="{ wrapper: 'space-y-0', container: 'flex-1 overflow-y-auto min-h-0' }">

                <!-- RESUMO TAB -->
                <template #resumo>
                    <div class="p-4 space-y-4">
                        <div
                            class="rounded-xl border border-gray-200 dark:border-gray-800 bg-gray-50 dark:bg-gray-800/40 p-4">
                            <h3 class="text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">
                                Resumo
                            </h3>

                            <p v-if="pub?.resumo"
                                class="text-sm text-gray-700 dark:text-gray-200 whitespace-pre-wrap leading-relaxed break-words">
                                {{ pub.resumo }}
                            </p>

                            <p v-else class="text-sm text-gray-500 italic">
                                Sem resumo disponível.
                            </p>
                        </div>
                    </div>
                </template>

                <!-- INFO TAB -->
                <template #info>
                    <div class="p-4 space-y-6">
                        <p class="text-gray-600 dark:text-gray-300 text-sm leading-relaxed">
                            {{ pub?.descricao }}
                        </p>

                        <!-- Rating Block -->


                        <!-- Details -->
                        <div>

                            <dl class="space-y-3 text-sm">
                                <div class="flex justify-between border-b border-gray-100 dark:border-gray-800 pb-2">
                                    <dt class="text-gray-500">Date</dt>
                                    <dd class="text-gray-900 dark:text-white font-medium">
                                        {{ pub ? new Date(pub.createdAt).toLocaleDateString() : '-' }}
                                    </dd>
                                </div>

                                <div class="flex justify-between border-b border-gray-100 dark:border-gray-800 pb-2">
                                    <dt class="text-gray-500">Type</dt>
                                    <dd class="text-gray-900 dark:text-white font-medium">
                                        {{ pub?.tipo }}
                                    </dd>
                                </div>

                                <div class="flex justify-between border-b border-gray-100 dark:border-gray-800 pb-2">
                                    <dt class="text-gray-500">Area</dt>
                                    <dd class="text-gray-900 dark:text-white font-medium">
                                        {{ pub?.areaCientifica }}
                                    </dd>
                                </div>

                                <div
                                    class="flex items-center justify-between p-4 bg-gray-50 dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700">
                                    <!-- Left -->
                                    <div class="flex flex-col gap-1">
                                        <div class="flex items-center gap-1.5">
                                            <UIcon name="i-heroicons-star-solid" class="w-5 h-5 text-yellow-400" />
                                            <span class="font-bold text-xl text-gray-900 dark:text-white leading-none">
                                                {{ displayRatingAverage || '0.0' }}/5
                                            </span>
                                        </div>
                                        <span
                                            class="text-[10px] font-bold text-gray-400 uppercase tracking-wider pl-0.5">
                                            {{ displayRatingCount }} RATINGS
                                        </span>
                                    </div>

                                    <!-- Right -->
                                    <div class="flex flex-col items-end gap-1.5">
                                        <span class="text-[10px] font-bold text-gray-400 uppercase tracking-wider">
                                            YOUR RATING
                                        </span>

                                        <div class="flex items-center gap-1" @mouseleave="hoverRating = 0">
                                            <button v-for="star in 5" :key="star" type="button"
                                                class="focus:outline-none transition-transform hover:scale-110 active:scale-95"
                                                @mouseenter="hoverRating = star" @click="handleRate(star)">
                                                <UIcon
                                                    :name="star <= (hoverRating || userRatingValue) ? 'i-heroicons-star-solid' : 'i-heroicons-star'"
                                                    class="w-6 h-6 transition-colors duration-200"
                                                    :class="star <= (hoverRating || userRatingValue) ? 'text-yellow-400' : 'text-gray-300 dark:text-gray-600'" />
                                            </button>

                                            <UTooltip text="Remove Rating" v-if="userRatingValue > 0">
                                                <UButton icon="i-heroicons-x-mark" size="2xs" color="red"
                                                    variant="ghost" :loading="ratingUpdating"
                                                    @click="handleClearRating" />
                                            </UTooltip>
                                        </div>
                                    </div>
                                </div>

                                <div class="pt-2">
                                    <dt class="text-gray-500 mb-1">Authors</dt>
                                    <dd class="flex flex-wrap gap-1">
                                        <UBadge v-for="author in (pub?.autores || [])" :key="author.username"
                                            color="gray" variant="subtle" size="md">
                                            {{ author.name }}
                                        </UBadge>
                                    </dd>
                                </div>

                                <div class="pt-1">
                                    <dt class="text-gray-500 mb-1">Tags</dt>
                                    <dd class="flex flex-wrap gap-1">
                                        <UBadge v-for="tag in (pub?.tags || [])" :key="tag" color="primary"
                                            variant="subtle" size="md">
                                            {{ tag }}
                                        </UBadge>
                                    </dd>
                                </div>

                                <div class="pt-2 flex justify-between">
                                    <dt class="text-gray-500">Uploaded By</dt>
                                    <dd class="text-gray-900 dark:text-white font-medium">
                                        {{ pub?.creatorName }}
                                    </dd>
                                </div>

                                <div class="pt-2 flex justify-between items-center">
                                    <dt class="text-gray-500">Visibility</dt>
                                    <dd class="text-gray-900 dark:text-white font-medium flex items-center gap-2">
                                        <UBadge :color="pub?.hidden ? 'red' : 'green'" variant="subtle" size="md">
                                            {{ pub?.hidden ? 'Hidden' : 'Visible' }}
                                        </UBadge>

                                        <UTooltip v-if="canToggleVisibility"
                                            :text="pub?.hidden ? 'Make Visible' : 'Hide'">
                                            <UButton :icon="pub?.hidden ? 'i-heroicons-eye' : 'i-heroicons-eye-slash'"
                                                size="md" color="gray" variant="ghost" @click="togglePubVisibility"
                                                :loading="visibilityUpdating" />
                                        </UTooltip>
                                    </dd>
                                </div>
                            </dl>
                        </div>
                    </div>
                </template>

                <!-- COMMENTS TAB -->
                <template #comments>
                    <div class="flex flex-col h-full">
                        <!-- Admin filter -->
                        <div v-if="authStore.isAdmin || authStore.isResponsavel"
                            class="sticky top-0 bg-white dark:bg-gray-900 z-10 px-4 py-2 border-b border-gray-100 dark:border-gray-800">
                            <USelectMenu v-model="commentsFilter" :items="filterOptions" option-attribute="label"
                                placeholder="Filter comments..." size="sm" :searchable="false"
                                @update:modelValue="refreshComments" />
                        </div>

                        <!-- Add comment -->
                        <div
                            class="p-4 border-b border-gray-100 dark:border-gray-800 bg-gray-50/50 dark:bg-gray-800/20">
                            <UForm :state="{ text: newComment }" @submit="submitComment">
                                <UTextarea v-model="newComment" placeholder="Write a comment..." :rows="2" size="sm"
                                    class="w-full mb-2" />
                                <div class="flex justify-end">
                                    <UButton type="submit" label="Post Comment" size="xs" color="primary"
                                        :loading="submittingComment" :disabled="!newComment.trim()" />
                                </div>
                            </UForm>
                        </div>

                        <!-- list -->
                        <div class="p-4 space-y-4 flex-1 overflow-y-auto">
                            <div v-if="commentsLoading" class="flex justify-center py-4">
                                <UIcon name="i-heroicons-arrow-path" class="w-5 h-5 animate-spin text-gray-400" />
                            </div>

                            <div v-else-if="sortedComments.length === 0" class="text-center text-gray-500 py-8 text-sm">
                                No comments yet.
                            </div>

                            <div v-else v-for="comment in sortedComments" :key="comment.id"
                                class="rounded-lg p-3 relative group transition-colors"
                                :class="comment.hidden ? 'bg-red-50 dark:bg-red-950/20 border border-red-100 dark:border-red-900/50' : 'bg-gray-50 dark:bg-gray-800'">
                                <div v-if="comment.hidden" class="absolute top-2 right-8 flex items-center gap-1">
                                    <span class="text-[10px] font-bold text-red-500 uppercase">Hidden</span>
                                </div>

                                <div class="flex items-center gap-2 mb-2 pr-6">
                                    <UAvatar :alt="comment.name" size="xs" :class="{ 'opacity-50': comment.hidden }" />
                                    <div class="flex flex-col" :class="{ 'opacity-50': comment.hidden }">
                                        <span class="font-bold text-sm text-gray-900 dark:text-white leading-none">
                                            {{ comment.name }}
                                        </span>
                                        <span class="text-[10px] text-gray-500 mt-0.5">
                                            {{ new Date(comment.date).toLocaleDateString() }}
                                        </span>
                                    </div>
                                </div>

                                <p class="text-xs text-gray-700 dark:text-gray-300 whitespace-pre-wrap"
                                    :class="{ 'opacity-50 italic': comment.hidden }">
                                    {{ comment.comment }}
                                </p>

                                <div v-if="comment.hidden && comment.motive"
                                    class="mt-2 text-[10px] text-red-500 border-t border-red-100 dark:border-red-900/30 pt-1">
                                    <strong>Motive:</strong> {{ comment.motive }}
                                </div>
                            </div>
                        </div>
                    </div>
                </template>
            </UTabs>
        </UCard>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const pubStore = usePublicationStore()
const authStore = useAuthStore()
const apiStore = useAPIStore()
const toast = useToast()

definePageMeta({ layout: 'default' })

const pubId = computed(() => Number(route.params.id))

const pub = ref(null)

// rating
const ratingUpdating = ref(false)
const userRatingObj = ref(null)
const displayRatingAverage = ref(0.0)
const displayRatingCount = ref(0)
const hoverRating = ref(0)

const userRatingValue = computed(() => (userRatingObj.value ? userRatingObj.value.value : 0))

// download
const downloadingId = ref(null)

// visibility
const visibilityUpdating = ref(false)
const canToggleVisibility = computed(() => {
    if (!pub.value) return false
    const isOwner = authStore.currentUser?.username === pub.value.creatorUsername
    return authStore.isAdmin || authStore.isResponsavel || isOwner
})

// comments
const comments = ref([])
const commentsLoading = ref(false)
const newComment = ref('')
const submittingComment = ref(false)

const filterOptions = ref([
    { label: 'All Comments', value: null },
    { label: 'Visible Only', value: false },
    { label: 'Hidden Only', value: true }
])

// default: normal user -> Visible Only, admin -> All
const commentsFilter = ref(filterOptions.value[1])

const tabs = [
    { label: 'Resumo', slot: 'resumo' },
    { label: 'Info', slot: 'info' },
    { label: 'Comments', slot: 'comments' }
]


const loadPublication = async () => {
    // garante que tens publicações carregadas e procura pelo id
    if (!pubStore.publications?.length) {
        await pubStore.fetchPublications()
    }

    const found = pubStore.publications.find(p => p.id === pubId.value)
    pub.value = found || null

    if (!pub.value) {
        toast.add({ title: 'Not found', description: 'Publication not found', color: 'red' })
        return
    }

    displayRatingAverage.value = pub.value.ratingAverage || 0.0
    displayRatingCount.value = pub.value.ratingCount || 0

    // default filter
    if (authStore.isAdmin || authStore.isResponsavel) {
        commentsFilter.value = filterOptions.value[0] // All
    } else {
        commentsFilter.value = filterOptions.value[1] // Visible only
    }

    // fetch rating of current user
    try {
        userRatingObj.value = await pubStore.fetchUserRating(pub.value.id)
    } catch (e) {
        userRatingObj.value = null
    }

    await refreshComments()
}

onMounted(loadPublication)

const refreshComments = async () => {
    if (!pub.value) return
    commentsLoading.value = true
    comments.value = []

    try {
        // 👇 ESTE É O BUG CLÁSSICO:
        // o teu fetchComments espera boolean (hidden) e tu estavas a mandar null/obj
        const filterValue = commentsFilter.value?.value
        // se for null => ALL (backend pode não suportar). decide:
        // - se backend suporta null => manda null
        // - se não suporta => manda false para "visíveis"
        const safeFilter = filterValue === null ? null : filterValue

        comments.value = await pubStore.fetchComments(pub.value.id, safeFilter)
    } catch (e) {
        console.error('Failed to load comments', e)
    } finally {
        commentsLoading.value = false
    }
}

const sortedComments = computed(() => {
    if (!comments.value) return []
    const currentUsername = authStore.currentUser?.username

    return [...comments.value].sort((a, b) => {
        const aIsOwn = a.username === currentUsername
        const bIsOwn = b.username === currentUsername
        if (aIsOwn && !bIsOwn) return -1
        if (!aIsOwn && bIsOwn) return 1
        return new Date(b.date) - new Date(a.date)
    })
})

const submitComment = async () => {
    if (!newComment.value.trim() || !pub.value) return
    submittingComment.value = true
    try {
        await apiStore.createComment(pub.value.id, { comment: newComment.value })
        toast.add({ title: 'Comment added', color: 'green' })
        newComment.value = ''
        await refreshComments()
    } catch (error) {
        toast.add({ title: 'Error', description: error.response?.data?.message || 'Failed to post comment', color: 'red' })
    } finally {
        submittingComment.value = false
    }
}

// rating logic
const handleRate = async (value) => {
    if (!pub.value) return
    ratingUpdating.value = true

    try {
        let result
        if (userRatingObj.value) {
            result = await pubStore.updateRating(pub.value.id, userRatingObj.value.id, value)
            toast.add({ title: 'Rating updated', color: 'green' })
        } else {
            result = await pubStore.addRating(pub.value.id, value)
            toast.add({ title: 'Rating added', color: 'green' })
        }

        displayRatingAverage.value = result.ratingAverage
        displayRatingCount.value = result.ratingCount
        userRatingObj.value = await pubStore.fetchUserRating(pub.value.id)

        await pubStore.fetchPublications()
    } catch (error) {
        toast.add({ title: 'Error rating', description: error.response?.data?.message, color: 'red' })
    } finally {
        ratingUpdating.value = false
    }
}

const handleClearRating = async () => {
    if (!userRatingObj.value || !pub.value) return
    ratingUpdating.value = true

    try {
        const result = await pubStore.deleteRating(pub.value.id, userRatingObj.value.id)
        toast.add({ title: 'Rating removed', color: 'green' })

        displayRatingAverage.value = result.rating_medio_atualizado
        displayRatingCount.value = result.numero_ratings
        userRatingObj.value = null

        await pubStore.fetchPublications()
    } catch (error) {
        toast.add({ title: 'Error removing rating', color: 'red' })
    } finally {
        ratingUpdating.value = false
    }
}

// download
const handleDownload = async (p) => {
    if (!p) return
    downloadingId.value = p.id
    try {
        await pubStore.downloadFile(p.id, p.titulo, p.fileType)
        toast.add({ title: 'Download started', color: 'green' })
    } catch (error) {
        toast.add({ title: 'Download failed', description: 'File not found or server error', color: 'red' })
    } finally {
        downloadingId.value = null
    }
}

// visibility
const togglePubVisibility = async () => {
    if (!pub.value) return
    visibilityUpdating.value = true
    try {
        await pubStore.togglePublicationVisibility(pub.value.id, pub.value.hidden)
        pub.value.hidden = !pub.value.hidden
        toast.add({ title: `Publication is now ${pub.value.hidden ? 'Hidden' : 'Visible'}`, color: 'green' })
    } catch (error) {
        toast.add({ title: 'Error', description: 'Failed to update visibility', color: 'red' })
    } finally {
        visibilityUpdating.value = false
    }
}
</script>
