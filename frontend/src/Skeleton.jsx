const BLOCKS = [
  [[4, 8], [24, 10], [48, 12]],
  [[10, 12], [40, 8]],
  [[4, 8], [26, 12], [60, 10]],
  [[14, 10], [44, 14]],
  [[8, 8], [34, 12], [64, 8]],
]

export function GridSkeleton() {
  return (
    <div className="grid-scroll" aria-busy="true" aria-label="Loading timetable">
      <div className="week-grid">
        <div className="axis-head" />
        {BLOCKS.map((_, index) => (
          <div key={`head-${index}`} className="day-head">
            <span className="skeleton skeleton-line" />
          </div>
        ))}
        <div className="axis" />
        {BLOCKS.map((blocks, column) => (
          <div key={column} className="day-body">
            {blocks.map(([top, height], index) => (
              <span
                key={index}
                className="skeleton skeleton-block"
                style={{
                  top: `${top}%`,
                  height: `${height}%`,
                  animationDelay: `${(column + index) * 120}ms`,
                }}
              />
            ))}
          </div>
        ))}
      </div>
    </div>
  )
}

export function ListSkeleton() {
  return (
    <ul className="course-list" aria-busy="true" aria-label="Loading">
      {[0, 1, 2].map((row) => (
        <li key={row} className="course">
          <div className="skeleton-lines">
            <span className="skeleton skeleton-title" style={{ animationDelay: `${row * 120}ms` }} />
            <span className="skeleton skeleton-line" style={{ animationDelay: `${row * 120}ms` }} />
          </div>
        </li>
      ))}
    </ul>
  )
}