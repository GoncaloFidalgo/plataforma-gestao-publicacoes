import { defineStore } from 'pinia'
import {useAPIStore} from "~/stores/api.js";

export const useAuthStore = defineStore('auth', () => {
    const apiStore = useAPIStore()

    const currentUser = ref(null)

    const isAdmin = computed(() => isLoggedIn && currentUser.value.roleType === "Administrator")
    const isLoggedIn = computed(() => !!currentUser.value)

    const login = async (credentials) => {
        await apiStore.postLogin(credentials)
        await getUser()
    }

    const logout = async () => {
        apiStore.token = null
        currentUser.value = null
        navigateTo('/login')
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
