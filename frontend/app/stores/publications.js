import { defineStore } from 'pinia'

export const usePublicationStore = defineStore('publications', () => {
    const apiStore = useAPIStore()
    const publications = ref([])
    const loading = ref(false)

    // Fetch List
    const fetchPublications = async () => {
        loading.value = true
        try {
            const { data } = await apiStore.getPublications()
            publications.value = data
        } catch (error) {
            console.error('Error fetching publications:', error)
        } finally {
            loading.value = false
        }
    }

    // Download File Logic
    const downloadFile = async (id, title, type) => {
        try {
            const response = await apiStore.downloadPublication(id)
            console.log(response)
            console.log(type)
            // 1. Extract filename from Content-Disposition header if available
            // fallback to Title + extension if header parsing fails
            let fileName = `${title.replace(/[^a-z0-9]/gi, '_').toLowerCase()}.${type}`

            const disposition = response.headers['content-disposition']
            if (disposition && disposition.indexOf('attachment') !== -1) {
                const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                const matches = filenameRegex.exec(disposition);
                if (matches != null && matches[1]) {
                    fileName = matches[1].replace(/['"]/g, '');
                }
            }

            // 2. Create a virtual Blob link and click it
            const url = window.URL.createObjectURL(new Blob([response.data]))
            const link = document.createElement('a')
            link.href = url
            link.setAttribute('download', fileName)
            document.body.appendChild(link)
            link.click()

            // 3. Cleanup
            link.remove()
            window.URL.revokeObjectURL(url)

        } catch (error) {
            console.error('Download failed:', error)
            throw error // Re-throw so UI can show a toast
        }
    }

    return {
        publications,
        loading,
        fetchPublications,
        downloadFile
    }
})