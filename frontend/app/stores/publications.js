import { defineStore } from 'pinia'

export const usePublicationStore = defineStore('publications', () => {
    const apiStore = useAPIStore()
    const publications = ref([])
    const loading = ref(false)

    // Fetch List
    const fetchPublications = async () => {
        loading.value = true
        try {
            const data = await apiStore.getPublications()
            publications.value = data || []

        } catch (error) {
            console.error('Error fetching publications:', error)
        } finally {
            loading.value = false
        }
    }

    const fetchPublicationsByUser = async (username) => {
        loading.value = true
        try {
            const data = await apiStore.getPublicationsByUser(username)
            publications.value = data || []
                        console.log("data",publications.value)
            return publications.value
        } catch (error) {
            console.error('Error fetching user publications:', error)
            throw error
        } finally {
            loading.value = false
        }
    }



    const fetchMyPublications = async () => {
        loading.value = true
        error.value = null
        try {
            const data = await apiStore.getMyPublications()
            publications.value = Array.isArray(data) ? data : []
            console.log(data)
            console.log(publications.value)
            return publications.value
        } catch (e) {
            error.value = e
            throw e
        } finally {
            loading.value = false
        }
    }
    // Download File Logic
    const downloadFile = async (id, title, type) => {
        try {
            const response = await apiStore.downloadPublication(id)

            // Criar nome do ficheiro a partir do titulo da publicação e tipo de ficheiro
            let fileName = `${title.replace(/[^a-z0-9]/gi, '_').toLowerCase()}.${type}`

            // Extrarir o titulo do header se tiver
            const disposition = response.headers['content-disposition']
            if (disposition && disposition.indexOf('attachment') !== -1) {
                const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                const matches = filenameRegex.exec(disposition);
                if (matches != null && matches[1]) {
                    fileName = matches[1].replace(/['"]/g, '');
                }
            }

            // Criar um link com um BLOB para baixar o ficheiro
            // Criar um elemento temporario para clicar automaticamente e baixar o ficheiro
            const url = window.URL.createObjectURL(new Blob([response.data]))
            const link = document.createElement('a')
            link.href = url
            link.setAttribute('download', fileName)
            document.body.appendChild(link)
            link.click()

            link.remove()
            window.URL.revokeObjectURL(url)

        } catch (error) {
            console.error('Download failed:', error)
            throw error
        }
    }
    const create = async (form) => {
        const formData = new FormData()

        // 1. Construct Metadata JSON
        const metadata = {
            titulo: form.titulo,
            tipo: form.tipo,
            area_cientifica: form.areaCientifica,
            descricao: form.descricao,
            // Convert comma-separated string to array if used, or pass array directly
            autores: Array.isArray(form.autores) ? form.autores : [],
            tags: form.tags, // Array of tag names
            hidden: form.hidden
        }

        formData.append('metadata', JSON.stringify(metadata))

        // 2. Append File
        if (form.file) {
            formData.append('file', form.file)
        }

        await apiStore.createPublication(formData)
        // Refresh list
        await fetchPublications()
    }
    return {
        publications,
        loading,
        fetchPublications,
        downloadFile,
        create,
        fetchMyPublications,
        fetchPublicationsByUser
    }
})