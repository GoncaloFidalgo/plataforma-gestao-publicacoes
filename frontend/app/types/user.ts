// types/user.ts
export interface User {
  id: number
  name: string
  username: string
  password: string
  role: number
  email: string
  active: boolean
  publicacoes: any[]
  comentarios: any[]
  ratings: any[]
  tags_subscritas: any[]
}
