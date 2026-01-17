import {useAuthStore} from "~/stores/auth.js";
import {useAPIStore} from "~/stores/api.js";
//
export default defineNuxtRouteMiddleware(async (to, from) => {
    const authStore = useAuthStore()
    const apiStore = useAPIStore()

    // 1. Hydration Check:
    // Se houver token mas não houver user, vai se buscar o
    if (apiStore.token && !authStore.currentUser) {
        // Usar await para a esperar até ter os dados do user, ou seja, a página nao carrega até obter os dados e continuar a execução.
        await authStore.getUser()
    }

    // 2. Protected Routes Logic:
    // Proteger contra users sem autenticação a tentar aceder a qualquer rota que não seja login
    if (!authStore.isLoggedIn && to.path !== '/login') {
        return navigateTo('/login')
    }

    // 3. Redirect Loop Prevention:
    // Se o user já tiver login e tentar fazer login, enviar para a homepage
    if (authStore.isLoggedIn && to.path === '/login') {
        return navigateTo('/')
    }
})