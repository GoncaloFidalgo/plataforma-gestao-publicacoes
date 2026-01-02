<template> 
  <div> 
    <h1>Login Form</h1> 
    <div>Username: 
      <input v-model="loginFormData.username"></div> 
    <div>Password: 
      <input v-model="loginFormData.password" type="password"></div> 
    <button @click="login">LOGIN</button> 
    <button @click="reset">RESET</button> 
  </div> 
 
  <div v-if="token"> 
    <h2>API Request Form</h2> 
    <div>Request: <code>{{ apiFormData.method }} {{ api }}</code>/ 
      <input v-model="apiFormData.path"></div> 

    <div>
      Method:
      <select v-model="apiFormData.method">
        <option>GET</option>
        <option>POST</option>
        <option>PUT</option>
        <option>PATCH</option>
        <option>DELETE</option>
      </select>
    </div>

    <div v-if="apiFormData.method !== 'GET'">
      Body (JSON): 
      <textarea v-model="apiFormData.body" rows="6" cols="60" placeholder='{"key":"value"}'></textarea>
    </div>

    <div>Token: {{ token }}</div> 
    <button @click="sendRequest">SEND REQUEST</button> 
  </div>

   <div v-if="messages.length > 0"> 
    <h2>Messages</h2> 
    <div v-for="(message, idx) in messages" :key="idx"><pre>{{ message }}</pre></div> 
  </div> 
</template> 
 
<script setup> 
const config = useRuntimeConfig() 
const api = config.public.apiBase; 
 
const loginFormData = reactive({ 
  username: null, 
  password: null 
}) 
 
const apiFormData = reactive({ 
  path: "students",
  method: "GET",
  body: ""
}) 
 
const token = ref(null) 
const messages = ref([])

async function login() { 
  reset() 
  try { 
    await $fetch(`${api}/auth/login`, { 
      method: 'POST', 
      headers: { 
        'Content-Type': 'application/json', 
        Accept: 'application/json', 
      }, 
      body: loginFormData, 
      onResponse({ request, response, options }) { 
        messages.value.push({ 
          method: options.method, 
          request: request, 
          status: response.status, 
          statusText: response.statusText, 
          payload: response._data 
        }) 
        if (response.status == 200)  
          token.value = response._data 
      } 
    }) 
  } catch (e) { 
    console.error('login request failed: ', e) 
  } 
} 
 
function reset() { 
  token.value = null 
  messages.value = []
  apiFormData.method = "GET"
  apiFormData.path = "students"
  apiFormData.body = ""
}

async function sendRequest() { 
  try { 
    const method = (apiFormData.method || 'GET').toUpperCase()
    const headers = {
      Accept: 'application/json'
    }
    if (token.value) headers.Authorization = `Bearer ${token.value}`

    let bodyToSend = undefined
    if (method !== 'GET' && apiFormData.body && apiFormData.body.trim() !== '') {
      try {
        bodyToSend = JSON.parse(apiFormData.body)
        headers['Content-Type'] = 'application/json'
      } catch (e) {
        messages.value.push({ error: 'Invalid JSON in request body', detail: apiFormData.body })
        return
      }
    }

    await $fetch(`${api}/${apiFormData.path}`, { 
      method, 
      headers, 
      body: bodyToSend, 
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
    console.error('api request failed: ', e) 
    messages.value.push({ error: 'Request failed', detail: String(e) })
  } 
} 
</script>