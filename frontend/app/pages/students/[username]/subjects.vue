<template> 
  <div v-if="error">Error loading subjects: {{ error.message }}</div>
  <div v-else> 
    <h2>Subjects for {{ username }}</h2>
    
    <div v-if="subjects && subjects.length > 0"> 
      <table>
        <thead>
          <tr>
            <th>Code</th>
            <th>Name</th>
            <th>School Year</th>
            <th>Course Year</th>
            <th>Course</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="subject in subjects" :key="subject.code">
            <td>{{ subject.code }}</td>
            <td>{{ subject.name }}</td>
            <td>{{ subject.schoolYear }}</td>
            <td>{{ subject.courseYear }}</td>
            <td>{{ subject.courseName }}</td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <div v-else>
      <p>This student is not enrolled in any subjects.</p>
    </div>
    
    <div style="margin-top: 20px;">
      <nuxt-link :to="`/students/${username}`">Back to Student Details</nuxt-link> | 
      <nuxt-link to="/students">Return to List</nuxt-link>
    </div>
  </div> 
</template> 
 
<script setup> 
const route = useRoute() 
const username = computed(() => route.params.username)
 
const config = useRuntimeConfig() 
const api = config.public.apiBase 
const { data: subjects, error } = await useFetch(`${api}/students/${username.value}/subjects`, {
  key: `student-${username.value}-subjects`,
  watch: [username]
}) 
</script>