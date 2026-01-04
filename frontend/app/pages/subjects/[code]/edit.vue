<template> 
  <div v-if="loadError">Error loading subject: {{ loadError.message }}</div>
  <form v-else @submit.prevent="update"> 
    <div>Code: 
      <input v-model.number="subjectForm.code" type="number" disabled>
    </div>
    <div>Name: 
      <input v-model="subjectForm.name" type="text" required>
    </div> 
    <div>School Year: 
      <input v-model="subjectForm.schoolYear" type="text" required placeholder="e.g., 2024/2025">
    </div>
    <div>Course Year: 
      <input v-model.number="subjectForm.courseYear" type="number" required min="1" max="5">
    </div>
    <div>Course:  
      <select v-model.number="subjectForm.courseCode" required> 
        <option v-for="course in courses" :key="course.code" :value="course.code"> 
           {{ course.name }} 
        </option> 
      </select> 
    </div> 
 
    <button type="reset" @click="resetForm">RESET</button> 
    <button type="submit">UPDATE</button> 
    <nuxt-link :to="`/subjects/${route.params.code}`">Cancel</nuxt-link> 
  </form> 
  {{ message }} 
</template> 
 
<script setup> 
const route = useRoute()
const config = useRuntimeConfig() 
const api = config.public.apiBase 

// Load existing subject data
const { data: subject, error: loadError } = await useFetch(`${api}/subjects/${route.params.code}`)

// Load courses for dropdown
const { data: courses } = await useFetch(`${api}/courses/`)

const subjectForm = reactive({ 
  code: subject.value?.code || null, 
  name: subject.value?.name || '',
  schoolYear: subject.value?.schoolYear || '',
  courseYear: subject.value?.courseYear || null,
  courseCode: subject.value?.courseCode || null
}) 
 
const message = ref('') 

function resetForm() {
  subjectForm.name = subject.value?.name || ''
  subjectForm.schoolYear = subject.value?.schoolYear || ''
  subjectForm.courseYear = subject.value?.courseYear || null
  subjectForm.courseCode = subject.value?.courseCode || null
}
 
async function update() { 
  const { error } = await useFetch(`${api}/subjects/${route.params.code}`, { 
    method: 'PUT', 
    body: { ...subjectForm }
  }) 
  if (!error.value) {
    navigateTo(`/subjects/${route.params.code}`)
  } else {
    message.value = error.value?.message || 'Failed to update subject'
  }
} 
</script>