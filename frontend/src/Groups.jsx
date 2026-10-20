import { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from './api.js'
import { ListSkeleton } from './Skeleton.jsx'

export default function Groups() {
  const [groups, setGroups] = useState([])
  const [loading, setLoading] = useState(true)
  const [listError, setListError] = useState('')

  const [newName, setNewName] = useState('')
  const [createError, setCreateError] = useState('')
  const [creating, setCreating] = useState(false)

  const [inviteCode, setInviteCode] = useState('')
  const [joinError, setJoinError] = useState('')
  const [joining, setJoining] = useState(false)

  const [copiedId, setCopiedId] = useState(null)

  const loadGroups = useCallback(async () => {
    try {
      setGroups(await api('/api/groups'))
      setListError('')
    } catch (err) {
      setListError(err.message)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    loadGroups()
  }, [loadGroups])

  async function handleCreate(event) {
    event.preventDefault()
    setCreateError('')
    setCreating(true)
    try {
      await api('/api/groups', { method: 'POST', body: { name: newName } })
      setNewName('')
      await loadGroups()
    } catch (err) {
      setCreateError(err.message)
    } finally {
      setCreating(false)
    }
  }

  async function handleJoin(event) {
    event.preventDefault()
    setJoinError('')
    setJoining(true)
    try {
      await api('/api/groups/join', { method: 'POST', body: { inviteCode } })
      setInviteCode('')
      await loadGroups()
    } catch (err) {
      setJoinError(err.message)
    } finally {
      setJoining(false)
    }
  }

  async function handleLeave(group) {
    if (!window.confirm(`Leave "${group.name}"?`)) {
      return
    }
    try {
      await api(`/api/groups/${group.id}/membership`, { method: 'DELETE' })
    } catch (err) {
      setListError(err.message)
    }
    await loadGroups()
  }

  async function copyCode(group) {
    try {
      await navigator.clipboard.writeText(group.inviteCode)
      setCopiedId(group.id)
      setTimeout(() => setCopiedId(null), 1500)
    } catch {
      // the browser blocked clipboard access, nothing else to do
    }
  }

  return (
    <>
      <h1>Study groups</h1>
      <p className="muted">
        Create a group, share its invite code, and see when everyone is free. Members see
        only free times, never each other&apos;s courses.
      </p>

      <div className="split">
        <div className="side-stack">
          <form className="card" onSubmit={handleCreate}>
            <h2>Create a group</h2>
            <label>
              Group name
              <input
                value={newName}
                onChange={(e) => setNewName(e.target.value)}
                placeholder="DBMS Study"
                maxLength={100}
                required
              />
            </label>
            {createError && (
              <p className="error" role="alert">
                {createError}
              </p>
            )}
            <button type="submit" disabled={creating}>
              {creating ? 'Creating...' : 'Create group'}
            </button>
          </form>

          <form className="card" onSubmit={handleJoin}>
            <h2>Join with a code</h2>
            <label>
              Invite code
              <input
                value={inviteCode}
                onChange={(e) => setInviteCode(e.target.value.toUpperCase())}
                placeholder="ABC234"
                maxLength={12}
                required
              />
            </label>
            {joinError && (
              <p className="error" role="alert">
                {joinError}
              </p>
            )}
            <button type="submit" disabled={joining}>
              {joining ? 'Joining...' : 'Join group'}
            </button>
          </form>
        </div>

        <section aria-label="Your groups">
          {listError && (
            <p className="error" role="alert">
              {listError}
            </p>
          )}

          {loading ? (
            <ListSkeleton />
          ) : groups.length === 0 ? (
            <p className="empty">
              You are not in any groups yet. Create one, or join with a code from a friend.
            </p>
          ) : (
            <ul className="course-list">
              {groups.map((group) => (
                <li key={group.id} className="course">
                  <div>
                    <strong>{group.name}</strong>
                    <div className="muted">
                      {group.memberCount} {group.memberCount === 1 ? 'member' : 'members'}
                    </div>
                    <div className="code-row">
                      Invite code: <code>{group.inviteCode}</code>
                      <button
                        type="button"
                        className="link-button"
                        onClick={() => copyCode(group)}
                      >
                        {copiedId === group.id ? 'Copied!' : 'Copy'}
                      </button>
                    </div>
                  </div>
                  <div className="actions">
                    <Link className="button-link" to={`/groups/${group.id}`}>
                      View free time
                    </Link>
                    <button className="secondary danger" onClick={() => handleLeave(group)}>
                      Leave
                    </button>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </section>
      </div>
    </>
  )
}