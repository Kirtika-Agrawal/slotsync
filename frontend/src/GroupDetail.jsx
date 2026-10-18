import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { api } from './api.js'
import { formatDay, formatTime, toMinutes } from './format.js'
import WeekGrid from './WeekGrid.jsx'

const MIN_OPTIONS = [30, 45, 60, 90, 120]

// Finds the single longest shared gap in the whole week
function findBestWindow(days) {
  let best = null
  for (const day of days) {
    for (const window of day.freeWindows) {
      const minutes = toMinutes(window.end) - toMinutes(window.start)
      if (!best || minutes > best.minutes) {
        best = { day: day.day, start: window.start, end: window.end, minutes }
      }
    }
  }
  return best
}

export default function GroupDetail() {
  const { id } = useParams()
  const [minMinutes, setMinMinutes] = useState(60)
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let cancelled = false

    api(`/api/groups/${id}/free-time?minMinutes=${minMinutes}`)
      .then((result) => {
        if (!cancelled) {
          setData(result)
          setError('')
        }
      })
      .catch((err) => {
        if (!cancelled) {
          setError(err.message)
        }
      })
      .finally(() => {
        if (!cancelled) {
          setLoading(false)
        }
      })

    return () => {
      cancelled = true
    }
  }, [id, minMinutes])

  function handleMinChange(event) {
    setLoading(true)
    setMinMinutes(Number(event.target.value))
  }

  const best = data ? findBestWindow(data.days) : null
  const missing = data ? data.membersWithoutCourses : []

  return (
    <>
      <p>
        <Link to="/groups">← Back to groups</Link>
      </p>

      {error && (
        <p className="error" role="alert">
          {error}
        </p>
      )}

      {loading && !data ? (
        <p className="muted">Loading...</p>
      ) : (
        data && (
          <>
            <h1>{data.groupName}</h1>
            <p className="muted">Members: {data.members.join(', ')}</p>

            {missing.length > 0 && (
              <p className="warning" role="status">
                {missing.join(', ')} {missing.length === 1 ? "hasn't" : "haven't"} added any
                courses yet, so the times below may look freer than they really are.
              </p>
            )}

            {best && (
              <p className="best">
                Longest shared gap: <strong>{formatDay(best.day)}</strong>,{' '}
                {formatTime(best.start)} – {formatTime(best.end)}
              </p>
            )}

            <div className="controls">
              <label className="inline">
                Show shared gaps of at least
                <select value={minMinutes} onChange={handleMinChange}>
                  {MIN_OPTIONS.map((minutes) => (
                    <option key={minutes} value={minutes}>
                      {minutes} minutes
                    </option>
                  ))}
                </select>
              </label>
            </div>

            <WeekGrid days={data.days} loading={loading} freeLabel="Everyone is free" />
          </>
        )
      )}
    </>
  )
}