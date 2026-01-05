// mocks/users.ts
import type { User } from '~/types/user'

export const usersMock: User[] = [
  {
    id: 1,
    name: 'João Silva',
    username: 'joaosilva',
    password: '123',
    role: 2,
    email: 'joao.silva.2003@gmail.com',
    active: false,
    publicacoes: [],
    comentarios: [],
    ratings: [],
    tags_subscritas: []
  },
  {
    id: 2,
    name: 'Admin Master',
    username: 'admin',
    password: 'admin123',
    role: 1,
    email: 'admin@plataforma.pt',
    active: true,
    publicacoes: [],
    comentarios: [],
    ratings: [],
    tags_subscritas: []
  }
]
