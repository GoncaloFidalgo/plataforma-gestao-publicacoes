<template> 
  <div v-if="error">Error: {{ error.message }}</div> 
  <div v-else-if="subject"> 
    <h2>Subject Details</h2>
    <div><strong>Code:</strong> {{ subject.code }}</div>
    <div><strong>Name:</strong> {{ subject.name }}</div>
    <div><strong>School Year:</strong> {{ subject.schoolYear }}</div>
    <div><strong>Course Year:</strong> {{ subject.courseYear }}</div>
    <div><strong>Course:</strong> {{ subject.courseName }}</div>
    
    <div style="margin-top: 20px;">
      <nuxt-link :to="`/subjects/${subject.code}/edit`">Edit</nuxt-link> | 
      <nuxt-link to="/subjects">Return to List</nuxt-link>
    </div>
  </div> 

  <div v-if="students && students.length > 0" style="margin-top: 30px;"> 
    <h2>Enrolled Students:</h2> 
    <ul>
      <li v-for="student in students" :key="student.username">
        {{ student.name }} ({{ student.username }})
      </li>
    </ul>
  </div> 

  <div v-if="teachers && teachers.length > 0" style="margin-top: 30px;"> 
    <h2>Assigned Teachers:</h2> 
    <ul>
      <li v-for="teacher in teachers" :key="teacher.username">
        {{ teacher.name }} ({{ teacher.username }})
      </li>
    </ul>
  </div> 
</template> 
 
<script setup> 
const route = useRoute()
const config = useRuntimeConfig() 
const api = config.public.apiBase 

const { data: subject, error } = await useFetch(`${api}/subjects/${route.params.code}`) 
const { data: students } = await useFetch(`${api}/subjects/${route.params.code}/students`) 
const { data: teachers } = await useFetch(`${api}/subjects/${route.params.code}/teachers`) 
</script>