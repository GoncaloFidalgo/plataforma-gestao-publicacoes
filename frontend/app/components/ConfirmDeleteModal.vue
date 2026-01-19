<template>
  <UModal v-model:open="isOpen" :title="title" >
    <template #content>
      <UCard>
        <template #header>
          <div class="flex items-center justify-between">
            <h3 class="text-base font-semibold text-gray-900 dark:text-white">
              {{ title }}
            </h3>
            <UButton
                color="gray"
                variant="ghost"
                icon="i-heroicons-x-mark-20-solid"
                class="-my-1"
                @click="close"
            />
          </div>
        </template>

        <div class="space-y-2">
          <p>
            Are you sure you want to delete the {{ type }} <strong>{{ itemName }}</strong>?
          </p>
          <p class="text-sm text-red-500">
            This action cannot be undone.
          </p>
        </div>

        <template #footer>
          <div class="flex justify-end gap-3">
            <UButton
                label="Cancel"
                color="neutral"
                variant="ghost"
                @click="close"
            />
            <UButton
                label="Delete"
                color="error"
                :loading="loading"
                @click="confirm"
            />
          </div>
        </template>
      </UCard>
    </template>
  </UModal>
</template>

<script setup>
const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  title: {
    type: String,
    default: 'Confirm Deletion'
  },
  type: {
    type: String,
    default: 'item'
  },
  itemName: {
    type: String,
    default: ''
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'confirm'])

const isOpen = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const close = () => {
  isOpen.value = false
}

const confirm = () => {
  emit('confirm')
}
</script>