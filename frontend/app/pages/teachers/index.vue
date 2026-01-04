<template> 
  <nuxt-link to="/">Home</nuxt-link> | 
  <nuxt-link to="/students">Manage Students</nuxt-link> | 
  <nuxt-link to="/courses">Manage Courses</nuxt-link> | 
  <nuxt-link to="/subjects">Manage Subjects</nuxt-link>
  <div v-if="error">Error: {{ error.message }}</div> 
  <div v-else> 
    <nuxt-link to="/teachers/create">Create a New Teacher</nuxt-link> 
    <h2>Teachers</h2> 
    <table> 
      <thead> 
        <tr>
          <th>Username</th>
          <th>Name</th>
          <th>E-mail</th>
          <th>Office</th>
          <th>Actions</th>
        </tr> 
      </thead> 
      <tbody> 
        <tr v-for="teacher in teachers" :key="teacher.username"> 
          <td>{{ teacher.username }}</td> 
          <td>{{ teacher.name }}</td> 
          <td>{{ teacher.email }}</td> 
          <td>{{ teacher.office }}</td> 
          <td>
            <nuxt-link :to="`/teachers/${teacher.username}`">Details</nuxt-link> | 
            <nuxt-link :to="`/teachers/${teacher.username}/edit`">Edit</nuxt-link> | 
            <button @click="deleteTeacher(teacher.username)">Delete</button>
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
const { data: teachers, error, refresh } = await useFetch(`${api}/teachers/`) 

async function deleteTeacher(username) {
  if (!confirm(`Are you sure you want to delete teacher ${username}?`)) return
  
  const { error: deleteError } = await useFetch(`${api}/teachers/${username}`, { 
    method: 'DELETE'
  }) 
  
  if (!deleteError.value) {
    refresh()
  } else {
    alert(`Error deleting teacher: ${deleteError.value?.message || deleteError.value}`)
  }
}
</script>