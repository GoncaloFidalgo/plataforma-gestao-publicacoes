<template>
  <div class="space-y-6">

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

    <!-- Table -->
    <UCard :ui="{ body: { padding: '' } }">
      <UTable
          :data="filteredPublications"
          :columns="columns"
          :loading="pubStore.loading"
      >
        <!-- Custom Cell: Type (Icon) -->
        <template #fileType-cell="{ row }">
          <UBadge
              :color="row.original.fileType === 'pdf' ? 'red' : 'yellow'"
              variant="subtle"
              size="xs"
          >
            {{ row.original.fileType.toUpperCase() }}
          </UBadge>
        </template>

        <!-- Custom Cell: Created At -->
        <template #createdAt-cell="{ row }">
          {{ new Date(row.original.createdAt).toLocaleDateString() }}
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
        </template>

      </UTable>
    </UCard>
  </div>
</template>

<script setup>
const pubStore = usePublicationStore()
const toast = useToast()

definePageMeta({
  layout: 'default'
})

// Columns configuration
const columns = [
  { accessorKey: 'id', header: '#' },
  { accessorKey: 'fileType', header: 'Type' },
  { accessorKey: 'titulo', header: 'Title' },
  { accessorKey: 'areaCientifica', header: 'Area' },
  { accessorKey: 'tipo', header: 'Category' },
  { accessorKey: 'creatorName', header: 'Uploader' },
  { accessorKey: 'createdAt', header: 'Date' },
  { id: 'actions', header: 'Download' }
]

const search = ref('')
const filteredPublications = computed(() => {
  if (!search.value) return pubStore.publications
  return pubStore.publications.filter(p =>
      p.titulo.toLowerCase().includes(search.value.toLowerCase())
  )
})

// Guardar o id da publicacao para saber de qual publicacao estamos a fazer download para mostrar o loading em vez do icon de download
const downloadingId = ref(null)

onMounted(() => {
  pubStore.fetchPublications()
})

const handleDownload = async (pub) => {
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