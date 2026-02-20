import { getToken } from './auth'

export async function apiFetch<T>(path: string, init?: RequestInit): Promise<T> {
  const token = getToken()
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...(init?.headers as Record<string, string>),
  }
  if (token) {
    (headers as Record<string, string>)['Authorization'] = `Bearer ${token}`
  }
  const response = await fetch(path, { ...init, headers })
  if (!response.ok) {
    throw new Error(`API error: ${response.status}`)
  }
  return response.json()
}
