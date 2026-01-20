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
                <UFormField label="Email" name="email">
                    <UInput v-model="form.email" type="email" :disabled="loading" />
                </UFormField>

                <UAlert v-if="errorMessage" color="red" variant="soft" icon="i-heroicons-exclamation-circle"
                    title="Erro" :description="errorMessage" />

                <div class="flex justify-between items-center">
                    <UButton to="/login" variant="ghost" color="gray" :disabled="loading">
                        Voltar
                    </UButton>

                    <UButton type="submit" color="primary" :loading="loading" icon="i-heroicons-paper-airplane">
                        Enviar email
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


definePageMeta({
  layout: false
})
const authStore = useAuthStore()
const router = useRouter()
const toast = useToast()

const loading = ref(false)
const errorMessage = ref('')

const form = ref({ email: '' })

const schema = z.object({
    email: z.string().email('Email inválido')
})

const handleSubmit = async () => {
    errorMessage.value = ''
    loading.value = true

    try {
        const res = await authStore.recoverPassword(form.value.email)

        toast.add({
            title: 'Email enviado',
            description: res?.mensagem || 'Verifica a tua caixa de email.',
            color: 'green',
            icon: 'i-heroicons-check-circle'
        })

        // ir para a página de redefinição
        await router.push('/login/reset-password')
    } catch (e) {
        console.error(e)
        const msg = e?.response?.data?.mensagem || e?.response?.data?.message || 'Erro ao enviar email.'
        errorMessage.value = msg
    } finally {
        loading.value = false
    }
}
</script>
