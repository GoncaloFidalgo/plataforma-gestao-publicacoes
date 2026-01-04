<template> 
  <div v-if="error">Error: {{ error.message }}</div>
  <div v-else-if="student"> 
    <h2>Details of {{ username }}</h2> 
    <div><strong>Username:</strong> {{ student.username }}</div>
    <div><strong>Name:</strong> {{ student.name }}</div>
    <div><strong>E-mail:</strong> {{ student.email }}</div>
    <div><strong>Course:</strong> {{ student.courseName }}</div>
    
    <div style="margin-top: 20px;">
      <nuxt-link :to="`/students/${username}/subjects`">View Subjects</nuxt-link> | 
      <nuxt-link :to="`/students/${username}/edit`">Edit</nuxt-link> | 
      <nuxt-link to="/students">Return to List</nuxt-link>
    </div>
  </div> 
</template> 
 
<script setup> 
const route = useRoute() 
const username = route.params.username 
 
const config = useRuntimeConfig() 
const api = config.public.apiBase 
const { data: student, error } = await useFetch(`${api}/students/${username}`) 
</script>