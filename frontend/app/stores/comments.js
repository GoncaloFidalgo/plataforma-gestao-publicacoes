import { defineStore } from 'pinia'
import { useAPIStore } from './api.js'

export const useCommentsStore = defineStore('comments', () => {
    const apiStore = useAPIStore()
    const comments = ref([])
    const loading = ref(false)
    const error = ref(null)

    const fetchMyComments = async () => {
        loading.value = true
        error.value = null
        try {
            const data = await apiStore.getMyComments()
            comments.value = data || []
            return comments.value
        } catch (e) {
            console.error('Error fetching my comments:', e)
            error.value = e
        } finally {
            loading.value = false
        }
    }

    return {
        comments,
        loading,
        error,
        fetchMyComments
    }
})