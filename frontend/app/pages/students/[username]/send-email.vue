<template> 
  <div v-if="error"> 
    <h2>Error loading student data</h2> 
    <pre>{{ error }}</pre> 
  </div> 
 
  <div v-if="student"> 
    <h2>Send an E-mail to Student {{ student.name }}</h2> 
    <form @submit.prevent="send"> 
      <div>Subject: 
        <input v-model.trim="formData.subject" /> 
        <span v-if="subjectError" class="error"> 
          ERROR: {{ subjectError }}</span></div> 
 
      <div>Body: 
        <input v-model="formData.body" /> 
        <span v-if="bodyError" class="error"> 
          ERROR: {{ bodyError }}</span></div> 
 
      <button type="submit" :disabled="isFormInvalid">SEND</button> 
    </form> 
  </div> 
 
  <hr> 
  <pre>{{ messages }}</pre> 
</template> 
 
<script setup> 
const formData = reactive({ 
  subject: null, 
  body: null 
}) 
const messages = ref([]) 
 
const route = useRoute() 
const username = route.params.username 
 
const config = useRuntimeConfig() 
const api = config.public.apiBase 
const { data: student, error } = await useFetch(`${api}/students/${username}`) 
 
const subjectError = computed(() => { 
  if (formData.subject === null) return null 
 
  if ( ! formData.subject ) 
    return 'message subject is required' 
 
  return null 
}) 
 
const bodyError = computed(() => { 
  if (formData.body === null) return null 
 
  if ( ! formData.body ) 
    return 'message body is required' 
 
  return null 
}) 
 
const isFormInvalid = computed(() => { 
  return subjectError.value || bodyError.value 
}) 
 
async function send() { 
  try { 
    await $fetch(`${api}/students/${username}/email`, { 
      method: 'POST', 
      headers: { 'Content-Type': 'application/json' }, 
      body: formData, 
      onResponse({ request, response, options }) { 
        messages.value.push({ 
          method: options.method, 
          request: request, 
          status: response.status, 
          statusText: response.statusText, 
          payload: response._data 
        }) 
      } 
    }) 
  } catch (e) { 
    console.log(e) 
  } 
} 
</script> 
 
<style> 
.error { 
  color: red; 
} 
</style> 