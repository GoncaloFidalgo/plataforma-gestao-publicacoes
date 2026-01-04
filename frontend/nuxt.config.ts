export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },

  runtimeConfig: {
    public: {
      apiBase: 'http://localhost:8080/academics/api'
    }
  },

  modules: ['@pinia/nuxt', '@nuxt/ui'],

  colorMode: {
    preference: 'dark', 
    fallback: 'dark',    
    classSuffix: ''      
  },

  css: [
    '~/assets/css/main.css'
  ]
})
