import { defineStore } from 'pinia'
import axios from 'axios'

export const useTagStore = defineStore('tags', () => {
  const apiStore = useAPIStore()
  const config = useRuntimeConfig()
  const API_URL = `${config.public.apiBase}/tags`
  const USERS_URL = `${config.public.apiBase}/users`
  const subscribedTagsOfUser = ref([])
  const loadingSubscribedTags = ref(false)
  const tags = ref([])
  const mySubscribedTags = ref([]) // EP28 result (TagDTO[])
  const loading = ref(false)

  const authHeader = () => ({ Authorization: `Bearer ${apiStore.token}` })

  const subscribedSet = computed(() =>
    new Set((mySubscribedTags.value || []).map(t => t.name))
  )

  const normalize = (s) => (s ?? '').toString().trim().toLowerCase()

  const isSubscribed = (tagName) => {
    const key = normalize(tagName)
    return mySubscribedTags.value.some(t => normalize(t?.name) === key)
  }


  // ------- existentes -------
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
    await axios.put(`${API_URL}/${encodeURIComponent(originalName)}`, tagData, { headers: authHeader() })
    await fetchTags()
  }

  const deleteTag = async (name) => {
    await axios.delete(`${API_URL}/${encodeURIComponent(name)}`, { headers: authHeader() })
    await fetchTags()
  }


  const fetchMySubscribedTags = async () => {
    const { data } = await axios.get(`${USERS_URL}/me/subscribed-tags`, {
      headers: authHeader()
    })
    mySubscribedTags.value = Array.isArray(data) ? data : []
    return mySubscribedTags.value
  }



  const subscribeTag = async (tagName) => {

    await axios.post(
      `${USERS_URL}/me/subscribed-tags`,
      { tag_id: [tagName] },
      { headers: authHeader() }
    )
    await fetchMySubscribedTags()
    await fetchTags()
  }



  const fetchSubscribedTagsOfUser = async (id) => {
    loadingSubscribedTags.value = true
    try {
      subscribedTagsOfUser.value = await apiStore.getUserSubscribedTags(id)
      return subscribedTagsOfUser.value
    } finally {
      loadingSubscribedTags.value = false
    }
  }

  const unsubscribeTag = async (tagName) => {

    await axios.delete(
      `${USERS_URL}/me/subscribed-tags/${encodeURIComponent(tagName)}`,
      { headers: authHeader() }
    )
    await fetchMySubscribedTags()
    await fetchTags()
  }


  const refreshAll = async (hidden = null) => {
    loading.value = true
    try {
      await Promise.all([fetchTags(hidden), fetchMySubscribedTags()])
    } finally {
      loading.value = false
    }
  }

  return {
    tags,
    mySubscribedTags,
    subscribedSet,
    isSubscribed,
    loading,

    fetchTags,
    createTag,
    updateTag,
    deleteTag,

    fetchMySubscribedTags,
    subscribeTag,
    unsubscribeTag,
    refreshAll,

    subscribedTagsOfUser,
    loadingSubscribedTags,
    fetchSubscribedTagsOfUser
  }
})
