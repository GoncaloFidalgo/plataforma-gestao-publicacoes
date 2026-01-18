import { defineStore } from 'pinia'

export const useReferenceStore = defineStore('references', () => {
    const apiStore = useAPIStore()

    const types = ref([])
    const areas = ref([])
    const loading = ref(false)

    // --- TYPES ---
    const fetchTypes = async () => {
        loading.value = true
        try {
            types.value = await apiStore.getTypes()
        } catch (error) {
            console.error('Error fetching types', error)
        } finally {
            loading.value = false
        }
    }

    const createType = async (name) => {
        await apiStore.createType({ name })
        await fetchTypes()
    }

    const deleteType = async (id) => {
        await apiStore.deleteType(id)
        await fetchTypes()
    }

    // --- AREAS ---
    const fetchAreas = async () => {
        loading.value = true
        try {
            areas.value = await apiStore.getAreas()
        } catch (error) {
            console.error('Error fetching areas', error)
        } finally {
            loading.value = false
        }
    }

    const createArea = async (name) => {
        await apiStore.createArea({ name })
        await fetchAreas()
    }

    const deleteArea = async (id) => {
        await apiStore.deleteArea(id)
        await fetchAreas()
    }

    return {
        types,
        areas,
        loading,
        fetchTypes,
        createType,
        deleteType,
        fetchAreas,
        createArea,
        deleteArea
    }
})