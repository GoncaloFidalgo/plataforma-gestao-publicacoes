<template> 
  <div v-if="loadError">Error loading course: {{ loadError.message }}</div>
  <form v-else @submit.prevent="update"> 
    <div>Code: 
      <input v-model.number="courseForm.code" type="number" disabled>
    </div>
    <div>Name: 
      <input v-model="courseForm.name" type="text" required>
    </div> 
 
    <button type="reset" @click="resetForm">RESET</button> 
    <button type="submit">UPDATE</button> 
    <nuxt-link to="/courses">Cancel</nuxt-link> 
  </form> 
  {{ message }} 
</template> 
 
<script setup> 
const route = useRoute()
const config = useRuntimeConfig() 
const api = config.public.apiBase 

// Load existing course data
const { data: course, error: loadError } = await useFetch(`${api}/courses/${route.params.code}`)

const courseForm = reactive({ 
  code: course.value?.code || null, 
  name: course.value?.name || ''
}) 
 
const message = ref('') 

function resetForm() {
  courseForm.name = course.value?.name || ''
}
 
async function update() { 
  const { error } = await useFetch(`${api}/courses/${route.params.code}`, { 
    method: 'PUT', 
    body: { ...courseForm }
  }) 
  if (!error.value) {
    navigateTo('/courses')
  } else {
    message.value = error.value?.message || 'Failed to update course'
  }
} 
</script>