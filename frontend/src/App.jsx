import { Navigate, Route, Routes } from 'react-router-dom'
import { useAuth } from './AuthContext.js'
import AuthPage from './AuthPage.jsx'
import Courses from './Courses.jsx'
import Layout from './Layout.jsx'

function RequireAuth({ children }) {
  const { user } = useAuth()
  return user ? children : <Navigate to="/login" replace />
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<AuthPage mode="login" />} />
      <Route path="/signup" element={<AuthPage mode="signup" />} />
      <Route
        element={
          <RequireAuth>
            <Layout />
          </RequireAuth>
        }
      >
        <Route path="/courses" element={<Courses />} />
      </Route>
      <Route path="*" element={<Navigate to="/courses" replace />} />
    </Routes>
  )
}