import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useAPIStore } from '~/stores/api'

export const useActivityStore = defineStore('activity', () => {
  const apiStore = useAPIStore()

  const activity = ref([])
  const loading = ref(false)
  const error = ref(null)

  const fetchMyActivity = async () => {
    loading.value = true
    error.value = null
    try {
      const data = await apiStore.getMyActivity()
      activity.value = Array.isArray(data) ? data : []
      return activity.value
    } catch (e) {
      error.value = e
      throw e
    } finally {
      loading.value = false
    }
  }

const userActivity = ref([])
const loadingUserActivity = ref(false)

const fetchUserActivity = async (id) => {
  loadingUserActivity.value = true
  try {
    const data = await apiStore.getUserActivity(id)
    userActivity.value = Array.isArray(data) ? data : []
    return userActivity.value
  } finally {
    loadingUserActivity.value = false
  }
}

  return {
    activity,
    loading,
    error,
    fetchMyActivity,
    userActivity,
    loadingUserActivity,
    fetchUserActivity
  }
})
