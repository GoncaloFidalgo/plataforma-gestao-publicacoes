<template>
  <div class="space-y-6">

    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900 dark:text-white">Tags Management</h1>
        <p class="text-gray-500 text-sm">Create and manage content tags</p>
      </div>
      <UButton
          icon="i-heroicons-plus"
          color="primary"
          label="New Tag"
          @click="openCreateModal"
      />
    </div>

    <!-- Data Table (Nuxt UI v3) -->
    <UCard :ui="{ body: { padding: '' } }">
      <UTable
          :data="filteredTags"
          :columns="columns"
          :loading="tagStore.loading"
      >
        <template #hidden-cell="{ row }">
          <UBadge
              :color="row.original.hidden ? 'gray' : 'green'"
              variant="subtle"
              size="xs"
          >
            {{ row.original.hidden ? 'Hidden' : 'Visible' }}
          </UBadge>
        </template>

        <template #actions-cell="{ row }">
          <div class="flex items-center gap-2">
            <UButton
                icon="i-heroicons-pencil-square"
                size="2xs"
                color="gray"
                variant="ghost"
                @click="openEditModal(row.original)"
            />
            <UButton
                icon="i-heroicons-trash"
                size="2xs"
                color="red"
                variant="ghost"
                @click="handleDelete(row.original)"
            />
          </div>
        </template>
      </UTable>
    </UCard>

    <!-- Create/Edit Modal -->
    <UModal
        v-model:open="isModalOpen"
        :title="isEditing ? 'Edit Tag' : 'Create New Tag'"
    >
      <!--
        FIX: Wrap content in UCard to restore padding, header, and footer styling.
        The #content slot overrides the default modal shell, so we must provide our own container.
      -->
      <template #content>
        <UCard>
          <template #header>
            <div class="flex items-center justify-between">
              <h3 class="text-base font-semibold leading-6 text-gray-900 dark:text-white">
                {{ isEditing ? 'Edit Tag' : 'Create New Tag' }}
              </h3>
              <UButton color="gray" variant="ghost" icon="i-heroicons-x-mark-20-solid" class="-my-1" @click="isModalOpen = false" />
            </div>
          </template>

          <UForm id="tag-form" :schema="schema" :state="state" @submit="handleSave" class="space-y-4">

            <UFormField label="Name (Unique ID)" name="name">
              <UInput v-model="state.name" :disabled="isEditing" placeholder="e.g. Java" class="w-full"/>
            </UFormField>

            <UFormField label="Description" name="description">
              <UTextarea v-model="state.description" placeholder="Short description..." class="w-full"/>
            </UFormField>

            <UFormField label="Scientific Area" name="scientific_area">
              <UInput v-model="state.scientific_area" placeholder="e.g. Computer Science" class="w-full"/>
            </UFormField>

            <UFormField name="hidden">
              <UCheckbox v-model="state.hidden" label="Hide this tag from public lists" />
            </UFormField>

          </UForm>

          <template #footer>
            <div class="flex justify-end gap-3">
              <UButton label="Cancel" color="neutral" variant="ghost" @click="isModalOpen = false" />
              <UButton type="submit" form="tag-form" label="Save Tag" color="primary" :loading="saving" />
            </div>
          </template>
        </UCard>
      </template>
    </UModal>

  </div>
</template>

<script setup>
import { z } from 'zod'

const tagStore = useTagStore()
const authStore = useAuthStore()
const toast = useToast()

definePageMeta({
  middleware: 'responsavel'
})

const columns = [
  { accessorKey: 'name', header: 'Name' },
  { accessorKey: 'description', header: 'Description' },
  { accessorKey: 'scientific_area', header: 'Area' },
  { accessorKey: 'publicacoes_count', header: 'Pubs' },
  { accessorKey: 'subscritores_count', header: 'Subs' },
  { accessorKey: 'hidden', header: 'Status' },
  { id: 'actions', header: 'Actions' }
]

const search = ref('')
const filteredTags = computed(() => {
  if (!search.value) return tagStore.tags
  return tagStore.tags.filter(t =>
      t.name.toLowerCase().includes(search.value.toLowerCase())
  )
})

const isModalOpen = ref(false)
const isEditing = ref(false)
const saving = ref(false)
const state = reactive({
  name: '',
  description: '',
  scientific_area: '',
  hidden: false
})

const schema = z.object({
  name: z.string().min(1, 'Name is required'),
  description: z.string().optional(),
  scientific_area: z.string().min(1, 'Area is required'),
  hidden: z.boolean()
})

onMounted(() => {
  tagStore.fetchTags()
})

const openCreateModal = () => {
  isEditing.value = false
  state.name = ''
  state.description = ''
  state.scientific_area = ''
  state.hidden = false
  isModalOpen.value = true
}

const openEditModal = (tag) => {
  isEditing.value = true
  state.name = tag.name
  state.description = tag.description
  state.scientific_area = tag.scientific_area
  state.hidden = tag.hidden
  isModalOpen.value = true
}

const handleSave = async () => {
  saving.value = true
  try {
    if (isEditing.value) {
      await tagStore.updateTag(state.name, state)
      toast.add({ title: 'Tag updated', color: 'green' })
    } else {
      await tagStore.createTag(state)
      toast.add({ title: 'Tag created', color: 'green' })
    }
    isModalOpen.value = false
  } catch (error) {
    const msg = error.response?.data || 'Failed to save tag'
    toast.add({ title: 'Error', description: msg, color: 'red' })
  } finally {
    saving.value = false
  }
}

const handleDelete = async (tag) => {
  if (!confirm(`Are you sure you want to delete tag "${tag.name}"?`)) return

  try {
    await tagStore.deleteTag(tag.name)
    toast.add({ title: 'Tag deleted', color: 'green' })
  } catch (error) {
    toast.add({ title: 'Error deleting tag', color: 'red' })
  }
}
</script>