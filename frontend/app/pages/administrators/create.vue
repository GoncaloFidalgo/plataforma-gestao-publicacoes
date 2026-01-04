<template> 
  <form @submit.prevent="create"> 
    <div>Username: 
      <input v-model="adminForm.username" type="text" required>
    </div>
    <div>Password: 
      <input v-model="adminForm.password" type="password" required>
    </div> 
    <div>Name: 
      <input v-model="adminForm.name" type="text" required>
    </div> 
    <div>E-mail: 
      <input v-model="adminForm.email" type="email" required>
    </div> 
 
    <button type="reset">RESET</button> 
    <button type="submit">CREATE</button> 
    <nuxt-link to="/administrators">Return</nuxt-link> 
  </form> 
  {{ message }} 
</template> 
 
<script setup> 
const adminForm = reactive({ 
  username: '', 
  password: '', 
  name: '', 
  email: ''
}) 
 
const message = ref('') 
 
const config = useRuntimeConfig() 
const api = config.public.apiBase 
 
async function create() { 
  const { error } = await useFetch(`${api}/administrators/`, { 
    method: 'POST',
    body: { ...adminForm }
  }) 
  if (!error.value) return navigateTo('/administrators') 
  message.value = error.value?.message || 'Failed to create administrator'
} 
</script>