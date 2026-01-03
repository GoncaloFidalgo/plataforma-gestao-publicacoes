<template> 
  <div v-if="error">Error: {{ error.message }}</div> 
  <div v-else> 
    <nuxt-link to="/courses/create">Create a New Course</nuxt-link> 
    <h2>Courses</h2> 
    <table> 
      <thead> 
        <tr>
          <th>Code</th>
          <th>Name</th>
          <th>Actions</th>
        </tr> 
      </thead> 
      <tbody> 
        <tr v-for="course in courses" :key="course.code"> 
          <td>{{ course.code }}</td> 
          <td>{{ course.name }}</td> 
          <td>
            <nuxt-link :to="`/courses/${course.code}`">Details</nuxt-link> | 
            <nuxt-link :to="`/courses/${course.code}/edit`">Edit</nuxt-link> | 
            <button @click="deleteCourse(course.code)">Delete</button>
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
const { data: courses, error, refresh } = await useFetch(`${api}/courses/`) 

async function deleteCourse(code) {
  if (!confirm(`Are you sure you want to delete course ${code}?`)) return
  
  const requestOptions = { 
    method: 'DELETE'
  } 
  const { error: deleteError } = await useFetch(`${api}/courses/${code}`, requestOptions) 
  
  if (!deleteError.value) {
    refresh()
  } else {
    alert(`Error deleting course: ${deleteError.value?.message || deleteError.value}`)
  }
}
</script>