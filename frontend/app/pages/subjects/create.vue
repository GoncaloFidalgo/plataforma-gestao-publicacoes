<template> 
  <form @submit.prevent="create"> 
    <div>Code: 
      <input v-model.number="subjectForm.code" type="number" required>
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
        <option value="" disabled>Select a course</option>
        <option v-for="course in courses" :key="course.code" :value="course.code"> 
           {{ course.name }} 
        </option> 
      </select> 
    </div> 
 
    <button type="reset">RESET</button> 
    <button type="submit">CREATE</button> 
    <nuxt-link to="/subjects">Return</nuxt-link> 
  </form> 
  {{ message }} 
</template> 
 
<script setup> 
const subjectForm = reactive({ 
  code: null,
  name: '', 
  schoolYear: '',
  courseYear: null,
  courseCode: null
}) 
 
const message = ref('') 
 
const config = useRuntimeConfig() 
const api = config.public.apiBase 
const { data: courses } = await useFetch(`${api}/courses/`) 
 
async function create() { 
  const { error } = await useFetch(`${api}/subjects/`, { 
    method: 'POST',
    body: { ...subjectForm }
  }) 
  if (!error.value) return navigateTo('/subjects') 
  message.value = error.value?.message || 'Failed to create subject'
} 
</script>