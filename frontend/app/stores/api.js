import {defineStore} from 'pinia'
import axios from 'axios'

export const useAPIStore = defineStore('api', () => {
    const config = useRuntimeConfig()
    const API_BASE_URL = config.public.apiBase

    const token = useCookie('auth_token', {
        maxAge: 60 * 60 * 24 * 7 // 1 week
    })

    // Para meter o header de autorização nos pedidos autenticados
    const authHeader = () => {
        return token.value ? {Authorization: `Bearer ${token.value}`} : {}
    }

    const changePassword = async (payload) => {
        // payload: { oldpassword, newpassword, confirmPassword }
        const {data} = await axios.patch(
            `${API_BASE_URL}/auth/set-password`,
            payload,
            {headers: authHeader()}
        )

        console.log('🟢 PUT /auth/set-password response:', data)

        return data   // { mensagem: "Palavra-passe alterada com sucesso" }
    }
    const createUser = async (payload) => {
        const {data} = await axios.post(
            `${API_BASE_URL}/users`,
            payload,
            {headers: authHeader()}
        )
    }
    // AUTH

    const postLogin = async (credentials) => {
        console.log('🔵 POST /auth/login sending:', credentials)

        const {data} = await axios.post(
            `${API_BASE_URL}/auth/login`,
            credentials
        )

        console.log('🟢 POST /auth/login response:', data) // <== data é a string JWT

        // 👇 guarda diretamente a string
        token.value = data

        return data
    }

    const refreshToken = async () => {
        const {data} = await axios.post(
            `${API_BASE_URL}/auth/refresh-token`,
            {token: token.value},
            {headers: authHeader()} // normalmente nem é preciso, mas não atrapalha
        )

        const newToken = typeof data === 'string' ? data : data.token

        token.value = newToken

        console.log('🟢 Token refreshed:', newToken)

        return newToken
    }

    const setUserRole = async (id, payload) => {
        const {data} = await axios.patch(`${API_BASE_URL}/users/${id}/role`, payload, {headers: authHeader()})
        return data
    }

    const updateUser = async (id, payload) => {
        const {data} = await axios.put(`${API_BASE_URL}/users/${id}`, payload, {headers: authHeader()})
        return data
    }

    const deleteUserApi = async (id) => {
        return axios.delete(`${API_BASE_URL}/users/${id}`, {headers: authHeader()})
    }


    const setUserStatus = async (id, payload) => {
        const {data} = await axios.patch(`${API_BASE_URL}/users/${id}/status`, payload, {headers: authHeader()})
        return data
    }
    const getAuthUser = async () => {
        const {data} = await axios.get(
            `${API_BASE_URL}/auth/user`,
            {headers: authHeader()}
        )

        console.log('🟢 GET /auth/user response:', data)

        return data
    }

    const getUsers = async () => {
        const config = useRuntimeConfig()
        const API_BASE_URL = config.public.apiBase

        const token = useCookie('auth_token')
        const headers = token.value
            ? {Authorization: `Bearer ${token.value}`}
            : {}

        const {data} = await axios.get(`${API_BASE_URL}/users`, {headers})

        console.log('🟢 GET /users response:', data)

        return data  // array de utilizadores
    }

    return {
        token,
        postLogin,
        getAuthUser,
        getUsers,
        createUser,
        updateUser,
        deleteUser: deleteUserApi,
        refreshToken,
        setUserStatus,
        setUserRole,
        changePassword
    }
})
