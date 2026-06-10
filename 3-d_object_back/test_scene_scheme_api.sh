#!/bin/zsh
set -euo pipefail

BASE="http://127.0.0.1:8080/api"
EMAIL="scene_test_$(date +%s)@example.com"
PASSWORD="scene-pass-123"
NICK="scene_tester"

echo "[register]"
REGISTER_PAYLOAD=$(jq -n \
  --arg nickname "$NICK" \
  --arg email "$EMAIL" \
  --arg password "$PASSWORD" \
  '{nickname:$nickname,email:$email,password:$password}')
REGISTER=$(curl -s -X POST "$BASE/users/register" -H "Content-Type: application/json" -d "$REGISTER_PAYLOAD")
echo "$REGISTER" | jq .

echo "[login]"
LOGIN_PAYLOAD=$(jq -n \
  --arg account "$EMAIL" \
  --arg password "$PASSWORD" \
  '{account:$account,password:$password}')
LOGIN=$(curl -s -X POST "$BASE/users/login" -H "Content-Type: application/json" -H "User-Agent: Codex" -d "$LOGIN_PAYLOAD")
echo "$LOGIN" | jq .
TOKEN=$(echo "$LOGIN" | jq -r '.accessToken')
AUTH_HEADER="Authorization: Bearer $TOKEN"

echo "[create model]"
MODEL_PAYLOAD=$(jq -n '
  {
    model: {
      title: "Scene API Test Model",
      story: "for scene api",
      productionYear: 2026,
      categoryId: 1,
      renderStatus: "success",
      thumbnailUrl: "http://localhost:8080/static/models/example.glb",
      isPublic: false
    },
    tags: ["scene-test"]
  }')
MODEL=$(curl -s -X POST "$BASE/models/upload" -H "$AUTH_HEADER" -H "Content-Type: application/json" -d "$MODEL_PAYLOAD")
echo "$MODEL" | jq .
MID=$(echo "$MODEL" | jq -r '.mid')

echo "[bind model file]"
BIND_PAYLOAD=$(jq -n --argjson mid "$MID" '
  {
    mid: $mid,
    fileUrl: "/static/models/example.glb",
    fileType: "GLB",
    originalName: "example.glb"
  }')
BIND=$(curl -s -X POST "$BASE/model-files/bind-url" -H "$AUTH_HEADER" -H "Content-Type: application/json" -d "$BIND_PAYLOAD")
echo "$BIND" | jq .

echo "[create scheme]"
CREATE_PAYLOAD=$(jq -n --argjson mid "$MID" '
  {
    title: "测试展馆方案",
    roomDisplayName: "中型展厅",
    roomId: "medium",
    roomName: "中型展厅",
    shape: "flat",
    area: "80㎡",
    lenM: 8,
    widM: 10,
    heightM: 4.2,
    kind: "flat",
    dimLine: "8m x 10m x 4.2m",
    roomData: {
      id: "medium",
      name: "中型展厅",
      shape: "flat",
      area: "80㎡",
      lenM: 8,
      widM: 10,
      heightM: 4.2,
      kind: "flat"
    },
    placements: [
      {
        placementUid: "p-test-1",
        mid: $mid,
        icon: "artifact",
        normX: 12.5,
        normY: 68,
        sortOrder: 0
      }
    ]
  }')
CREATE=$(curl -s -X POST "$BASE/scene-schemes" -H "$AUTH_HEADER" -H "Content-Type: application/json" -d "$CREATE_PAYLOAD")
echo "$CREATE" | jq .
SCHEME_ID=$(echo "$CREATE" | jq -r '.schemeId')

echo "[list mine]"
LIST=$(curl -s "$BASE/scene-schemes/mine" -H "$AUTH_HEADER")
echo "$LIST" | jq .

echo "[detail]"
DETAIL=$(curl -s "$BASE/scene-schemes/$SCHEME_ID" -H "$AUTH_HEADER")
echo "$DETAIL" | jq .

echo "[update]"
UPDATE_PAYLOAD=$(jq -n --argjson mid "$MID" '
  {
    title: "测试展馆方案-更新",
    roomDisplayName: "中型展厅",
    roomId: "medium",
    placements: [
      {
        placementUid: "p-test-1",
        mid: $mid,
        icon: "artifact",
        normX: 20,
        normY: 30,
        sortOrder: 1
      }
    ]
  }')
UPDATE=$(curl -s -X PUT "$BASE/scene-schemes/$SCHEME_ID" -H "$AUTH_HEADER" -H "Content-Type: application/json" -d "$UPDATE_PAYLOAD")
echo "$UPDATE" | jq .

echo "[delete]"
DELETE_RES=$(curl -s -X DELETE "$BASE/scene-schemes/$SCHEME_ID" -H "$AUTH_HEADER")
echo "$DELETE_RES" | jq .

echo "[list mine after delete]"
LIST2=$(curl -s "$BASE/scene-schemes/mine" -H "$AUTH_HEADER")
echo "$LIST2" | jq .
