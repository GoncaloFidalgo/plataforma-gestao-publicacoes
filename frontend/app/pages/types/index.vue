<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900 dark:text-white">Publication Types</h1>
        <p class="text-gray-500 text-sm">Manage types like 'Thesis', 'Article', etc.</p>
      </div>
      <UButton icon="i-heroicons-plus" color="primary" label="New Type" @click="isModalOpen = true" />
    </div>

    <UCard :ui="{ body: { padding: '' } }">
      <UTable :data="refStore.types" :columns="columns" :loading="refStore.loading">
        <template #actions-cell="{ row }">
          <UButton
              icon="i-heroicons-trash"
              size="2xs"
              color="red"
              variant="ghost"
              @click="handleDelete(row.original)"
          />
        </template>
      </UTable>
    </UCard>

    <UModal v-model:open="isModalOpen" title="New Publication Type">
      <template #content>
        <UCard>
          <template #header>
            <div class="flex items-center justify-between">
              <h3 class="font-semibold">Create Type</h3>
              <UButton color="gray" variant="ghost" icon="i-heroicons-x-mark-20-solid" @click="isModalOpen = false" />
            </div>
          </template>

          <UForm :schema="schema" :state="state" @submit="handleSave" class="space-y-4">
            <UFormField label="Name" name="name">
              <UInput v-model="state.name" placeholder="e.g. Thesis" class="w-full" autofocus />
            </UFormField>

            <div class="flex justify-end gap-3 pt-4">
              <UButton label="Cancel" color="neutral" variant="ghost" @click="isModalOpen = false" />
              <UButton type="submit" label="Save" color="primary" :loading="saving" />
            </div>
          </UForm>
        </UCard>
      </template>
    </UModal>
  </div>
</template>

<script setup>
import { z } from 'zod'
const refStore = useReferenceStore()
const toast = useToast()

definePageMeta({
  layout: 'default',
  middleware: 'responsavel' // Admin or Responsavel
})

const columns = [
  { accessorKey: 'id', header: 'ID' },
  { accessorKey: 'name', header: 'Name' },
  { id: 'actions', header: '' }
]

const isModalOpen = ref(false)
const saving = ref(false)
const state = reactive({ name: '' })
const schema = z.object({ name: z.string().min(1, 'Name is required') })

onMounted(() => {
  refStore.fetchTypes()
})

const handleSave = async () => {
  saving.value = true
  try {
    await refStore.createType(state.name)
    toast.add({ title: 'Type created', color: 'green' })
    isModalOpen.value = false
    state.name = ''
  } catch (error) {
    toast.add({ title: 'Error', description: error.response?.data?.message || 'Failed to create', color: 'red' })
  } finally {
    saving.value = false
  }
}

const handleDelete = async (item) => {
  if (!confirm(`Delete type "${item.name}"?`)) return
  try {
    await refStore.deleteType(item.id)
    toast.add({ title: 'Type deleted', color: 'green' })
  } catch (error) {
    toast.add({ title: 'Error', description: 'Cannot delete type in use', color: 'red' })
  }
}
</script>