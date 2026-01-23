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
            tipoId: form.tipo,
            areaId: form.areaCientifica,
            descricao: form.descricao,
            autores: Array.isArray(form.autores) ? form.autores : [],
            tags: form.tags,
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
    const fetchComments = async (id, hidden) => apiStore.getPublicationComments(id, hidden)
    const addRating = async (pubId, value) => {
        const { data } = await apiStore.addRating(pubId, value)
        return data
    }

    const updateRating = async (pubId, ratingId, value) => {
        const { data } = await apiStore.updateRating(pubId, ratingId, value)
        return data
    }

    const deleteRating = async (pubId, ratingId) => {
        const { data } = await apiStore.deleteRating(pubId, ratingId)
        return data
    }
    const fetchUserRating = async (id) => apiStore.getUserRating(id)
    const toggleCommentVisibility = async (pubId, commentId, currentHidden, motive, currentFilter) => {
        const newHidden = !currentHidden
        await apiStore.updateCommentVisibility(pubId, commentId, newHidden, motive)
        return await fetchComments(pubId, currentFilter)
    }

    const togglePublicationVisibility = async (id, currentHidden) => {
        await apiStore.updatePublicationVisibility(id, !currentHidden)
        await fetchPublications()
    }
    const update = async (id, form) => {
        const formData = new FormData()

        // 1. Metadata
        const metadata = {
            titulo: form.titulo,
            tipoId: form.tipo, // ID
            areaId: form.areaCientifica, // ID
            descricao: form.descricao,
            tags: form.tags,
            hidden: form.hidden,
            autores: Array.isArray(form.autores) ? form.autores : [],
        }
        formData.append('metadata', JSON.stringify(metadata))

        if (form.file) {
            formData.append('file', form.file)
        }

        console.log('Updating publication...')
        await apiStore.updatePublication(id, formData)

        console.log('Fetching publications...')
        await fetchPublications()

        //console.log('Fetching my publications...')
        //await fetchMyPublications()
    }
    return {
        publications,
        loading,
        fetchPublications,
        downloadFile,
        create,
        fetchComments,
        addRating, updateRating, deleteRating,fetchUserRating,
        toggleCommentVisibility,
        fetchMyPublications,
        fetchPublicationsByUser,
        togglePublicationVisibility,
        update
    }
})