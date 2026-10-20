#!/usr/bin/env bash
# Creates three demo students with realistic VIT timetables and one study group,
# so the app has something to show. Run it once, with the backend running.
#
# Usage:  bash scripts/seed-demo.sh [backend-url]

BASE_URL="${1:-http://localhost:8080}"
PASSWORD="demopass123"

signup() {   # name, email
  curl -s -o /dev/null -X POST "$BASE_URL/api/auth/signup" \
    -H "Content-Type: application/json" \
    -d "{\"name\":\"$1\",\"email\":\"$2\",\"password\":\"$PASSWORD\"}"
}

login() {    # email, prints the token
  curl -s -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"email\":\"$1\",\"password\":\"$PASSWORD\"}" \
    | sed 's/.*"token":"\([^"]*\)".*/\1/'
}

add_course() {   # token, course code, course title, slots as a JSON list
  local status
  status=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/enrollments" \
    -H "Authorization: Bearer $1" \
    -H "Content-Type: application/json" \
    -d "{\"courseCode\":\"$2\",\"courseTitle\":\"$3\",\"slots\":$4}")
  echo "  $status  $2 $3"
}

echo "Creating demo accounts at $BASE_URL ..."
signup "Aarav Mehta" "aarav@example.com"
signup "Meera Iyer" "meera@example.com"
signup "Rohan Das" "rohan@example.com"

T1=$(login "aarav@example.com")
T2=$(login "meera@example.com")
T3=$(login "rohan@example.com")

for token in "$T1" "$T2" "$T3"; do
  if [[ "$token" != eyJ* ]]; then
    echo "Login failed. Is the backend running at $BASE_URL?"
    exit 1
  fi
done

echo "Aarav's courses"
add_course "$T1" "BCSE202L" "Database Systems" '["A1","TA1"]'
add_course "$T1" "BCSE202P" "Database Systems Lab" '["L21","L22"]'
add_course "$T1" "BCSE301L" "Operating Systems" '["B1","TB1"]'
add_course "$T1" "BCSE303L" "Computer Networks" '["C1","TC1"]'
add_course "$T1" "BMAT202L" "Probability and Statistics" '["D1","TD1"]'
add_course "$T1" "BCSE305L" "Software Engineering" '["E1","TF1"]'

echo "Meera's courses"
add_course "$T2" "BCSE202L" "Database Systems" '["A2","TA2"]'
add_course "$T2" "BCSE202P" "Database Systems Lab" '["L51","L52"]'
add_course "$T2" "BCSE301L" "Operating Systems" '["B2","TB2"]'
add_course "$T2" "BCSE303L" "Computer Networks" '["C2","TC2"]'
add_course "$T2" "BMAT202L" "Probability and Statistics" '["D2","TD2"]'
add_course "$T2" "BCSE305L" "Software Engineering" '["E2","TF2"]'

echo "Rohan's courses"
add_course "$T3" "BCSE202L" "Database Systems" '["A1","TA1"]'
add_course "$T3" "BCSE202P" "Database Systems Lab" '["L21","L22"]'
add_course "$T3" "BCSE301L" "Operating Systems" '["B2","TB2"]'
add_course "$T3" "BCSE303L" "Computer Networks" '["C1","TC1"]'
add_course "$T3" "BCSE307L" "Machine Learning" '["G2","TG2"]'
add_course "$T3" "BCSE305L" "Software Engineering" '["E2","TF2"]'

echo "Creating the study group"
INVITE=$(curl -s -X POST "$BASE_URL/api/groups" \
  -H "Authorization: Bearer $T1" \
  -H "Content-Type: application/json" \
  -d '{"name":"DBMS Study Group"}' \
  | sed 's/.*"inviteCode":"\([^"]*\)".*/\1/')

if [[ ${#INVITE} -ne 6 ]]; then
  echo "Could not create the group."
  exit 1
fi

for token in "$T2" "$T3"; do
  curl -s -o /dev/null -X POST "$BASE_URL/api/groups/join" \
    -H "Authorization: Bearer $token" \
    -H "Content-Type: application/json" \
    -d "{\"inviteCode\":\"$INVITE\"}"
done

echo
echo "Done. Log in with any of these (password: $PASSWORD):"
echo "  aarav@example.com"
echo "  meera@example.com"
echo "  rohan@example.com"
echo "Group invite code: $INVITE"
