<template> 
  <div v-if="loadError">Error loading student: {{ loadError.message }}</div>
  <form v-else @submit.prevent="update"> 
    <div>Username: 
      <input v-model="studentForm.username" type="text" disabled>
    </div>
    <div>Password: 
      <input v-model="studentForm.password" type="password" placeholder="Leave empty to keep current">
    </div>
    <div>Name: 
      <input v-model="studentForm.name" type="text" required>
    </div> 
    <div>E-mail: 
      <input v-model="studentForm.email" type="email" required>
    </div>
    <div>Course:  
      <select v-model.number="studentForm.courseCode" required> 
        <option v-for="course in courses" :key="course.code" :value="course.code"> 
           {{ course.name }} 
        </option> 
      </select> 
    </div>
 
    <button type="reset" @click="resetForm">RESET</button> 
    <button type="submit">UPDATE</button> 
    <nuxt-link :to="`/students/${route.params.username}`">Cancel</nuxt-link> 
  </form> 
  {{ message }} 
</template> 
 
<script setup> 
const route = useRoute()
const config = useRuntimeConfig() 
const api = config.public.apiBase 

// Load existing student data
const { data: student, error: loadError } = await useFetch(`${api}/students/${route.params.username}`)

// Load courses for dropdown
const { data: courses } = await useFetch(`${api}/courses/`)

const studentForm = reactive({ 
  username: student.value?.username || '', 
  password: '',
  name: student.value?.name || '',
  email: student.value?.email || '',
  courseCode: student.value?.courseCode || null
}) 
 
const message = ref('') 

function resetForm() {
  studentForm.password = ''
  studentForm.name = student.value?.name || ''
  studentForm.email = student.value?.email || ''
  studentForm.courseCode = student.value?.courseCode || null
}
 
async function update() { 
  const { error } = await useFetch(`${api}/students/${route.params.username}`, { 
    method: 'PUT', 
    body: { ...studentForm }
  }) 
  if (!error.value) {
    navigateTo(`/students/${route.params.username}`)
  } else {
    message.value = error.value?.message || 'Failed to update student'
  }
} 
</script>