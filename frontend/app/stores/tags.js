import { defineStore } from 'pinia'
import axios from 'axios'

export const useTagStore = defineStore('tags', () => {
    const apiStore = useAPIStore()
    const config = useRuntimeConfig()
    const API_URL = `${config.public.apiBase}/tags`

    const tags = ref([])
    const loading = ref(false)

    const authHeader = () => ({ Authorization: `Bearer ${apiStore.token}` })

    const fetchTags = async (hidden = null) => {
        loading.value = true
        try {
            const params = hidden !== null ? { hidden } : {}
            const { data } = await axios.get(API_URL, {
                headers: authHeader(),
                params
            })
            tags.value = data
        } catch (error) {
            console.error('Failed to fetch tags', error)
            throw error
        } finally {
            loading.value = false
        }
    }

    const createTag = async (tagData) => {
        await axios.post(API_URL, tagData, { headers: authHeader() })
        await fetchTags()
    }

    const updateTag = async (originalName, tagData) => {
        await axios.put(`${API_URL}/${originalName}`, tagData, { headers: authHeader() })
        await fetchTags()
    }

    const deleteTag = async (name) => {
        await axios.delete(`${API_URL}/${name}`, { headers: authHeader() })
        await fetchTags()
    }

    return {
        tags,
        loading,
        fetchTags,
        createTag,
        updateTag,
        deleteTag
    }
})