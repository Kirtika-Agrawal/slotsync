import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from './api.js'

const MIN_OPTIONS = [30, 45, 60, 90, 120]

// "14:00:00" becomes "2:00 PM"
function formatTime(time) {
  const [hourText, minute] = time.split(':')
  const hour = Number(hourText)
  const suffix = hour >= 12 ? 'PM' : 'AM'
  const hour12 = hour % 12 === 0 ? 12 : hour % 12
  return `${hour12}:${minute} ${suffix}`
}

// "MONDAY" becomes "Monday"
function formatDay(day) {
  return day.charAt(0) + day.slice(1).toLowerCase()
}

// Mixes classes and free gaps into one list, in time order
function buildItems(day) {
  const classes = day.classes.map((c) => ({
    kind: 'class',
    start: c.start,
    end: c.end,
    label: c.courseCode,
    detail: `Slot ${c.slot}`,
  }))
  const free = day.freeWindows.map((w) => ({
    kind: 'free',
    start: w.start,
    end: w.end,
    label: 'Free',
    detail: '',
  }))
  return [...classes, ...free].sort((a, b) => a.start.localeCompare(b.start))
}

export default function Timetable() {
  const [minMinutes, setMinMinutes] = useState(60)
  const [days, setDays] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let cancelled = false

    api(`/api/timetable?minMinutes=${minMinutes}`)
      .then((data) => {
        if (!cancelled) {
          setDays(data)
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

    // If the dropdown changes before this request finishes, ignore the old answer
    return () => {
      cancelled = true
    }
  }, [minMinutes])

  function handleMinChange(event) {
    setLoading(true)
    setMinMinutes(Number(event.target.value))
  }

  const hasClasses = days.some((day) => day.classes.length > 0)

  return (
    <>
      <h1>My timetable</h1>
      <p className="muted">
        Your classes (blue) and the gaps between them (green). Shorter gaps are hidden.
      </p>

      <div className="controls">
        <label className="inline">
          Show free gaps of at least
          <select value={minMinutes} onChange={handleMinChange}>
            {MIN_OPTIONS.map((minutes) => (
              <option key={minutes} value={minutes}>
                {minutes} minutes
              </option>
            ))}
          </select>
        </label>
      </div>

      {error && (
        <p className="error" role="alert">
          {error}
        </p>
      )}

      {loading && days.length === 0 ? (
        <p className="muted">Loading...</p>
      ) : (
        <>
          {!loading && !error && !hasClasses && (
            <p className="muted">
              You have no classes yet. <Link to="/courses">Add your courses</Link> to see
              your week.
            </p>
          )}

          <div className={loading ? 'week loading' : 'week'}>
            {days.map((day) => (
              <section key={day.day} className="day">
                <h2>{formatDay(day.day)}</h2>
                {buildItems(day).map((item, index) => (
                  <div key={`${item.kind}-${item.start}-${index}`} className={`block ${item.kind}`}>
                    <div className="block-time">
                      {formatTime(item.start)} – {formatTime(item.end)}
                    </div>
                    <div className="block-label">{item.label}</div>
                    {item.detail && <div className="block-detail">{item.detail}</div>}
                  </div>
                ))}
              </section>
            ))}
          </div>
        </>
      )}
    </>
  )
}