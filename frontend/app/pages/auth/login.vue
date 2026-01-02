<template>
  <div class="login-container">
    <div class="login-card">
      <h1>Login to DAE</h1>
      
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username">Username</label>
          <input
            id="username"
            v-model="credentials.username"
            type="text"
            required
            placeholder="Enter your username"
            autocomplete="username"
          />
        </div>
        
        <div class="form-group">
          <label for="password">Password</label>
          <input
            id="password"
            v-model="credentials.password"
            type="password"
            required
            placeholder="Enter your password"
            autocomplete="current-password"
          />
        </div>
        
        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>
        
        <button type="submit" class="btn-submit" :disabled="loading">
          {{ loading ? 'Logging in...' : 'Login' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '~/stores/auth-store'

const authStore = useAuthStore()
const router = useRouter()

const credentials = ref({
  username: '',
  password: '',
})

const errorMessage = ref('')
const loading = ref(false)

const handleLogin = async () => {
  errorMessage.value = ''
  loading.value = true
  
  try {
    const result = await authStore.login(credentials.value)
    
    if (result.success) {
      // Redirect based on user role
      const redirectPath = getRedirectPath(result.role)
      await router.push(redirectPath)
    } else {
      errorMessage.value = 'Invalid username or password. Please try again.'
    }
  } catch (error) {
    errorMessage.value = 'An error occurred during login. Please try again.'
    console.error('Login error:', error)
  } finally {
    loading.value = false
  }
}

const getRedirectPath = (role) => {
  const roleRedirects = {
    'ADMINISTRATOR': '/administrators',
    'TEACHER': '/teachers',
    'STUDENT': '/students',
  }
  
  return roleRedirects[role] || '/'
}

// Redirect if already logged in
onMounted(() => {
  if (authStore.isLoggedIn) {
    const redirectPath = getRedirectPath(authStore.userRole)
    router.push(redirectPath)
  }
})
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 200px);
  padding: 2rem;
}

.login-card {
  background: white;
  padding: 3rem;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 450px;
}

.login-card h1 {
  text-align: center;
  margin-bottom: 2rem;
  color: #2c3e50;
  font-size: 2rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #2c3e50;
  font-weight: 600;
  font-size: 0.95rem;
}

.form-group input {
  width: 100%;
  padding: 0.875rem;
  border: 2px solid #e0e0e0;
  border-radius: 6px;
  font-size: 1rem;
  box-sizing: border-box;
  transition: border-color 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #3498db;
}

.error-message {
  background-color: #fee;
  color: #c33;
  padding: 0.875rem;
  border-radius: 6px;
  margin-bottom: 1.5rem;
  text-align: center;
  border: 1px solid #fcc;
  font-size: 0.95rem;
}

.btn-submit {
  width: 100%;
  padding: 0.875rem;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn-submit:hover:not(:disabled) {
  background-color: #2980b9;
}

.btn-submit:disabled {
  background-color: #95a5a6;
  cursor: not-allowed;
  opacity: 0.7;
}
</style>