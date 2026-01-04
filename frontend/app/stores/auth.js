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
        if (!apiStore.token) return
        try {
            const response = await apiStore.getAuthUser()
            currentUser.value = response.data
        } catch (error) {
            console.error('Session expired or invalid', error)
            apiStore.token = null
            currentUser.value = null
        }
    }
    return {
        currentUser,
        isLoggedIn,
        isAdmin,
        getUser,
        login,
        logout,
    }
})
