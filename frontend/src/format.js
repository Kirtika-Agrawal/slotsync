// "14:00:00" becomes "2:00 PM"
export function formatTime(time) {
  const [hourText, minute] = time.split(':')
  const hour = Number(hourText)
  const suffix = hour >= 12 ? 'PM' : 'AM'
  const hour12 = hour % 12 === 0 ? 12 : hour % 12
  return `${hour12}:${minute} ${suffix}`
}

// "MONDAY" becomes "Monday"
export function formatDay(day) {
  return day.charAt(0) + day.slice(1).toLowerCase()
}

// "14:30:00" becomes 870 (minutes since midnight)
export function toMinutes(time) {
  const [hour, minute] = time.split(':').map(Number)
  return hour * 60 + minute
}

// "14:30:00" becomes "2:30"
export function formatShort(time) {
  const [hourText, minute] = time.split(':')
  const hour = Number(hourText) % 12 || 12
  return `${hour}:${minute}`
}