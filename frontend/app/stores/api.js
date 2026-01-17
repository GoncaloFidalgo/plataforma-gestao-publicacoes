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
        return token.value ? {Authorization: `Bearer ${token.value}`} : {}
    }
//#endregion

//#region User
    const getAuthUser = async () => {
        const { data } = await axios.get(`${API_BASE_URL}/auth/user`, {
            headers: authHeader()
        })
        return data
    }
    const createUser = async (payload) => {
        const {data} = await axios.post(`${API_BASE_URL}/users`, payload, {headers: authHeader()})
    }
    const updateUser = async (id, payload) => {
        const { data } = await axios.put(`${API_BASE_URL}/users/${id}`, payload, {
            headers: authHeader()
        })
        return data
    }
    const deleteUserApi = async (id) => {
        return axios.delete(`${API_BASE_URL}/users/${id}`, {headers: authHeader()})
    }
    const getUsers = async () => {
        const { data } = await axios.get(`${API_BASE_URL}/users`, {
            headers: authHeader()
        })
        return data
    }
    const setUserRole = async (id, payload) => {
        const { data } = await axios.patch(`${API_BASE_URL}/users/${id}/role`, payload, {
            headers: authHeader()
        })
        return data
    }

    const changePassword = async (payload) => {
        // payload: { oldpassword, newpassword, confirmPassword }
        const { data } = await axios.patch(`${API_BASE_URL}/auth/set-password`, payload,{
            headers: authHeader()
        })
        return data
        // { mensagem: "Palavra-passe alterada com sucesso" }
    }
    const setUserStatus = async (id, payload) => {
        const { data } = await axios.patch(`${API_BASE_URL}/users/${id}/status`, payload, {
            headers: authHeader()
        })
        return data
    }
 //#endregion

    //#region Publications
    const getPublications = () => {
        return axios.get(`${API_BASE_URL}/publications`, {
            headers: authHeader()
        })
    }

    const downloadPublication = (id) => {
        return axios.get(`${API_BASE_URL}/publications/${id}/ficheiro`, {
            headers: authHeader(),
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
