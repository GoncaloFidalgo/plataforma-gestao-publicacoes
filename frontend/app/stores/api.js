import { defineStore } from 'pinia'
import axios from 'axios'
// Note: 'ref', 'inject', 'useRuntimeConfig', and 'useCookie' are auto-imported in Nuxt

export const useAPIStore = defineStore('api', () => {
    const config = useRuntimeConfig()
    const API_BASE_URL = config.public.apiBase

    const token = useCookie('auth_token', {
        maxAge: 60 * 60 * 24 * 7 // 1 week
    })

    // Para meter o header de autorização nos pedidos autenticados
    const authHeader = () => {
        return token.value ? { Authorization: `Bearer ${token.value}` } : {}
    }

    // AUTH
    const postLogin = async (credentials) => {
        const response = await axios.post(`${API_BASE_URL}/auth/login`, credentials)
        token.value = response.data
    }

    // Users
    const getAuthUser = () => {
        return axios.get(`${API_BASE_URL}/auth/user`, {
            headers: authHeader()
        })
    }

    return {
        token,
        postLogin,
        getAuthUser,
    }
})