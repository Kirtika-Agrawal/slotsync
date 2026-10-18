import { useAuth } from './AuthContext.js'

export default function Home() {
  const { user, logout } = useAuth()

  return (
    <main className="page">
      <header className="topbar">
        <strong>SlotSync</strong>
        <button className="secondary" onClick={logout}>
          Log out
        </button>
      </header>

      <h1>Welcome, {user.name}</h1>
      <p>Your courses and timetable will appear here.</p>
    </main>
  )
}