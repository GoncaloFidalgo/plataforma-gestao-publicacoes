<template> 
  <div v-if="loadError">Error loading teacher: {{ loadError.message }}</div>
  <form v-else @submit.prevent="update"> 
    <div>Username: 
      <input v-model="teacherForm.username" type="text" disabled>
    </div>
    <div>Password: 
      <input v-model="teacherForm.password" type="password" placeholder="Leave empty to keep current">
    </div>
    <div>Name: 
      <input v-model="teacherForm.name" type="text" required>
    </div> 
    <div>E-mail: 
      <input v-model="teacherForm.email" type="email" required>
    </div>
    <div>Office: 
      <input v-model="teacherForm.office" type="text" required>
    </div>
 
    <button type="reset" @click="resetForm">RESET</button> 
    <button type="submit">UPDATE</button> 
    <nuxt-link :to="`/teachers/${route.params.username}`">Cancel</nuxt-link> 
  </form> 
  {{ message }} 
</template> 
 
<script setup> 
const route = useRoute()
const config = useRuntimeConfig() 
const api = config.public.apiBase 

// Load existing teacher data
const { data: teacher, error: loadError } = await useFetch(`${api}/teachers/${route.params.username}`)

const teacherForm = reactive({ 
  username: teacher.value?.username || '', 
  password: '',
  name: teacher.value?.name || '',
  email: teacher.value?.email || '',
  office: teacher.value?.office || ''
}) 
 
const message = ref('') 

function resetForm() {
  teacherForm.password = ''
  teacherForm.name = teacher.value?.name || ''
  teacherForm.email = teacher.value?.email || ''
  teacherForm.office = teacher.value?.office || ''
}
 
async function update() { 
  const { error } = await useFetch(`${api}/teachers/${route.params.username}`, { 
    method: 'PUT', 
    body: { ...teacherForm }
  }) 
  if (!error.value) {
    navigateTo(`/teachers/${route.params.username}`)
  } else {
    message.value = error.value?.message || 'Failed to update teacher'
  }
} 
</script>