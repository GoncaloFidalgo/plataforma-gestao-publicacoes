import {defineStore} from 'pinia'
import axios from 'axios'

export const useAPIStore = defineStore('api', () => {
    const config = useRuntimeConfig()
    const API_BASE_URL = config.public.apiBase

    const token = useCookie('auth_token', {
        maxAge: 60 * 60 * 24 * 7 // 1 week
    })

    //#region Auth
    const postLogin = async (credentials) => {
        const {data} = await axios.post(`${API_BASE_URL}/auth/login`, credentials)
        token.value = data
    }
    // Para meter o header de autorização nos pedidos autenticados
    const authHeader = () => {
        return token.value ? {Authorization: `Bearer ${token.value}`} : null
    }
//#endregion

//#region User
    const getAuthUser = async () => {
        const headers = authHeader()
        if (!headers) return null

        const { data } = await axios.get(`${API_BASE_URL}/auth/user`, { headers})
        console.log(data)
        return data
    }
    const createUser = async (payload) => {
        const headers = authHeader()
        if (!headers) return null

        const {data} = await axios.post(`${API_BASE_URL}/users`, payload, {headers})
    }
    const updateUser = async (id, payload) => {
        const headers = authHeader()
        if (!headers) return null
        const { data } = await axios.put(`${API_BASE_URL}/users/${id}`, payload, {
            headers
        })
        return data
    }
    const deleteUserApi = async (id) => {
        const headers = authHeader()
        if (!headers) return null
        return axios.delete(`${API_BASE_URL}/users/${id}`, {headers})
    }
    const getUsers = async () => {
        const headers = authHeader()
        if (!headers) return null
        const { data } = await axios.get(`${API_BASE_URL}/users`, {
            headers
        })
        return data
    }
    const setUserRole = async (id, payload) => {
        const headers = authHeader()
        if (!headers) return null
        const { data } = await axios.patch(`${API_BASE_URL}/users/${id}/role`, payload, {
            headers
        })
        return data
    }

    const changePassword = async (payload) => {
        const headers = authHeader()
        if (!headers) return null
        const { data } = await axios.patch(`${API_BASE_URL}/auth/set-password`, payload,{
            headers
        })
        return data
        // { mensagem: "Palavra-passe alterada com sucesso" }
    }
    const setUserStatus = async (id, payload) => {
        const headers = authHeader()
        if (!headers) return null
        const { data } = await axios.patch(`${API_BASE_URL}/users/${id}/status`, payload, {
            headers
        })
        return data
    }
 //#endregion

    //#region Publications
    const getPublications = async () => {
        const headers = authHeader()
        if (!headers) return []
        const { data } = await axios.get(`${API_BASE_URL}/publicacoes`, { headers })
        return data
    }


    const downloadPublication = (id) => {
        const headers = authHeader()
        if (!headers) throw new Error("No token")

        return axios.get(`${API_BASE_URL}/publicacoes/${id}/ficheiro`, {
            headers,
            responseType: 'blob'
        })
    }

    //#endregion


    return {
        token,
        postLogin,
        getAuthUser,
        getUsers,
        createUser,
        updateUser,
        deleteUser: deleteUserApi,
        setUserStatus,
        setUserRole,
        changePassword,
        getPublications,
        downloadPublication,
    }
})
