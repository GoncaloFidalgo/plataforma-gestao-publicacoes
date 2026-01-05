// stores/auth.js
import { defineStore } from 'pinia'
import { useAPIStore } from '~/stores/api.js'

export const useAuthStore = defineStore('auth', () => {
  const apiStore = useAPIStore()

  const currentUser = ref(null)

  const isLoggedIn = computed(() => !!currentUser.value)

  const isAdmin = computed(() => {
    if (!currentUser.value) return false

    if (currentUser.value.roleType) {
      return currentUser.value.roleType.toLowerCase() === 'administrator'
    }
    if (typeof currentUser.value.role === 'number') {
      return currentUser.value.role === 1
    }
    return false
  })

  const login = async (credentials) => {
    console.log('🟡 authStore.login() called with:', credentials)

    try {
      await apiStore.postLogin(credentials)
      const user = await getUser()
    } catch (error) {
      throw error
    }
  }

  const getUser = async () => {
    if (!apiStore.token) {
      console.warn('getUser: no token, skipping')
      return null
    }

    try {
      const user = await apiStore.getAuthUser()
      currentUser.value = user        // se API devolver { id, ... }
      return user
    } catch (error) {
      console.error('Session expired or invalid', error.response?.data || error)
      apiStore.token = null
      currentUser.value = null
      throw error
    }
  }

const changePassword = async (payload) => {
  try {
    const result = await apiStore.changePassword(payload)
    // aqui result.mensagem deve ser a string de sucesso da API
    return result
  } catch (error) {
    console.error('Erro ao alterar password:', error.response?.data || error)
    throw error
  }
}

  const logout = async () => {
    apiStore.token = null
    currentUser.value = null

    const cookie = useCookie('auth_token')
    cookie.value = null

    if (process.client) {
      await navigateTo('/login')
    }
  }

  return {
    currentUser,
    isLoggedIn,
    isAdmin,
    login,
    logout,
    getUser,
    changePassword
  }
})