<template>
    <div class="min-h-screen px-4 sm:px-6 lg:px-8 py-10 bg-gray-50 dark:bg-gray-950">
        <UCard class="max-w-4xl mx-auto shadow-xl ring-1 ring-gray-200 dark:ring-gray-800 bg-white dark:bg-gray-900">
            <template #header>
                <div class="flex items-start justify-between gap-4">
                    <div>
                        <h1 class="text-2xl font-bold text-gray-900 dark:text-white">A minha atividade</h1>
                        <p class="text-sm text-gray-500 mt-1">Histórico da tua atividade na plataforma</p>
                    </div>

                    <UButton icon="i-heroicons-arrow-path" color="primary" variant="soft"
                        :loading="activityStore.loading" @click="refresh">
                        Atualizar
                    </UButton>
                </div>
            </template>

            <div v-if="activityStore.loading" class="py-10 text-center text-gray-500">
                A carregar...
            </div>

            <div v-else>
                <UAlert v-if="activityStore.error" color="red" variant="soft" icon="i-heroicons-exclamation-triangle"
                    title="Erro ao carregar atividade" :description="errorMessage" class="mb-4" />

                <div v-if="activityStore.activity.length === 0" class="py-10 text-center text-gray-500">
                    Ainda não tens atividade registada.
                </div>

                <div v-else class="space-y-3">
                    <div v-for="(a, idx) in activityStore.activity"
                        :key="a.id ?? `${a.tipo}-${a.publicacaoId ?? ''}-${idx}`"
                        class="rounded-xl border border-gray-200 dark:border-gray-800 p-4">
                        <div class="flex items-start justify-between gap-4">
                            <div class="min-w-0">
                                <div class="flex items-center gap-2">
                                    <p class="font-semibold text-gray-900 dark:text-white">
                                        {{ titleFor(a) }}
                                    </p>
                                </div>

                                <p class="text-sm text-gray-500 mt-1">
                                    {{ a.descricao }}
                                </p>

                                <div class="flex items-center gap-3 mt-2 text-xs text-gray-400">
                                    <span v-if="a.data">📅 {{ a.data }}</span>
                                    <span v-if="a.publicacaoId">📄 Pub: #{{ a.publicacaoId }}</span>
                                    <span v-if="a.comentarioId">💬 Coment: #{{ a.comentarioId }}</span>
                                    <span v-if="a.rating != null">⭐ Rating: {{ a.rating }}</span>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </UCard>
    </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useToast } from '#imports'
import { useActivityStore } from '~/stores/activity'

const activityStore = useActivityStore()
const toast = useToast()

const refresh = async () => {
    try {
        await activityStore.fetchMyActivity()
    } catch (e) {
        toast.add({
            title: 'Erro',
            description: e?.response?.data?.mensagem || e?.response?.data || 'Não foi possível carregar a tua atividade.',
            color: 'red'
        })
    }
}

const titleFor = (a) => {
  switch (a?.tipo) {
    case 'upload': return 'Upload'
    case 'comentario': return 'Comentário'
    case 'rating': return 'Rating'
    default: return 'Atividade'
  }
}

onMounted(refresh)

const errorMessage = computed(() =>
    activityStore.error?.response?.data?.mensagem ||
    activityStore.error?.response?.data ||
    activityStore.error?.message ||
    'Ocorreu um erro.'
)

const formatDate = (iso) => {
    try {
        const d = new Date(iso)
        return d.toLocaleString('pt-PT')
    } catch {
        return iso
    }
}

const fallbackText = (a) => {
    // última rede: mostrar campos úteis ou JSON curtinho
    if (typeof a === 'string') return a
    const keys = Object.keys(a || {})
    if (!keys.length) return ''
    return keys.includes('details') ? String(a.details) : JSON.stringify(a)
}
</script>
