import {useAuthStore} from "~/stores/auth.js";
import {useAPIStore} from "~/stores/api.js";

export default defineNuxtRouteMiddleware(async (to, from) => {
    const authStore = useAuthStore()
    const apiStore = useAPIStore()

    // 1. Hydration Check:
    // If we have a Token (cookie) but no User (memory), fetch the user.
    // We use 'await' so the page does NOT load until this finishes.
    if (apiStore.token && !authStore.currentUser) {
        await authStore.getUser()
    }

    // 2. Protected Routes Logic:
    // If user is NOT logged in and tries to go strictly anywhere BUT login
    if (!authStore.isLoggedIn && to.path !== '/login') {
        return navigateTo('/login')
    }

    // 3. Redirect Loop Prevention:
    // If user IS logged in and tries to go to login, send them to dashboard/home
    if (authStore.isLoggedIn && to.path === '/login') {
        return navigateTo('/')
    }
})