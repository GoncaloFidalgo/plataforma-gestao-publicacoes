import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useAPIStore } from '~/stores/api'

export const useUsersStore = defineStore('users', () => {
  const apiStore = useAPIStore()

  const users = ref([])
  const loading = ref(false)
  const error = ref(null)

  const allUsers = computed(() => users.value)

  /* =========================
     EP12 – GET /users
  ========================== */
  const fetchUsers = async () => {
    loading.value = true
    error.value = null

    try {
      const data = await apiStore.getUsers()
      users.value = Array.isArray(data) ? data : []
      return users.value
    } catch (err) {
      console.error('Erro ao buscar utilizadores:', err.response?.data || err)
      error.value = err
      throw err
    } finally {
      loading.value = false
    }
  }

  /* =========================
     EP13 – GET /users/{id}
  ========================== */
  const fetchUserById = async (id) => {
    try {
      const data = await apiStore.getUser(id)
      return data
    } catch (err) {
      console.error('Erro ao buscar utilizador:', err.response?.data || err)
      throw err
    }
  }

  /* =========================
     EP07 – POST /users
  ========================== */
  const createUser = async (payload) => {
    try {
      const data = await apiStore.createUser(payload)

      if (data) {
        users.value.push(data)
      } else {
        await fetchUsers()
      }

      return data
    } catch (err) {
      console.error('Erro ao criar utilizador:', err.response?.data || err)
      throw err
    }
  }

  const findByUsername = (username) => {
  if (!username) return null
  return users.value.find(u => u.username === username) || null
  }
  /* =========================
     EP08 – PUT /users/{id}
  ========================== */
  const updateUser = async (id, payload) => {
    try {
      const data = await apiStore.updateUser(id, payload)

      
      const idx = users.value.findIndex(u => u.username === id)
      
      if (idx !== -1) users.value[idx] = data

      return data
    } catch (err) {
      console.error('Erro ao editar utilizador:', err.response?.data || err)
      throw err
    }
  }

  /* =========================
     EP09 – DELETE /users/{id}
  ========================== */
  const deleteUser = async (id) => {
    try {
      await apiStore.deleteUser(id)
      users.value = users.value.filter(u => u.username !== id)
    } catch (err) {
      console.error('Erro ao remover utilizador:', err.response?.data || err)
      throw err
    }
  }

  /* =========================
     EP10 – PATCH /users/{id}/status
  ========================== */
  const setUserStatus = async (id, active, motive = '') => {
    try {
      const data = await apiStore.setUserStatus(id, { active, motive })

      const idx = users.value.findIndex(u => u.id === id)
      if (idx !== -1) users.value[idx].active = data.active

      return data
    } catch (err) {
      console.error('Erro ao alterar estado:', err.response?.data || err)
      throw err
    }
  }

  /* =========================
     EP11 – PATCH /users/{id}/role
  ========================== */
  const setUserRole = async (id, role) => {
    try {
      const data = await apiStore.setUserRole(id, { role })

      const idx = users.value.findIndex(u => u.id === id)
      if (idx !== -1) users.value[idx].role = data.role

      return data
    } catch (err) {
      console.error('Erro ao alterar role:', err.response?.data || err)
      throw err
    }
  }

  const updateMe = async (payload) => {
    const data = await apiStore.updateMe(payload)
    if (data) {
        users.value.push(data)
      } else {
        await fetchAuthenticatedUser()
      }

    return data
  }

  const fetchAuthenticatedUser = async () => {
    const data = await apiStore.getAuthUser() // GET /auth/user
    return data
  }
  return {
    users,
    loading,
    error,

    allUsers,

    fetchUsers,
    fetchUserById,
    createUser,
    updateUser,
    deleteUser,
    setUserStatus,
    findByUsername,
    setUserRole,
    fetchAuthenticatedUser,
    updateMe,
  }
})
