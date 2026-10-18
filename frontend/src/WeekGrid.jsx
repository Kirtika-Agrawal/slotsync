import { formatDay, formatShort, formatTime, toMinutes } from './format.js'

const START_MIN = 8 * 60
const END_MIN = 20 * 60
const TOTAL_MIN = END_MIN - START_MIN
const HOURS = Array.from({ length: 12 }, (_, i) => 8 + i)

// Turns a time in minutes into a position (0 to 100) down the day column
function percent(minutes) {
  const clamped = Math.min(Math.max(minutes, START_MIN), END_MIN)
  return ((clamped - START_MIN) / TOTAL_MIN) * 100
}

function formatHour(hour) {
  return `${hour % 12 === 0 ? 12 : hour % 12} ${hour >= 12 ? 'PM' : 'AM'}`
}

// Classes that overlap in time are placed side by side, in "lanes"
function layoutLanes(classes) {
  const blocks = classes
    .map((c) => ({
      code: c.courseCode,
      slot: c.slot,
      start: c.start,
      end: c.end,
      startMin: toMinutes(c.start),
      endMin: toMinutes(c.end),
      isLab: /^L\d+$/.test(c.slot),
    }))
    .sort((a, b) => a.startMin - b.startMin || a.endMin - b.endMin)

  const placed = []
  let cluster = []
  let clusterEnd = -1

  function flushCluster() {
    const laneEnds = []
    for (const block of cluster) {
      let lane = laneEnds.findIndex((end) => end <= block.startMin)
      if (lane === -1) {
        lane = laneEnds.length
        laneEnds.push(block.endMin)
      } else {
        laneEnds[lane] = block.endMin
      }
      block.lane = lane
    }
    for (const block of cluster) {
      block.lanes = laneEnds.length
      placed.push(block)
    }
    cluster = []
    clusterEnd = -1
  }

  for (const block of blocks) {
    if (cluster.length > 0 && block.startMin >= clusterEnd) {
      flushCluster()
    }
    cluster.push(block)
    clusterEnd = Math.max(clusterEnd, block.endMin)
  }
  if (cluster.length > 0) {
    flushCluster()
  }

  return placed
}

export default function WeekGrid({ days, loading = false, freeLabel = 'Free' }) {
  const hasClasses = days.some((day) => (day.classes ?? []).length > 0)

  return (
    <div>
      <ul className="legend" aria-label="Key">
        {hasClasses && (
          <>
            <li>
              <span className="swatch theory" />
              Theory class
            </li>
            <li>
              <span className="swatch lab" />
              Lab class
            </li>
          </>
        )}
        <li>
          <span className="swatch free" />
          {freeLabel}
        </li>
      </ul>

      <div
        className={loading ? 'grid-scroll loading' : 'grid-scroll'}
        role="region"
        aria-label="Weekly timetable"
        tabIndex={0}
      >
        <div className="week-grid">
          <div className="axis-head" />
          {days.map((day) => (
            <h2 key={`head-${day.day}`} className="day-head">
              {formatDay(day.day)}
            </h2>
          ))}

          <div className="axis" aria-hidden="true">
            {HOURS.map((hour) => (
              <span
                key={hour}
                className="hour-label"
                style={{ top: `${percent(hour * 60)}%` }}
              >
                {formatHour(hour)}
              </span>
            ))}
          </div>

          {days.map((day) => (
            <div key={day.day} className="day-body">
              {day.freeWindows.map((window) => {
                const top = percent(toMinutes(window.start))
                const height = percent(toMinutes(window.end)) - top
                return (
                  <div
                    key={`free-${window.start}`}
                    className="free-block"
                    style={{ top: `${top}%`, height: `${height}%` }}
                    title={`${freeLabel}: ${formatTime(window.start)} – ${formatTime(window.end)}`}
                  >
                    {formatTime(window.start)} – {formatTime(window.end)}
                  </div>
                )
              })}

              {layoutLanes(day.classes ?? []).map((block) => {
                const top = percent(block.startMin)
                const height = percent(block.endMin) - top
                return (
                  <div
                    key={`${block.slot}-${block.start}`}
                    className={`class-block ${block.isLab ? 'lab' : 'theory'}`}
                    style={{
                      top: `${top}%`,
                      height: `${height}%`,
                      left: `calc(${(block.lane / block.lanes) * 100}% + 2px)`,
                      width: `calc(${100 / block.lanes}% - 4px)`,
                    }}
                    title={`${block.code} (${block.slot}), ${formatTime(block.start)} – ${formatTime(block.end)}`}
                  >
                    <strong>{block.code}</strong>
                    <span>
                      {block.slot} {formatShort(block.start)}–{formatShort(block.end)}
                    </span>
                  </div>
                )
              })}
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}