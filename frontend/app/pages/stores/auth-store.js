import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: null,
  }),
  
  getters: {
    isLoggedIn: (state) => !!state.token && !!state.user,
    currentUser: (state) => state.user,
    userName: (state) => state.user?.name || state.user?.username || 'Guest',
    userRole: (state) => state.user?.type || null,
  },
  
  actions: {
    async login(credentials) {
      try {
        // Replace with your actual API endpoint
        const response = await $fetch('http://localhost:8080/api/auth/login', {
          method: 'POST',
          body: credentials,
        })
        
        this.token = response.token
        this.user = response.user
        
        // Save to localStorage for persistence
        if (process.client) {
          localStorage.setItem('token', this.token)
          localStorage.setItem('user', JSON.stringify(this.user))
        }
        
        return { success: true, role: this.user.type }
      } catch (error) {
        console.error('Login failed:', error)
        return { success: false, error }
      }
    },
    
    logout() {
      this.user = null
      this.token = null
      
      if (process.client) {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
      }
    },
    
    restoreAuth() {
      if (process.client) {
        const token = localStorage.getItem('token')
        const user = localStorage.getItem('user')
        
        if (token && user) {
          this.token = token
          this.user = JSON.parse(user)
        }
      }
    },
  },
})