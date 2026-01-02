// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  runtimeConfig: { 
    public: { 
      apiBase: 'http://localhost:8080/academics/api' 
    } 
  },

  // Register only the Nuxt Pinia module. Do not list "pinia" here — it's a library, not a Nuxt module.
  modules: ['@pinia/nuxt']
})