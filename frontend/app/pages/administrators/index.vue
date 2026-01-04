<template>
  <div v-if="error">Error: {{ error.message }}</div> 
  <div v-else> 
    <nuxt-link to="/administrators/create">Create a New Administrator</nuxt-link> 
    <h2>Administrators</h2> 
    <table> 
      <thead> 
        <tr>
          <th>Username</th>
          <th>Name</th>
          <th>E-mail</th>
          <th>Actions</th>
        </tr> 
      </thead> 
      <tbody> 
        <tr v-for="admin in administrators" :key="admin.username"> 
          <td>{{ admin.username }}</td> 
          <td>{{ admin.name }}</td> 
          <td>{{ admin.email }}</td> 
          <td>
            <nuxt-link :to="`/administrators/${admin.username}`">Details</nuxt-link> | 
            <nuxt-link :to="`/administrators/${admin.username}/edit`">Edit</nuxt-link> | 
            <button @click="deleteAdministrator(admin.username)">Delete</button>
          </td> 
        </tr> 
      </tbody> 
    </table> 
  </div> 
  <button @click.prevent="refresh">Refresh Data</button> 
</template> 
 
<script setup> 
const config = useRuntimeConfig() 
const api = config.public.apiBase 
const { data: administrators, error, refresh } = await useFetch(`${api}/administrators/`) 

async function deleteAdministrator(username) {
  if (!confirm(`Are you sure you want to delete administrator ${username}?`)) return
  
  const { error: deleteError } = await useFetch(`${api}/administrators/${username}`, { 
    method: 'DELETE'
  }) 
  
  if (!deleteError.value) {
    refresh()
  } else {
    alert(`Error deleting administrator: ${deleteError.value?.message || deleteError.value}`)
  }
}
</script>