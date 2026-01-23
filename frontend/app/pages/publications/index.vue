<template>
  <div class="flex gap-6 items-start relative">
    <div class="flex-1 min-w-0 space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900 dark:text-white">Publications</h1>
        <p class="text-gray-500 text-sm">Browse and download academic resources</p>
      </div>
      <UButton
          icon="i-heroicons-cloud-arrow-up"
          color="primary"
          label="Upload"
          to="/publications/create"
      />
    </div>
      <div class="flex gap-3 items-center bg-white dark:bg-gray-900 p-3 rounded-lg border border-gray-200 dark:border-gray-800 shadow-sm">
        <UIcon name="i-heroicons-funnel" class="w-5 h-5 text-gray-400" />

        <!-- Search Input -->
        <UInput
            v-model="search"
            icon="i-heroicons-magnifying-glass"
            placeholder="Search titles..."
            size="sm"
            class="flex-1 min-w-[200px]"
        />

        <!-- Hidden Status Filter (Admins/Resp only) -->
        <USelectMenu
            v-if="authStore.isAdmin || authStore.isResponsavel"
            v-model="pubHiddenFilter"
            :items="pubFilterOptions"
            option-attribute="label"
            placeholder="Visibility"
            class="w-40"
            size="sm"
            :searchable="false"
        >
          <template #label>
            <span class="truncate">{{ pubHiddenFilter.label }}</span>
          </template>
        </USelectMenu>

        <!-- Tags Filter (Multi-select) -->
        <USelectMenu
            v-model="selectedTags"
            :items="tagOptions"
            placeholder="Filter by Tags"
            multiple
            searchable
            class="w-48"
            size="sm"
        >
          <template #label>
            <span v-if="selectedTags.length" class="truncate">{{ selectedTags.join(', ') }}</span>
            <span v-else class="text-gray-500">Filter by Tags</span>
          </template>
        </USelectMenu>

        <!-- Clear Filters -->
        <UButton
            v-if="selectedTags.length > 0 || search !== ''"
            icon="i-heroicons-x-mark"
            color="gray"
            variant="ghost"
            size="xs"
            @click="clearFilters"
        />
      </div>
    <!-- Table -->
    <UCard :ui="{ body: { padding: '' } }">
      <UTable
          :data="filteredPublications"
          :columns="columns"
          :loading="pubStore.loading"
      >
        <template #ratingAverage-cell="{ row }">
          <div class="flex items-center gap-1 text-gray-700 dark:text-gray-200">
            <span class="font-semibold text-sm">{{ row.original.ratingAverage || '0.0' }}</span>
            <UIcon name="i-heroicons-star-solid" class="w-4 h-4 text-yellow-400 mb-0.5" />
          </div>
        </template>
        <!-- Custom Cell: Created At -->
        <template #createdAt-cell="{ row }">
          {{ new Date(row.original.createdAt).toLocaleDateString() }}
        </template>
        <template #tags-cell="{ row }">
          <div class="flex flex-wrap gap-1">
            <UBadge
                v-for="tag in row.original.tags.slice(0, 3)"
                :key="tag"
                color="primary"
                variant="subtle"
                size="md"
            >
              {{ tag }}
            </UBadge>
            <span v-if="row.original.tags.length > 3" class="text-xs text-gray-500 self-center">
                +{{ row.original.tags.length - 3 }}
              </span>
          </div>
        </template>
        <!-- Custom Cell: Actions -->
        <template #actions-cell="{ row }">
          <UTooltip text="Download File">
            <UButton
                icon="i-heroicons-arrow-down-tray"
                size="xs"
                color="primary"
                variant="ghost"
                :loading="downloadingId === row.original.id"
                @click="handleDownload(row.original)"
            />
          </UTooltip>
          <UTooltip text="View Details">
            <UButton
                icon="i-heroicons-eye"
                size="xs"
                color="gray"
                variant="ghost"
                @click="openSidePanel(row.original)"
            />
          </UTooltip>
        </template>

      </UTable>
    </UCard>

    </div>
    <aside
        v-if="isPanelOpen"
        class="w-96 flex-shrink-0 sticky top-0 h-[calc(100vh-5rem)] overflow-hidden"
    >
      <UCard class="h-full flex flex-col" :ui="{ body: { padding: 'p-0', base: 'flex-1 flex flex-col min-h-0' } }">

        <!-- Panel Header -->
        <div class="p-4 border-b border-gray-200 dark:border-gray-800 flex justify-between items-start bg-gray-50 dark:bg-gray-800/50 rounded-t-lg">
          <h2 class="text-lg font-bold text-gray-900 dark:text-white truncate pr-2 leading-tight">
            {{ selectedPub?.titulo }}
          </h2>
          <UButton
              color="gray"
              variant="ghost"
              icon="i-heroicons-x-mark-20-solid"
              size="xs"
              @click="isPanelOpen = false"
          />
        </div>

        <!-- Panel Tabs -->
        <UTabs   :items="tabs"
                 class="flex flex-col flex-1 min-h-0"
                 :ui="{ wrapper: 'space-y-0', container: 'flex-1 overflow-y-auto min-h-0' }">

          <!-- 1. INFO TAB -->
          <template #info="{ item }">
            <div class="p-4 space-y-6">

              <!-- Description -->
              <p class="text-gray-600 dark:text-gray-300 text-sm leading-relaxed">
                {{ selectedPub?.descricao }}
              </p>

              <!-- Rating Block -->
              <div class="flex items-center justify-between p-4 bg-gray-50 dark:bg-gray-800 rounded-lg border border-gray-200 dark:border-gray-700">

                <!-- Left: Average Rating Display -->
                <div class="flex flex-col gap-1">
                  <div class="flex items-center gap-1.5">
                    <UIcon name="i-heroicons-star-solid" class="w-5 h-5 text-yellow-400" />
                    <span class="font-bold text-xl text-gray-900 dark:text-white leading-none">
                      {{ displayRatingAverage || '0.0' }}/5
                    </span>
                  </div>
                  <span class="text-[10px] font-bold text-gray-400 uppercase tracking-wider pl-0.5">
                    {{ displayRatingCount }} RATINGS
                  </span>
                </div>

                <!-- Right: Interactive User Rating -->
                <div class="flex flex-col items-end gap-1.5">
                  <span class="text-[10px] font-bold text-gray-400 uppercase tracking-wider">
                    YOUR RATING
                  </span>

                  <div class="flex items-center gap-1" @mouseleave="hoverRating = 0">
                    <button
                        v-for="star in 5"
                        :key="star"
                        type="button"
                        class="focus:outline-none transition-transform hover:scale-110 active:scale-95"
                        @mouseenter="hoverRating = star"
                        @click="handleRate(star)"
                    >
                      <UIcon
                          :name="star <= (hoverRating || userRatingValue) ? 'i-heroicons-star-solid' : 'i-heroicons-star'"
                          class="w-6 h-6 transition-colors duration-200"
                          :class="star <= (hoverRating || userRatingValue) ? 'text-yellow-400' : 'text-gray-300 dark:text-gray-600'"
                      />
                    </button>

                    <!-- Clear Rating -->
                    <UTooltip text="Remove Rating" v-if="userRatingValue > 0">
                      <UButton
                          icon="i-heroicons-x-mark"
                          size="2xs"
                          color="red"
                          variant="ghost"
                          :loading="ratingUpdating"
                          @click="handleClearRating"
                      />
                    </UTooltip>
                  </div>
                </div>

              </div>
              <!-- Information Grid -->
              <div>
                <div class="flex justify-between items-center mb-3">
                  <h3 class="text-xs font-bold text-gray-400 uppercase tracking-wider">
                    Details
                  </h3>
                  <!-- Download-->
                  <UButton
                      label="Download"
                      icon="i-heroicons-arrow-down-tray"
                      size="xs"
                      color="primary"
                      variant="soft"
                      :loading="downloadingId === selectedPub?.id"
                      @click="handleDownload(selectedPub)"
                  />
                </div>
                <dl class="space-y-3 text-sm">
                  <div class="flex justify-between border-b border-gray-100 dark:border-gray-800 pb-2">
                    <dt class="text-gray-500">Date</dt>
                    <dd class="text-gray-900 dark:text-white font-medium">
                      {{ selectedPub ? new Date(selectedPub.createdAt).toLocaleDateString() : '-' }}
                    </dd>
                  </div>
                  <div class="flex justify-between border-b border-gray-100 dark:border-gray-800 pb-2">
                    <dt class="text-gray-500">Type</dt>
                    <dd class="text-gray-900 dark:text-white font-medium">
                      {{ selectedPub?.tipo }}
                    </dd>
                  </div>
                  <div class="flex justify-between border-b border-gray-100 dark:border-gray-800 pb-2">
                    <dt class="text-gray-500">Area</dt>
                    <dd class="text-gray-900 dark:text-white font-medium">
                      {{ selectedPub?.areaCientifica }}
                    </dd>
                  </div>

                  <div class="pt-2">
                    <dt class="text-gray-500 mb-1">Authors</dt>
                    <dd class="flex flex-wrap gap-1">
                      <UBadge v-for="author in selectedPub?.autores" :key="author.username" color="gray" variant="subtle" size="md">
                        {{ author.name }}
                      </UBadge>
                    </dd>
                  </div>

                  <div class="pt-1">
                    <dt class="text-gray-500 mb-1">Tags</dt>
                    <dd class="flex flex-wrap gap-1">
                      <UBadge v-for="tag in selectedPub?.tags" :key="tag" color="primary" variant="subtle" size="md">
                        {{ tag }}
                      </UBadge>
                    </dd>
                  </div>

                  <div class="pt-2 flex justify-between">
                    <dt class="text-gray-500">Uploaded By</dt>
                    <dd class="text-gray-900 dark:text-white font-medium">
                      {{ selectedPub?.creatorName }}
                    </dd>
                  </div>

                  <div class="pt-2 flex justify-between items-center">
                    <dt class="text-gray-500">Visibility</dt>
                    <dd class="text-gray-900 dark:text-white font-medium flex items-center gap-2">
                      <UBadge :color="selectedPub?.hidden ? 'red' : 'green'" variant="subtle" size="md">
                        {{ selectedPub?.hidden ? 'Hidden' : 'Visible' }}
                      </UBadge>

                      <UTooltip v-if="canToggleVisibility" :text="selectedPub?.hidden ? 'Make Visible' : 'Hide'">
                        <UButton
                            :icon="selectedPub?.hidden ? 'i-heroicons-eye' : 'i-heroicons-eye-slash'"
                            size="md"
                            color="gray"
                            variant="ghost"
                            @click="togglePubVisibility"
                            :loading="visibilityUpdating"
                        />
                      </UTooltip>
                    </dd>
                  </div>
                </dl>
              </div>

            </div>
          </template>

          <!-- 2. COMMENTS TAB -->
          <template #comments="{ item }">
            <div class="flex flex-col h-full">
              <!-- Admin Filter -->
              <div
                  v-if="authStore.isAdmin || authStore.isResponsavel"
                  class="sticky top-0 bg-white dark:bg-gray-900 z-10 px-4 py-2 border-b border-gray-100 dark:border-gray-800"
              >
                <USelectMenu
                    v-model="commentsFilter"
                    :items="filterOptions"
                    option-attribute="label"
                    placeholder="Filter comments..."
                    size="sm"
                    :searchable="false"
                    @change="refreshComments"
                />
              </div>

              <!-- ADD COMMENT FORM -->
              <div class="p-4 border-b border-gray-100 dark:border-gray-800 bg-gray-50/50 dark:bg-gray-800/20">
                <UForm :state="{ text: newComment }" @submit="submitComment">
                  <UTextarea
                      v-model="newComment"
                      placeholder="Write a comment..."
                      :rows="2"
                      size="sm"
                      class="w-full mb-2"
                  />
                  <div class="flex justify-end">
                    <UButton
                        type="submit"
                        label="Post Comment"
                        size="xs"
                        color="primary"
                        :loading="submittingComment"
                        :disabled="!newComment.trim()"
                    />
                  </div>
                </UForm>
              </div>

              <!-- COMMENTS LIST -->
              <div class="p-4 space-y-4 flex-1 overflow-y-auto">
                <div v-if="commentsLoading" class="flex justify-center py-4">
                  <UIcon name="i-heroicons-arrow-path" class="w-5 h-5 animate-spin text-gray-400" />
                </div>

                <div v-else-if="sortedComments.length === 0" class="text-center text-gray-500 py-8 text-sm">
                  No comments yet.
                </div>

                <div
                    v-else
                    v-for="comment in sortedComments"
                    :key="comment.id"
                    class="rounded-lg p-3 relative group transition-colors"
                    :class="comment.hidden ? 'bg-red-50 dark:bg-red-950/20 border border-red-100 dark:border-red-900/50' : 'bg-gray-50 dark:bg-gray-800'"
                >
                  <!-- Hidden Indicator -->
                  <div v-if="comment.hidden" class="absolute top-2 right-8 flex items-center gap-1">
                    <span class="text-[10px] font-bold text-red-500 uppercase">Hidden</span>
                  </div>

                  <!-- Action Menu (Admin/Resp OR Owner) -->
                  <div class="absolute top-2 right-2">
                    <UDropdownMenu
                        v-if="getCommentActions(comment).length > 0"
                        :items="getCommentActions(comment)"
                        :content="{ align: 'end' }"
                    >
                      <UButton
                          color="gray"
                          variant="ghost"
                          icon="i-heroicons-ellipsis-vertical"
                          size="2xs"
                          :class="comment.hidden ? 'text-red-400 hover:text-red-600' : 'text-gray-400 hover:text-gray-600'"
                      />
                    </UDropdownMenu>
                  </div>

                  <!-- Header -->
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

                  <!-- EDIT MODE -->
                  <div v-if="editingId === comment.id">
                    <UTextarea v-model="editingText" :rows="2" size="sm" class="mb-2" />
                    <div class="flex gap-2 justify-end">
                      <UButton label="Cancel" size="2xs" color="gray" variant="ghost" @click="cancelEdit" />
                      <UButton label="Save" size="2xs" color="primary" @click="saveEdit(comment)" />
                    </div>
                  </div>

                  <!-- VIEW MODE -->
                  <div v-else>
                    <p
                        class="text-xs text-gray-700 dark:text-gray-300 whitespace-pre-wrap"
                        :class="{ 'opacity-50 italic': comment.hidden }"
                    >
                      {{ comment.comment }}
                    </p>

                    <div v-if="comment.hidden && comment.motive" class="mt-2 text-[10px] text-red-500 border-t border-red-100 dark:border-red-900/30 pt-1">
                      <strong>Motive:</strong> {{ comment.motive }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </template>
        </UTabs>

      </UCard>
      <UModal v-model:open="isVisibilityModalOpen" title="Update Visibility">
        <template #content>
          <UCard>
            <template #header>
              <h3 class="font-semibold">
                {{ commentToToggle?.hidden ? 'Show Comment' : 'Hide Comment' }}
              </h3>
            </template>

            <UForm :state="{ motive: visibilityMotive }" @submit="confirmVisibilityChange" class="space-y-4">
              <p v-if="!commentToToggle?.hidden" class="text-sm text-gray-600 dark:text-gray-300">
                Please provide a reason for hiding this comment.
              </p>
              <p v-else class="text-sm text-gray-600 dark:text-gray-300">
                Are you sure you want to show this comment again? You can leave the motive empty to clear it.
              </p>

              <UFormField label="Motive" name="motive" :required="!commentToToggle?.hidden">
                <UTextarea
                    v-model="visibilityMotive"
                    placeholder="e.g. Inappropriate content..."
                    :rows="2"
                    autofocus
                />
              </UFormField>

              <div class="flex justify-end gap-3 pt-2">
                <UButton label="Cancel" color="neutral" variant="ghost" @click="isVisibilityModalOpen = false" />
                <UButton
                    type="submit"
                    :label="commentToToggle?.hidden ? 'Show' : 'Hide'"
                    :color="commentToToggle?.hidden ? 'green' : 'red'"
                    :loading="visibilityUpdating"
                />
              </div>
            </UForm>
          </UCard>
        </template>
      </UModal>
    </aside>
  </div>
</template>

<script setup >
const pubStore = usePublicationStore()
const authStore = useAuthStore()
const tagStore = useTagStore()
const apiStore = useAPIStore()
const toast = useToast()

definePageMeta({ layout: 'default' })

const columns = [
  { accessorKey: 'titulo', header: 'Title' },
  { accessorKey: 'areaCientifica', header: 'Area' },
  { accessorKey: 'tipo', header: 'Category' },
  { accessorKey: 'tags', header: 'Tags' },
  { accessorKey: 'ratingAverage', header: 'Rating' },
  { accessorKey: 'creatorName', header: 'Uploader' },
  { accessorKey: 'createdAt', header: 'Date' },
  { id: 'actions', header: '' }
]

// --- PUBLICATION FILTER STATE ---
const search = ref('')
const selectedTags = ref([])
const pubFilterOptions = [
  { label: 'All Publications', value: null },
  { label: 'Visible Only', value: false },
  { label: 'Hidden Only', value: true }
]
const pubHiddenFilter = ref(pubFilterOptions[1]) // Default Visible

const tagOptions = computed(() => {
  return tagStore.tags
      .filter(t => !t.hidden)
      .map(t => t.name)
})

const filteredPublications = computed(() => {
  let pubs = pubStore.publications

  // 1. Filter by Hidden Status
  if (authStore.isAdmin || authStore.isResponsavel) {
    if (pubHiddenFilter.value.value !== null) {
      pubs = pubs.filter(p => p.hidden === pubHiddenFilter.value.value)
    }
  } else {
    // Normal users always see visible only
    pubs = pubs.filter(p => p.hidden === false)
  }

  // 2. Filter by Search
  if (search.value) {
    pubs = pubs.filter(p =>
        p.titulo.toLowerCase().includes(search.value.toLowerCase())
    )
  }

  // 3. Filter by Tags
  if (selectedTags.value.length > 0) {
    pubs = pubs.filter(p =>
        p.tags.some(t => selectedTags.value.includes(t))
    )
  }

  return pubs
})

// Side Panel State
const isPanelOpen = ref(false)
const selectedPub = ref(null)
const comments = ref([])
const commentsLoading = ref(false)
const ratingUpdating = ref(false)
const downloadingId = ref(null)

// Local rating state
const userRatingObj = ref(null)
const displayRatingAverage = ref(0.0)
const displayRatingCount = ref(0)

// Visibility Modal State
const isVisibilityModalOpen = ref(false)
const visibilityMotive = ref('')
const commentToToggle = ref(null)
const visibilityUpdating = ref(false)

// COMMENTS FILTER


const filterOptions = ref([
  { label: 'All Comments', value: null },
  { label: 'Visible Only', value: false},
  { label: 'Hidden Only', value: true }
])
const commentsFilter = ref(filterOptions[1])

const tabs = [
  { label: 'Info', slot: 'info' },
  { label: 'Comments', slot: 'comments' }
]

const userRatingValue = computed(() => userRatingObj.value ? userRatingObj.value.value : 0)
const hoverRating = ref(0)

const canToggleVisibility = computed(() => {
  if (!selectedPub.value) return false
  const isOwner = authStore.currentUser?.username === selectedPub.value.creatorUsername
  return authStore.isAdmin || authStore.isResponsavel || isOwner
})
const togglePubVisibility = async () => {
  if (!selectedPub.value) return
  visibilityUpdating.value = true
  try {
    await pubStore.togglePublicationVisibility(selectedPub.value.id, selectedPub.value.hidden)

    selectedPub.value.hidden = !selectedPub.value.hidden

    const state = selectedPub.value.hidden ? 'Hidden' : 'Visible'
    toast.add({ title: `Publication is now ${state}`, color: 'green' })

  } catch (error) {
    toast.add({ title: 'Error', description: 'Failed to update visibility', color: 'red' })
  } finally {
    visibilityUpdating.value = false
  }
}

onMounted(() => {
  pubStore.fetchPublications()
  tagStore.fetchTags()

  if (authStore.isAdmin || authStore.isResponsavel) {
    pubHiddenFilter.value = pubFilterOptions[0] // All
  }
})
const openSidePanel = async (row) => {
  selectedPub.value = row
  displayRatingAverage.value = row.ratingAverage || 0.0
  displayRatingCount.value = row.ratingCount || 0
  isPanelOpen.value = true

  // Set default filter based on role
  if (authStore.isAdmin || authStore.isResponsavel) {
    commentsFilter.value = filterOptions[0]
  } else {
    commentsFilter.value = filterOptions[1]
  }

  await refreshComments()

  try {
    userRatingObj.value = await pubStore.fetchUserRating(row.id)
  } catch (e) { /* ignore */ }
}
const sortedComments = computed(() => {
  if (!comments.value) return []
  const currentUsername = authStore.currentUser?.username

  return [...comments.value].sort((a, b) => {
    // 1. Own comments first
    const aIsOwn = a.username === currentUsername
    const bIsOwn = b.username === currentUsername
    if (aIsOwn && !bIsOwn) return -1
    if (!aIsOwn && bIsOwn) return 1

    // 2. Sort by Date Descending
    return new Date(b.date) - new Date(a.date)
  })
})
const refreshComments = async () => {
  if (!selectedPub.value) return
  commentsLoading.value = true
  comments.value = []

  try {
    const filterValue = commentsFilter.value ? commentsFilter.value.value : false
    comments.value = await pubStore.fetchComments(selectedPub.value.id, filterValue)
  } catch (e) {
    console.error("Failed to load comments", e)
  } finally {
    commentsLoading.value = false
  }
}
const newComment = ref('')
const submittingComment = ref(false)
const editingId = ref(null)
const editingText = ref('')
// --- COMMENT MANAGEMENT ---
// --- COMMENT CRUD ---

const submitComment = async () => {
  if (!newComment.value.trim() || !selectedPub.value) return
  submittingComment.value = true

  try {
    await apiStore.createComment(selectedPub.value.id, { comment: newComment.value })
    toast.add({ title: 'Comment added', color: 'green' })
    newComment.value = ''
    await refreshComments()
  } catch (error) {
    toast.add({ title: 'Error', description: error.response?.data?.message || 'Failed to post comment', color: 'red' })
  } finally {
    submittingComment.value = false
  }
}

const startEdit = (comment) => {
  editingId.value = comment.id
  editingText.value = comment.comment
}

const cancelEdit = () => {
  editingId.value = null
  editingText.value = ''
}

const saveEdit = async (comment) => {
  if (!editingText.value.trim()) return

  try {
    await apiStore.updateComment(selectedPub.value.id, comment.id, { comment: editingText.value })
    toast.add({ title: 'Comment updated', color: 'green' })
    editingId.value = null
    await refreshComments()
  } catch (error) {
    toast.add({ title: 'Error', description: error.response?.data?.message, color: 'red' })
  }
}

const handleDeleteComment = async (comment) => {
  if (!confirm('Are you sure you want to delete this comment?')) return

  try {
    await apiStore.deleteComment(selectedPub.value.id, comment.id)
    toast.add({ title: 'Comment deleted', color: 'green' })
    await refreshComments()
  } catch (error) {
    toast.add({ title: 'Error', description: error.response?.data?.message, color: 'red' })
  }
}
const getCommentActions = (comment) => {
  const actions = []
  const isOwner = authStore.currentUser?.username === comment.username
  const isPrivileged = authStore.isAdmin || authStore.isResponsavel

  // Owner actions
  if (isOwner) {
    actions.push([
      { label: 'Edit', icon: 'i-heroicons-pencil-square', onSelect: () => startEdit(comment) },
      { label: 'Delete', icon: 'i-heroicons-trash', color: 'red', onSelect: () => handleDeleteComment(comment) }
    ])
  }

  // Admin actions
  if (isPrivileged) {
    actions.push([
      {
        label: comment.hidden ? 'Show Comment' : 'Hide Comment',
        icon: comment.hidden ? 'i-heroicons-eye' : 'i-heroicons-eye-slash',
        onSelect: () => {
          commentToToggle.value = comment
          visibilityMotive.value = comment.motive || ''
          isVisibilityModalOpen.value = true
        }
      }
    ])
  }
  return actions
}

const confirmVisibilityChange = async () => {
  if (!selectedPub.value || !commentToToggle.value) return

  if (!commentToToggle.value.hidden && !visibilityMotive.value.trim()) {
    toast.add({ title: 'Motive Required', description: 'Please explain why you are hiding this comment.', color: 'red' })
    return
  }

  visibilityUpdating.value = true

  try {
    const updatedList = await pubStore.toggleCommentVisibility(
        selectedPub.value.id,
        commentToToggle.value.id,
        commentToToggle.value.hidden,
        visibilityMotive.value,
        commentsFilter.value
    )
    comments.value = updatedList

    const action = commentToToggle.value.hidden ? 'shown' : 'hidden'
    toast.add({ title: `Comment ${action}`, color: 'green' })
    isVisibilityModalOpen.value = false
    if (commentToToggle.value.hidden)
    {
      commentsFilter.value = filterOptions[1]
    }else{
      commentsFilter.value = filterOptions[2]
    }


  } catch (error) {
    toast.add({ title: 'Error', description: error.response?.data?.message || 'Failed to update visibility', color: 'red' })
  } finally {
    visibilityUpdating.value = false
  }
}

// --- RATING LOGIC ---

const handleRate = async (value) => {
  if (!selectedPub.value) return
  ratingUpdating.value = true

  try {
    let result
    if (userRatingObj.value) {
      result = await pubStore.updateRating(selectedPub.value.id, userRatingObj.value.id, value)
      toast.add({ title: 'Rating updated', color: 'green' })
    } else {
      result = await pubStore.addRating(selectedPub.value.id, value)
      toast.add({ title: 'Rating added', color: 'green' })
    }

    displayRatingAverage.value = result.ratingAverage
    displayRatingCount.value = result.ratingCount
    userRatingObj.value = await pubStore.fetchUserRating(selectedPub.value.id)
    pubStore.fetchPublications()

  } catch (error) {
    toast.add({ title: 'Error rating', description: error.response?.data?.message, color: 'red' })
  } finally {
    ratingUpdating.value = false
  }
}

const handleClearRating = async () => {
  if (!userRatingObj.value) return
  ratingUpdating.value = true

  try {
    const result = await pubStore.deleteRating(selectedPub.value.id, userRatingObj.value.id)
    toast.add({ title: 'Rating removed', color: 'green' })

    displayRatingAverage.value = result.rating_medio_atualizado
    displayRatingCount.value = result.numero_ratings
    userRatingObj.value = null
    pubStore.fetchPublications()
  } catch (error) {
    toast.add({ title: 'Error removing rating', color: 'red' })
  } finally {
    ratingUpdating.value = false
  }
}

const handleDownload = async (pub) => {
  if (!pub) return
  downloadingId.value = pub.id

  try {
    await pubStore.downloadFile(pub.id, pub.titulo, pub.fileType)
    toast.add({ title: 'Download started', color: 'green' })
  } catch (error) {
    toast.add({ title: 'Download failed', description: 'File not found or server error', color: 'red' })
  } finally {
    downloadingId.value = null
  }
}
</script>