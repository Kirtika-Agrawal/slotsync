import { useCallback, useEffect, useState } from 'react'
import { api } from './api.js'

// The backend returns one row per slot, so we group rows into one card per course
function groupByCourse(rows) {
  const map = new Map()
  for (const row of rows) {
    if (!map.has(row.courseCode)) {
      map.set(row.courseCode, {
        code: row.courseCode,
        title: row.courseTitle,
        slots: [],
        ids: [],
      })
    }
    const course = map.get(row.courseCode)
    course.slots.push(row.slot)
    course.ids.push(row.id)
  }
  return [...map.values()]
}

// "a1+ta1", "A1, TA1" and "A1 TA1" all become ["A1", "TA1"]
function parseSlots(text) {
  return text
    .split(/[\s,+]+/)
    .map((slot) => slot.trim().toUpperCase())
    .filter(Boolean)
}

export default function Courses() {
  const [courses, setCourses] = useState([])
  const [loading, setLoading] = useState(true)
  const [listError, setListError] = useState('')

  const [code, setCode] = useState('')
  const [title, setTitle] = useState('')
  const [slotText, setSlotText] = useState('')
  const [formError, setFormError] = useState('')
  const [saving, setSaving] = useState(false)

  const loadCourses = useCallback(async () => {
    try {
      const rows = await api('/api/enrollments')
      setCourses(groupByCourse(rows))
      setListError('')
    } catch (err) {
      setListError(err.message)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    loadCourses()
  }, [loadCourses])

  async function handleAdd(event) {
    event.preventDefault()
    setFormError('')

    const slots = parseSlots(slotText)
    if (slots.length === 0) {
      setFormError('Enter at least one slot, for example A1+TA1')
      return
    }

    setSaving(true)
    try {
      await api('/api/enrollments', {
        method: 'POST',
        body: { courseCode: code, courseTitle: title, slots },
      })
      setCode('')
      setTitle('')
      setSlotText('')
      await loadCourses()
    } catch (err) {
      setFormError(err.message)
    } finally {
      setSaving(false)
    }
  }

  async function handleRemove(course) {
    if (!window.confirm(`Remove ${course.code} from your courses?`)) {
      return
    }
    try {
      await Promise.all(
        course.ids.map((id) => api(`/api/enrollments/${id}`, { method: 'DELETE' })),
      )
    } catch (err) {
      setListError(err.message)
    }
    await loadCourses()
  }

  return (
    <>
      <h1>My courses</h1>
      <p className="muted">
        Add each course with its slots as shown on VTOP, for example A1+TA1 or L21+L22.
      </p>

      <form className="card wide" onSubmit={handleAdd}>
        <div className="row">
          <label>
            Course code
            <input
              value={code}
              onChange={(e) => setCode(e.target.value)}
              placeholder="BCSE202L"
              maxLength={20}
              required
            />
          </label>
          <label>
            Course title
            <input
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="Database Systems"
              maxLength={150}
              required
            />
          </label>
          <label>
            Slots
            <input
              value={slotText}
              onChange={(e) => setSlotText(e.target.value)}
              placeholder="A1+TA1"
              required
            />
          </label>
        </div>

        {formError && (
          <p className="error" role="alert">
            {formError}
          </p>
        )}

        <button type="submit" disabled={saving}>
          {saving ? 'Adding...' : 'Add course'}
        </button>
      </form>

      {listError && (
        <p className="error" role="alert">
          {listError}
        </p>
      )}

      {loading ? (
        <p className="muted">Loading...</p>
      ) : courses.length === 0 ? (
        <p className="muted">No courses yet. Add your first one above.</p>
      ) : (
        <ul className="course-list">
          {courses.map((course) => (
            <li key={course.code} className="course">
              <div>
                <strong>{course.title}</strong>
                <div className="muted">{course.code}</div>
                <div className="chips">
                  {course.slots.map((slot) => (
                    <span key={slot} className="chip">
                      {slot}
                    </span>
                  ))}
                </div>
              </div>
              <button className="secondary danger" onClick={() => handleRemove(course)}>
                Remove
              </button>
            </li>
          ))}
        </ul>
      )}
    </>
  )
}