import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from './api.js'
import WeekGrid from './WeekGrid.jsx'
import { GridSkeleton } from './Skeleton.jsx'

const MIN_OPTIONS = [30, 45, 60, 90, 120]

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
        Each class sits at its real time of day. Yellow marks your free gaps, and gaps
        shorter than the minimum are hidden.
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

      {loading && days.length === 0 && <GridSkeleton />}

      {!loading && !error && days.length > 0 && !hasClasses && (
        <p className="muted">
          You have no classes yet. <Link to="/courses">Add your courses</Link> to see your
          week.
        </p>
      )}

      {days.length > 0 && <WeekGrid days={days} loading={loading} freeLabel="Free" />}
    </>
  )
}