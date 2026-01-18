// stores/auth.js
import {defineStore} from 'pinia'
import {useAPIStore} from '~/stores/api.js'

export const useAuthStore = defineStore('auth', () => {
    const apiStore = useAPIStore()

    const currentUser = ref(null)

    const isLoggedIn = computed(() => !!currentUser.value)

    const isAdmin = computed(() => {
        const user = currentUser.value
        if (!user) return false
        return user.role === 1 || user.roleType.toLowerCase() === 'administrator'
    })
    const isResponsavel = computed(() => {
        const user = currentUser.value
        if (!user) return false
        return user.role === 2 || user.roleType.toLowerCase() === 'responsavel'
    })
    const isColaborador = computed(() => {
        const user = currentUser.value
        if (!user) return false
        return user.role === 3 || user.roleType.toLowerCase() === 'Colaborador'
    })

    const login = async (credentials) => {
        await apiStore.postLogin(credentials)
        await authUser()
    }

    const authUser = async () => {
        try {
            currentUser.value = await apiStore.getAuthUser()
        } catch (error) {
           // console.error('Session expired or invalid', error.response?.data || error)
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
        isResponsavel,
        isColaborador,
        login,
        logout,
        authUser,
        changePassword
    }
})