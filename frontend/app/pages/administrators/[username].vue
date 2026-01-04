<template> 
  <div v-if="error">Error: {{ error.message }}</div> 
  <div v-else-if="administrator"> 
    <h2>Details of {{ username }}</h2> 
    <div><strong>Username:</strong> {{ administrator.username }}</div>
    <div><strong>Name:</strong> {{ administrator.name }}</div>
    <div><strong>E-mail:</strong> {{ administrator.email }}</div>
    
    <div style="margin-top: 20px;">
      <nuxt-link :to="`/administrators/${username}/edit`">Edit</nuxt-link> | 
      <nuxt-link to="/administrators">Return to List</nuxt-link>
    </div>
  </div> 
</template> 
 
<script setup> 
const route = useRoute() 
const username = route.params.username 
 
const config = useRuntimeConfig() 
const api = config.public.apiBase 
const { data: administrator, error } = await useFetch(`${api}/administrators/${username}`) 
</script>