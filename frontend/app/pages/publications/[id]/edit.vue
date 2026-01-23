<template>
  <div class="max-w-3xl mx-auto py-8 space-y-6">

    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold text-gray-900 dark:text-white">Edit Publication</h1>
      <UButton
          icon="i-heroicons-arrow-left"
          color="gray"
          variant="ghost"
          label="Back"
          @click="$router.back()"
      />
    </div>

    <div v-if="loadingData" class="flex justify-center py-10">
      <UIcon name="i-heroicons-arrow-path" class="w-8 h-8 animate-spin text-gray-400" />
    </div>

    <UCard v-else>
      <UForm :schema="schema" :state="state" @submit="handleSubmit" class="space-y-6">

        <!-- Title -->
        <UFormField label="Title" name="titulo" required>
          <UInput v-model="state.titulo" size="lg" class="w-full" />
        </UFormField>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <!-- Type -->
          <UFormField label="Type" name="tipo" required>
            <USelectMenu
                v-model="state.tipo"
                :items="typeOptions"
                option-attribute="name"
                placeholder="Select type"
                class="w-full"
            >
              <template #label>
                <span v-if="state.tipo" class="truncate">{{ state.tipo.name }}</span>
                <span v-else class="text-gray-500">Select type</span>
              </template>
            </USelectMenu>
          </UFormField>

          <!-- Scientific Area -->
          <UFormField label="Scientific Area" name="areaCientifica" required>
            <USelectMenu
                v-model="state.areaCientifica"
                :items="areaOptions"
                v-bind="state.areaCientifica"
                option-attribute="name"
                placeholder="Select area"
                class="w-full"
            >
              <template #label>
                <span v-if="state.areaCientifica" class="truncate">{{ state.areaCientifica.name }}</span>
                <span v-else class="text-gray-500">Select area</span>
              </template>
            </USelectMenu>
          </UFormField>
        </div>
        <UFormField label="Authors" name="autores">
          <USelectMenu
              v-model="state.autores"
              :items="authorOptions"
              placeholder="Select authors"
              multiple
              searchable
              class="w-full"
          >
          </USelectMenu>
        </UFormField>
        <!-- Tags -->
        <UFormField label="Tags" name="tags">
          <USelectMenu
              v-model="state.tags"
              :items="tagOptions"
              placeholder="Select tags"
              multiple
              searchable
              class="w-full"
          >
            <template #label>
              <span v-if="state.tags.length" class="truncate">{{ state.tags.join(', ') }}</span>
              <span v-else class="text-gray-500">Select tags</span>
            </template>
          </USelectMenu>
        </UFormField>

        <!-- Description -->
        <UFormField label="Description" name="descricao" required>
          <UTextarea v-model="state.descricao" :rows="4" class="w-full" />
        </UFormField>

        <!-- File Upload (Optional on Edit) -->
        <UFormField label="Replace File (Optional)" name="file">
          <div class="space-y-2">
            <div v-if="existingFileName" class="text-sm text-gray-500 flex items-center gap-2">
              <UIcon name="i-heroicons-document" class="w-4 h-4" />
              Current file: <span class="font-medium text-gray-700 dark:text-gray-300">{{ existingFileName }}</span>
            </div>

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
          <UButton label="Cancel" color="gray" variant="ghost" @click="$router.back()" />
          <UButton type="submit" label="Save Changes" color="primary" :loading="saving" />
        </div>

      </UForm>
    </UCard>
  </div>
</template>

<script setup>
import { z } from 'zod'
import { useAPIStore } from '~/stores/api.js'

const route = useRoute()
const router = useRouter()
const apiStore = useAPIStore()
const pubStore = usePublicationStore()
const tagStore = useTagStore()
const refStore = useReferenceStore()
const toast = useToast()
const userStore = useUserStore()

const pubId = route.params.id
const loadingData = ref(true)
const saving = ref(false)
const fileError = ref('')
const existingFileName = ref('')

const state = reactive({
  titulo: '',
  tipo: null,
  areaCientifica: null,
  descricao: '',
  tags: [],
  hidden: false,
  file: null,
  autores: [],
})

// Data Sources
const typeOptions = computed(() => {
  return refStore.types
      .map(t => ({
        label: t.name,
        value: t.id,
      }))
})
const areaOptions = computed(() => {
  return refStore.areas
      .map(t => ({
        label: t.name,
        value: t.id,
      }))
})
const tagOptions = computed(() => tagStore.tags.map(t => t.name))
const authorOptions = computed(() => {
  return userStore.users.map(u => ({
    label: `${u.name} (${u.username})`,
    value: u.username
  }))
})
onMounted(async () => {
  try {
    // 1. Load Reference Data
    if (tagStore.tags.length === 0) await tagStore.fetchTags()
    await refStore.fetchTypes()
    await refStore.fetchAreas()
    await userStore.fetchUsers()
    // 2. Load Publication Details
    const pub = await apiStore.getPublications().then(list => list.find(p => p.id == pubId))

    if (!pub) {
      toast.add({ title: 'Error', description: 'Publication not found', color: 'red' })
      router.push('/me/publications')
      return
    }

    // 3. Populate State
    state.titulo = pub.titulo
    state.descricao = pub.descricao
    state.hidden = pub.hidden
    state.tags = pub.tags || []
    existingFileName.value = pub.file || 'Existing File'

    state.tipo = typeOptions.value.find(o => o.label === pub.tipo) || null
    state.autores = authorOptions.value.filter(opt =>
        pub.autores.some(a => a.username === opt.value)
    )

    state.areaCientifica = areaOptions.value.find(o => o.label === pub.areaCientifica) || null

  } catch (error) {
    console.error(error)
    toast.add({ title: 'Error', description: 'Failed to load publication data', color: 'red' })
  } finally {
    loadingData.value = false
  }
})

// Validation Schema
const schema = z.object({
  titulo: z.string().min(3, 'Title too short'),
  tipo: z.any().refine(val => val && val.value, 'Type is required'),
  areaCientifica: z.any().refine(val => val && val.value, 'Scientific area is required'),
  descricao: z.string().min(10, 'Description too short')
})

const handleFileChange = (event) => {
  const file = event.target.files[0]
  if (!file) {
    state.file = null
    return
  }

  const validTypes = ['application/pdf', 'application/zip', 'application/x-zip-compressed']
  if (!validTypes.includes(file.type) && !file.name.endsWith('.pdf') && !file.name.endsWith('.zip')) {
    fileError.value = 'Only PDF or ZIP files are allowed.'
    state.file = null
    event.target.value = ''
    return
  }

  fileError.value = ''
  state.file = file
}

const handleSubmit = async () => {
  saving.value = true
  try {
    await pubStore.update(pubId, {
      ...state,
      tipo: state.tipo.value,
      areaCientifica: state.areaCientifica.value,
      autores: state.autores.map(a => a.value)
    })

    toast.add({ title: 'Success', description: 'Publication updated!', color: 'green' })
    router.back()

  } catch (error) {
    console.log(error)
    const msg = error.response?.data?.mensagem || error.response?.data || 'Failed to update'
    toast.add({ title: 'Error', description: msg, color: 'red' })
  } finally {
    saving.value = false
  }
}
</script>