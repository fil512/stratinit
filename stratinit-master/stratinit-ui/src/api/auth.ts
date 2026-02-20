const TOKEN_KEY = 'stratinit_jwt'

export interface LoginResponse {
  token: string
  expiresIn: number
}

export async function login(username: string, password: string): Promise<LoginResponse> {
  const response = await fetch('/stratinit/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password }),
  })
  if (!response.ok) {
    throw new Error('Invalid username or password')
  }
  const data: LoginResponse = await response.json()
  localStorage.setItem(TOKEN_KEY, data.token)
  return data
}

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

export function logout(): void {
  localStorage.removeItem(TOKEN_KEY)
}

export function isLoggedIn(): boolean {
  return getToken() !== null
}
