const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

export class ApiError extends Error {
  constructor(status, message, fieldErrors = {}) {
    super(message)
    this.status = status
    this.fieldErrors = fieldErrors
  }
}

export function getToken() {
  return localStorage.getItem('token')
}

export function saveToken(token) {
  localStorage.setItem('token', token)
}

export function clearToken() {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
}

export async function api(path, { method = 'GET', body } = {}) {
  const headers = { 'Content-Type': 'application/json' }
  const token = getToken()
  if (token) {
    headers.Authorization = `Bearer ${token}`
  }

  let response
  try {
    response = await fetch(`${API_URL}${path}`, {
      method,
      headers,
      body: body ? JSON.stringify(body) : undefined,
    })
  } catch {
    throw new ApiError(0, 'Cannot reach the server. Is the backend running?')
  }

  // An expired or invalid token: log the user out and go to the login page
  if (response.status === 401 && token && !path.startsWith('/api/auth/')) {
    clearToken()
    window.location.assign('/login')
  }

  if (response.status === 204) {
    return null
  }

  let data = null
  try {
    data = await response.json()
  } catch {
    // the response had no body
  }

  if (!response.ok) {
    throw new ApiError(
      response.status,
      data?.message || 'Something went wrong. Please try again.',
      data?.fieldErrors || {},
    )
  }

  return data
}