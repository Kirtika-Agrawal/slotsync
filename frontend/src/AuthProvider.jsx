import { useState } from 'react'
import { AuthContext } from './AuthContext.js'
import { api, clearToken, getToken, saveToken } from './api.js'

function loadSavedUser() {
  try {
    return JSON.parse(localStorage.getItem('user'))
  } catch {
    return null
  }
}

export default function AuthProvider({ children }) {
  const [user, setUser] = useState(() => (getToken() ? loadSavedUser() : null))

  async function login(email, password) {
    const data = await api('/api/auth/login', {
      method: 'POST',
      body: { email, password },
    })
    saveToken(data.token)
    localStorage.setItem('user', JSON.stringify(data.user))
    setUser(data.user)
  }

  async function signup(name, email, password) {
    await api('/api/auth/signup', {
      method: 'POST',
      body: { name, email, password },
    })
    await login(email, password)
  }

  function logout() {
    clearToken()
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, login, signup, logout }}>
      {children}
    </AuthContext.Provider>
  )
}