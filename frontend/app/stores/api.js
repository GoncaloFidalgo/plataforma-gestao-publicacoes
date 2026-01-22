import { defineStore } from 'pinia'
import axios from 'axios'

export const useAPIStore = defineStore('api', () => {
    const config = useRuntimeConfig()
    const API_BASE_URL = config.public.apiBase

    const token = useCookie('auth_token', {
        maxAge: 60 * 60 * 24 * 7 // 1 week
    })

    //#region Auth
    const postLogin = async (credentials) => {
        const { data } = await axios.post(`${API_BASE_URL}/auth/login`, credentials)
        token.value = data
    }
    // Para meter o header de autorização nos pedidos autenticados
    const authHeader = () => {
        return token.value ? { Authorization: `Bearer ${token.value}` } : null
    }
    //#endregion

    //#region User
    const getAuthUser = async () => {
        const headers = authHeader()
        if (!headers) return null

        const { data } = await axios.get(`${API_BASE_URL}/auth/user`, { headers })
        return data
    }
    const createUser = async (payload) => {
        const headers = authHeader()
        if (!headers) return null

        const { data } = await axios.post(`${API_BASE_URL}/users`, payload, { headers })
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
        return axios.delete(`${API_BASE_URL}/users/${id}`, { headers })
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

    const getMyPublications = async () => {
        const headers = authHeader()
        if (!headers) return null
        const { data } = await axios.get(`${API_BASE_URL}/users/me/publications`, {
            headers
        })
        return data // array de publications
    }


    const getMyComments = async () => {
        const headers = authHeader()
        if (!headers) return null
        // This calls the backend endpoint we fixed earlier
        const { data } = await axios.get(`${API_BASE_URL}/users/me/comments`, {
            headers
        })
        return data
    }


    const recoverPassword = async (email) => {
        const { data } = await axios.post(
            `${API_BASE_URL}/auth/recover-password`,
            { email }
        )
        return data // { mensagem: "..." }
    }

    const resetPassword = async (payload) => {
        // payload: { token, nova_password }  (ou camelCase se o backend exigir)
        const { data } = await axios.put(
            `${API_BASE_URL}/auth/reset-password`,
            payload
        )
        return data // { mensagem: "..." }
    }

    const updateMe = async (payload) => {
        const headers = authHeader()
        if (!headers) return null
        const { data } = await axios.put(
            `${API_BASE_URL}/users/me`,
            payload, {
            headers
        })
        return data
    }

    const changePassword = async (payload) => {
        const headers = authHeader()
        if (!headers) return null
        const { data } = await axios.patch(`${API_BASE_URL}/auth/set-password`, payload, {
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
        const { data } = await axios.get(`${API_BASE_URL}/publications`, { headers })
        return data
    }

    const getPublicationsByUser = async (username) => {
        const headers = authHeader()
        if (!headers) return []

        const { data } = await axios.get(
            `${API_BASE_URL}/users/${username}/publications`,
            { headers }
        )
        return data

    }


    const getMySubscribedTags = async () => {
        const headers = authHeader()
        if (!headers) return []
        const { data } = await axios.get(`${API_BASE_URL}/users/me/subscribed-tags`, { headers })
        return data
    }

    const subscribeTags = async (tagNames = []) => {
        const headers = authHeader()
        if (!headers) return null
        const payload = { tag_id: tagNames }
        const { data } = await axios.post(`${API_BASE_URL}/users/me/subscribed-tags`, payload, { headers })
        return data
    }

    const unsubscribeTag = async (tagName) => {
        const headers = authHeader()
        if (!headers) return null
        const { data } = await axios.delete(`${API_BASE_URL}/users/me/subscribed-tags/${encodeURIComponent(tagName)}`, { headers })
        return data
    }


    const createPublication = (formData) => {
        const headers = authHeader()
        if (!headers) throw new Error("No token")

        // Axios mete 'Content-Type': 'multipart/form-data' quando deteta um FormData
        return axios.post(`${API_BASE_URL}/publications`, formData, { headers })
    }

    const downloadPublication = (id) => {
        const headers = authHeader()
        if (!headers) throw new Error("No token")

        return axios.get(`${API_BASE_URL}/publications/${id}/ficheiro`, {
            headers,
            responseType: 'blob'
        })
    }

    const getPublicationComments = (id, hidden) => {
        const headers = authHeader()
        if (!headers) return []

        const params = {}
        if (hidden === true || hidden === false) {
            params.hidden = hidden
        }

        return axios.get(`${API_BASE_URL}/publications/${id}/comments`, { headers, params }).then(r => r.data)
    }


    //#endregion
    // --- REFERENCE DATA (Types & Areas) ---

    // TYPES
    const getTypes = () => {
        const headers = authHeader()
        if (!headers) return []
        return axios.get(`${API_BASE_URL}/references/types`, { headers }).then(r => r.data)
    }

    const createType = (data) => {
        const headers = authHeader()
        return axios.post(`${API_BASE_URL}/references/types`, data, { headers })
    }

    const deleteType = (id) => {
        const headers = authHeader()
        return axios.delete(`${API_BASE_URL}/references/types/${id}`, { headers })
    }

    // AREAS
    const getAreas = () => {
        const headers = authHeader()
        if (!headers) return []
        return axios.get(`${API_BASE_URL}/references/areas`, { headers }).then(r => r.data)
    }

    const createArea = (data) => {
        const headers = authHeader()
        return axios.post(`${API_BASE_URL}/references/areas`, data, { headers })
    }

    const deleteArea = (id) => {
        const headers = authHeader()
        return axios.delete(`${API_BASE_URL}/references/areas/${id}`, { headers })
    }

    // RATINGS
    const addRating = (pubId, rating) => {
        const headers = authHeader()
        // Send as { rating: 5 }
        return axios.post(`${API_BASE_URL}/publications/${pubId}/ratings`, { rating }, { headers })
    }

    const updateRating = (pubId, ratingId, rating) => {
        const headers = authHeader()
        return axios.patch(`${API_BASE_URL}/publications/${pubId}/ratings/${ratingId}`, { rating }, { headers })
    }

    const deleteRating = (pubId, ratingId) => {
        const headers = authHeader()
        return axios.delete(`${API_BASE_URL}/publications/${pubId}/ratings/${ratingId}`, { headers })
    }
    const getUserRating = (id) => {
        const headers = authHeader()
        if (!headers) return null
        return axios.get(`${API_BASE_URL}/publications/${id}/ratings/me`, { headers }).then(r => r.data || null)
    }

    const updateCommentVisibility = (pubId, commentId, hidden, motive) => {
        const headers = authHeader()
        return axios.patch(
            `${API_BASE_URL}/publications/${pubId}/comments/${commentId}/visibility`,
            { hidden, motive },
            { headers }
        )
    }
    const createComment = (pubId, data) => {
        const headers = authHeader()
        return axios.post(`${API_BASE_URL}/publications/${pubId}/comments`, data, { headers })
    }

    const updateComment = (pubId, commentId, data) => {
        const headers = authHeader()
        return axios.patch(`${API_BASE_URL}/publications/${pubId}/comments/${commentId}`, data, { headers })
    }

    const deleteComment = (pubId, commentId) => {
        const headers = authHeader()
        return axios.delete(`${API_BASE_URL}/publications/${pubId}/comments/${commentId}`, { headers })
    }

    const getUserSubscribedTags = async (id) => {
        const headers = authHeader()
        if (!headers) return []

        const { data } = await axios.get(`${API_BASE_URL}/users/${encodeURIComponent(id)}/subscribed-tags`, { headers })
        return data || []
    }

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
        recoverPassword,
        resetPassword,
        getMyPublications,
        getMyComments,
        getPublicationsByUser,
        updateMe,
        getMySubscribedTags, subscribeTags, unsubscribeTag, getUserSubscribedTags,
        getTypes, createType, deleteType,
        getAreas, createArea, deleteArea,
        getPublicationComments,
        addRating, updateRating, deleteRating,
        getUserRating,
        updateCommentVisibility,
        createComment, updateComment, deleteComment,
    }
})
