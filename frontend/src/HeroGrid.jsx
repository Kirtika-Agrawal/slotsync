// [kind, startHour, endHour] for each weekday column
const SAMPLE = [
  [['theory', 8, 8.8], ['free', 9, 11.5], ['theory', 12, 12.8], ['free', 14.8, 20]],
  [['free', 8, 10], ['lab', 10.2, 11.7], ['free', 12, 17], ['theory', 17, 17.8]],
  [['theory', 9, 9.8], ['free', 10, 14], ['lab', 14, 15.6], ['free', 16, 20]],
  [['free', 8, 12], ['theory', 12, 12.8], ['free', 13, 17]],
  [['theory', 10, 10.8], ['free', 11, 18]],
]

export default function HeroGrid() {
  return (
    <div className="hero-grid" aria-hidden="true">
      {SAMPLE.map((segments, column) => (
        <div key={column} className="hero-col">
          {segments.map(([kind, from, to], index) => (
            <span
              key={index}
              className={`hero-seg ${kind}`}
              style={{
                top: `${((from - 8) / 12) * 100}%`,
                height: `${((to - from) / 12) * 100}%`,
                '--delay': `${column * 120 + index * 90}ms`,
              }}
            />
          ))}
        </div>
      ))}
    </div>
  )
}