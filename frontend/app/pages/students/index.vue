<template> 
  <nuxt-link to="/">Home</nuxt-link> | 
  <nuxt-link to="/courses">Manage Courses</nuxt-link>
  <div v-if="error">Error: {{ error.message }}</div> 
  <div v-else> 
    <nuxt-link to="/students/create">Create a New Student</nuxt-link> 
    <h2>Students</h2> 
    <table> 
      <thead> 
        <tr><th>Username</th><th>Name</th><th>E-mail</th><th>Course</th><th>Actions</th></tr> 
      </thead> 
      <tbody> 
        <tr v-for="student in students" :key="student.username"> 
          <td>{{ student.username }}</td> 
          <td>{{ student.name }}</td> 
          <td>{{ student.email }}</td> 
          <td>{{ student.courseName }}</td> 
          <td>
            <nuxt-link :to="`/students/${student.username}`">Details</nuxt-link> | 
            <nuxt-link :to="`/students/${student.username}/edit`">Edit</nuxt-link> | 
            <nuxt-link :to="`/students/${student.username}/send-email`">
              <button>Send Email</button>
            </nuxt-link> | 
            <button @click="deleteStudent(student.username)">Delete</button>
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
const { data: students, error, refresh } = await useFetch(`${api}/students/`) 

async function deleteStudent(username) {
  if (!confirm(`Are you sure you want to delete student ${username}?`)) return
  
  const { error: deleteError } = await useFetch(`${api}/students/${username}`, { 
    method: 'DELETE'
  }) 
  
  if (!deleteError.value) {
    refresh()
  } else {
    alert(`Error deleting student: ${deleteError.value?.message || deleteError.value}`)
  }
}
</script>