<template> 
  <nuxt-link to="/">Home</nuxt-link> | 
  <nuxt-link to="/students">Manage Students</nuxt-link> | 
  <nuxt-link to="/courses">Manage Courses</nuxt-link>
  <div v-if="error">Error: {{ error.message }}</div> 
  <div v-else> 
    <nuxt-link to="/subjects/create">Create a New Subject</nuxt-link> 
    <h2>Subjects</h2> 
    <table> 
      <thead> 
        <tr>
          <th>Code</th>
          <th>Name</th>
          <th>School Year</th>
          <th>Course Year</th>
          <th>Course</th>
          <th>Actions</th>
        </tr> 
      </thead> 
      <tbody> 
        <tr v-for="subject in subjects" :key="subject.code"> 
          <td>{{ subject.code }}</td> 
          <td>{{ subject.name }}</td> 
          <td>{{ subject.schoolYear }}</td> 
          <td>{{ subject.courseYear }}</td> 
          <td>{{ subject.courseName }}</td> 
          <td>
            <nuxt-link :to="`/subjects/${subject.code}`">Details</nuxt-link> | 
            <nuxt-link :to="`/subjects/${subject.code}/edit`">Edit</nuxt-link> | 
            <button @click="deleteSubject(subject.code)">Delete</button>
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
const { data: subjects, error, refresh } = await useFetch(`${api}/subjects/`) 

async function deleteSubject(code) {
  if (!confirm(`Are you sure you want to delete subject ${code}?`)) return
  
  const { error: deleteError } = await useFetch(`${api}/subjects/${code}`, { 
    method: 'DELETE'
  }) 
  
  if (!deleteError.value) {
    refresh()
  } else {
    alert(`Error deleting subject: ${deleteError.value?.message || deleteError.value}`)
  }
}
</script>