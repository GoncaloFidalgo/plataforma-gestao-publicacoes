<template> 
  <div v-if="error">Error: {{ error.message }}</div> 
  <div v-else-if="teacher"> 
    <h2>Details of {{ username }}</h2> 
    <div><strong>Username:</strong> {{ teacher.username }}</div>
    <div><strong>Name:</strong> {{ teacher.name }}</div>
    <div><strong>E-mail:</strong> {{ teacher.email }}</div>
    <div><strong>Office:</strong> {{ teacher.office }}</div>
    
    <div style="margin-top: 20px;">
      <nuxt-link :to="`/teachers/${username}/edit`">Edit</nuxt-link> | 
      <nuxt-link to="/teachers">Return to List</nuxt-link>
    </div>
  </div> 
 
  <div v-if="subjects && subjects.length > 0" style="margin-top: 30px;"> 
    <h2>Teaching Subjects:</h2> 
    <ul>
      <li v-for="subject in subjects" :key="subject.code">
        {{ subject.name }} ({{ subject.code }}) - Year {{ subject.courseYear }}
      </li>
    </ul>
  </div> 
 
  <div v-if="messages.length > 0" style="margin-top: 20px; color: red;">
    <h2>Error messages:</h2> 
    <div v-for="(msg, index) in messages" :key="index">
      {{ msg.message || msg }}
    </div>
  </div>
</template> 
 
<script setup> 
const route = useRoute() 
const username = route.params.username 
 
const config = useRuntimeConfig() 
const api = config.public.apiBase 
const {data: teacher, error: teacherErr} = await useFetch(`${api}/teachers/${username}`) 
const {data: subjects, error: subjectsErr} = await useFetch(`${api}/teachers/${username}/subjects`) 
const messages = ref([]) 
if (teacherErr.value) messages.value.push(teacherErr.value) 
if (subjectsErr.value) messages.value.push(subjectsErr.value) 
</script>