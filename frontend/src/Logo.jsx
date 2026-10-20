export default function Logo({ size = 28, inverted = false }) {
  const base = inverted ? '#FAFAF7' : '#1B1D3A'

  return (
    <svg width={size} height={size} viewBox="0 0 24 24" aria-hidden="true">
      <rect x="1.5" y="1.5" width="9.5" height="9.5" rx="1.5" fill={base} />
      <rect x="13" y="1.5" width="9.5" height="9.5" rx="1.5" fill={base} />
      <rect x="1.5" y="13" width="9.5" height="9.5" rx="1.5" fill={base} />
      <rect
        x="13"
        y="13"
        width="9.5"
        height="9.5"
        rx="1.5"
        fill="#FFE14D"
        stroke={base}
        strokeWidth="1.5"
      />
    </svg>
  )
}