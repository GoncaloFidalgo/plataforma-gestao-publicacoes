<template> 
  <div v-if="loadError">Error loading administrator: {{ loadError.message }}</div>
  <form v-else @submit.prevent="update"> 
    <div>Username: 
      <input v-model="adminForm.username" type="text" disabled>
    </div>
    <div>Password: 
      <input v-model="adminForm.password" type="password" placeholder="Leave empty to keep current">
    </div>
    <div>Name: 
      <input v-model="adminForm.name" type="text" required>
    </div> 
    <div>E-mail: 
      <input v-model="adminForm.email" type="email" required>
    </div>
 
    <button type="reset" @click="resetForm">RESET</button> 
    <button type="submit">UPDATE</button> 
    <nuxt-link :to="`/administrators/${route.params.username}`">Cancel</nuxt-link> 
  </form> 
  {{ message }} 
</template> 
 
<script setup> 
const route = useRoute()
const config = useRuntimeConfig() 
const api = config.public.apiBase 

// Load existing administrator data
const { data: administrator, error: loadError } = await useFetch(`${api}/administrators/${route.params.username}`)

const adminForm = reactive({ 
  username: administrator.value?.username || '', 
  password: '',
  name: administrator.value?.name || '',
  email: administrator.value?.email || ''
}) 
 
const message = ref('') 

function resetForm() {
  adminForm.password = ''
  adminForm.name = administrator.value?.name || ''
  adminForm.email = administrator.value?.email || ''
}
 
async function update() { 
  const { error } = await useFetch(`${api}/administrators/${route.params.username}`, { 
    method: 'PUT', 
    body: { ...adminForm }
  }) 
  if (!error.value) {
    navigateTo(`/administrators/${route.params.username}`)
  } else {
    message.value = error.value?.message || 'Failed to update administrator'
  }
} 
</script>