<template>
  <div class="max-w-3xl mx-auto py-8 space-y-6">

    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold text-gray-900 dark:text-white">Upload Publication</h1>
      <UButton
          icon="i-heroicons-arrow-left"
          color="gray"
          variant="ghost"
          label="Back"
          to="/publications"
      />
    </div>

    <UCard>
      <UForm :schema="schema" :state="state" @submit="handleSubmit" class="space-y-6">

        <!-- Title -->
        <UFormField label="Title" name="titulo" required>
          <UInput v-model="state.titulo" placeholder="e.g. Introduction to AI" size="lg" class="w-full" />
        </UFormField>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <!-- Type -->
          <UFormField label="Type" name="tipo" required>
            <USelectMenu
                v-model="state.tipo"
                :items="typeOptions"
                placeholder="Select type"
                class="w-full"
            />
          </UFormField>

          <!-- Scientific Area -->
          <UFormField label="Scientific Area" name="areaCientifica" required>
            <USelectMenu
                v-model="state.areaCientifica"
                :items="areaOptions"
                placeholder="Select area"
                class="w-full"
            />
          </UFormField>
        </div>

        <!-- Tags -->
        <UFormField label="Tags" name="tags">
          <USelectMenu
              v-model="state.tags"
              :items="tagOptions"
              placeholder="Select tags"
              multiple
              searchable
              class="w-full"
          />
        </UFormField>

        <!-- Description -->
        <UFormField label="Description" name="descricao" required>
          <UTextarea v-model="state.descricao" placeholder="Enter a brief abstract or description..." :rows="4" class="w-full" />
        </UFormField>

        <!-- File Upload -->
        <UFormField label="File (PDF or ZIP)" name="file" required>
          <div class="flex items-center gap-4">
            <input
                type="file"
                ref="fileInput"
                accept=".pdf,.zip"
                @change="handleFileChange"
                class="block w-full text-sm text-gray-500
                file:mr-4 file:py-2 file:px-4
                file:rounded-full file:border-0
                file:text-sm file:font-semibold
                file:bg-primary-50 file:text-primary-700
                hover:file:bg-primary-100 dark:file:bg-gray-800 dark:file:text-gray-200"
            />
          </div>
          <p v-if="fileError" class="text-red-500 text-xs mt-1">{{ fileError }}</p>
        </UFormField>

        <!-- Options -->
        <UFormField name="hidden">
          <UCheckbox v-model="state.hidden" label="Keep this publication hidden (Private)" />
        </UFormField>

        <!-- Actions -->
        <div class="flex justify-end gap-3 pt-4">
          <UButton label="Cancel" color="gray" variant="ghost" to="/publications" />
          <UButton type="submit" label="Upload Publication" color="primary" :loading="uploading" />
        </div>

      </UForm>
    </UCard>
  </div>
</template>

<script setup>
import { z } from 'zod'

const pubStore = usePublicationStore()
const tagStore = useTagStore()
const referenceStore = useReferenceStore()
const toast = useToast()


// State
const uploading = ref(false)
const fileError = ref('')
const fileInput = ref(null)

const state = reactive({
  titulo: '',
  tipo: '',
  areaCientifica: '',
  descricao: '',
  tags: [],
  hidden: false,
  file: null
})

const tagOptions = computed(() => {
  return tagStore.tags
      .filter(t => !t.hidden)
      .map(t => t.name)
})
const typeOptions = computed(() => {
  return referenceStore.types
      .map(t => t.name)
})
const areaOptions = computed(() => {
  return referenceStore.areas
      .map(t => t.name)
})

onMounted(() => {
 tagStore.fetchTags()
referenceStore.fetchTypes()
  referenceStore.fetchAreas()
})

const schema = z.object({
  titulo: z.string().min(3, 'Title must be at least 3 characters'),
  tipo: z.string().min(1, 'Type is required'),
  areaCientifica: z.string().min(1, 'Scientific area is required'),
  descricao: z.string().min(10, 'Description must be at least 10 characters')
})

const handleFileChange = (event) => {
  const file = event.target.files[0]
  if (!file) {
    state.file = null
    return
  }

  // Validate extension
  const validTypes = ['application/pdf', 'application/zip', 'application/x-zip-compressed']
  if (!validTypes.includes(file.type) && !file.name.endsWith('.pdf') && !file.name.endsWith('.zip')) {
    fileError.value = 'Only PDF or ZIP files are allowed.'
    state.file = null
    event.target.value = ''
    return
  }

  // Validate size
  if (file.size > 10 * 1024 * 1024) {
    fileError.value = 'File size exceeds 10MB.'
    state.file = null
    event.target.value = ''
    return
  }

  fileError.value = ''
  state.file = file
}

const handleSubmit = async () => {
  if (!state.file) {
    fileError.value = 'Please select a file to upload.'
    return
  }

  uploading.value = true
  try {

    await pubStore.create({
      ...state,
      autores: []
    })

    toast.add({ title: 'Success', description: 'Publication uploaded successfully!', color: 'green' })
    await navigateTo('/publications')

  } catch (error) {
    const msg = error.response?.data?.mensagem || error.response?.data || 'Failed to upload'
    toast.add({ title: 'Error', description: msg, color: 'red' })
  } finally {
    uploading.value = false
  }
}
</script>