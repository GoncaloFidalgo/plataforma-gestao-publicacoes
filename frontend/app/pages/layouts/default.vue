<script setup> 
import {useAuthStore} from "~/stores/auth-store.js"; 
 
const router = useRouter() 
const authStore = useAuthStore() 
 
function logout() { 
  authStore.logout() 
  router.push('/') 
} 
</script> 
 
<template> 
  <nuxt-link to="/auth-test">Test</nuxt-link> 

  <nuxt-link to="/auth/login">Login</nuxt-link> 
   
  <nuxt-link to="/students">Students</nuxt-link> 
   
  <span v-if="authStore.user">
  <a href="#" @click.prevent="logout">Logout</a> 
  </span>

  <div> 
    <slot /> 
  </div> 
</template> 