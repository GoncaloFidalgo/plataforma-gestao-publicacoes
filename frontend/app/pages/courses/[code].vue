<template> 
  <div v-if="error">Error: {{ error.message }}</div> 
  <div v-else-if="course"> 
    <h2>Course Details</h2>
    <div><strong>Code:</strong> {{ course.code }}</div>
    <div><strong>Name:</strong> {{ course.name }}</div>
    
    <div style="margin-top: 20px;">
      <nuxt-link :to="`/courses/${course.code}/edit`">Edit</nuxt-link> | 
      <nuxt-link to="/courses">Return to List</nuxt-link>
    </div>
  </div> 
</template> 
 
<script setup> 
const route = useRoute()
const config = useRuntimeConfig() 
const api = config.public.apiBase 

const { data: course, error } = await useFetch(`${api}/courses/${route.params.code}`) 
</script>