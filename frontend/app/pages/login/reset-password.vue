<template>
    <div class="login-page">
        <UCard class="login-card">

            <template #header>
                <div class="flex flex-col items-center gap-2">
                    <h1 class="login-title">
                        Recuperar palavra-passe
                    </h1>
                    <p class="login-subtitle">
                        Introduz o teu e-mail e vamos enviar um código/token.
                    </p>
                </div>
            </template>
            <UForm :schema="schema" :state="form" @submit="handleSubmit" class="space-y-5">
                <UFormField label="Token / Código" name="token">
                    <UInput v-model="form.token" :disabled="loading" />
                </UFormField>

                <UFormField label="Nova palavra-passe" name="newPassword">
                    <UInput v-model="form.newPassword" type="password" :disabled="loading" />
                </UFormField>

                <UFormField label="Confirmar nova palavra-passe" name="confirmPassword">
                    <UInput v-model="form.confirmPassword" type="password" :disabled="loading" />
                </UFormField>

                <UAlert v-if="errorMessage" color="red" variant="soft" icon="i-heroicons-exclamation-circle"
                    title="Erro" :description="errorMessage" />

                <div class="flex justify-between items-center">
                    <UButton to="/auth/forgot-password" variant="ghost" color="gray" :disabled="loading">
                        Voltar
                    </UButton>

                    <UButton type="submit" color="primary" :loading="loading" icon="i-heroicons-key">
                        Alterar palavra-passe
                    </UButton>
                </div>
            </UForm>
        </UCard>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { z } from 'zod'
import { useRouter } from 'vue-router'
import { useAuthStore } from '~/stores/auth'
import { useToast } from '#imports'

const authStore = useAuthStore()
const router = useRouter()
const toast = useToast()

const loading = ref(false)
const errorMessage = ref('')

const form = ref({
    token: '',
    newPassword: '',
    confirmPassword: ''
})


definePageMeta({
    layout: false
})

const schema = z.object({
    token: z.string().min(1, 'Token é obrigatório'),
    newPassword: z.string().min(6, 'Mínimo 6 caracteres'),
    confirmPassword: z.string().min(1, 'Confirma a palavra-passe')
}).refine(d => d.newPassword === d.confirmPassword, {
    message: 'As palavras-passe não coincidem',
    path: ['confirmPassword']
})

const handleSubmit = async () => {
    errorMessage.value = ''
    loading.value = true

    try {
        // payload conforme enunciado (pode precisar camelCase)
        const payload = {
            token: form.value.token,
            nova_password: form.value.newPassword
        }

        const res = await authStore.resetPassword(payload)

        toast.add({
            title: 'Password alterada',
            description: res?.mensagem || 'Palavra-passe redefinida com sucesso.',
            color: 'green',
            icon: 'i-heroicons-check-circle'
        })

        await router.push('/login')
    } catch (e) {
        console.error(e)
        const msg = e?.response?.data?.mensagem || e?.response?.data?.message || 'Erro ao redefinir password.'
        errorMessage.value = msg
    } finally {
        loading.value = false
    }
}
</script>
