<template> 
  <form @submit.prevent="create"> 
    <div>Code: 
      <input v-model.number="courseForm.code" type="number" required>
    </div>
    <div>Name: 
      <input v-model="courseForm.name" type="text" required>
    </div> 
 
    <button type="reset">RESET</button> 
    <button type="submit">CREATE</button> 
    <nuxt-link to="/courses">Return</nuxt-link> 
  </form> 
  {{ message }} 
</template> 
 
<script setup> 
const courseForm = reactive({ 
  code: null, 
  name: ''
}) 
 
const message = ref('') 
 
const config = useRuntimeConfig() 
const api = config.public.apiBase 
 
async function create() { 
  const { error } = await useFetch(`${api}/courses/`, { 
    method: 'POST',
    body: { ...courseForm }
  }) 
  if (!error.value) return navigateTo('/courses') 
  message.value = error.value?.message || 'Failed to create course' 
} 
</script>