<template> 
  <form @submit.prevent="create"> 
    <div>Username: 
      <input v-model="teacherForm.username" type="text" required>
    </div>
    <div>Password: 
      <input v-model="teacherForm.password" type="password" required>
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
 
    <button type="reset">RESET</button> 
    <button type="submit">CREATE</button> 
    <nuxt-link to="/teachers">Return</nuxt-link> 
  </form> 
  {{ message }} 
</template> 
 
<script setup> 
const teacherForm = reactive({ 
  username: '', 
  password: '', 
  name: '', 
  email: '', 
  office: ''
}) 
 
const message = ref('') 
 
const config = useRuntimeConfig() 
const api = config.public.apiBase 
 
async function create() { 
  const { error } = await useFetch(`${api}/teachers/`, { 
    method: 'POST',
    body: { ...teacherForm }
  }) 
  if (!error.value) return navigateTo('/teachers') 
  message.value = error.value?.message || 'Failed to create teacher'
} 
</script>