import { NavLink, Outlet } from 'react-router-dom'
import { useAuth } from './AuthContext.js'
import Logo from './Logo.jsx'

export default function Layout() {
  const { user, logout } = useAuth()

  return (
    <div className="page">
      <header className="topbar">
        <div className="brand">
          <span className="wordmark">
            <Logo />
            SlotSync
          </span>
          <nav aria-label="Main">
            <NavLink to="/courses">Courses</NavLink>
            <NavLink to="/timetable">Timetable</NavLink>
            <NavLink to="/groups">Groups</NavLink>
          </nav>
        </div>
        <div className="user-area">
          <span className="muted">{user.name}</span>
          <button className="secondary" onClick={logout}>
            Log out
          </button>
        </div>
      </header>

      <Outlet />
    </div>
  )
}