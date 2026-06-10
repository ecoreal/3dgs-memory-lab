#!/bin/bash
# ============================================
#  MediaController 接口测试脚本
#  用法: ./test_media_api.sh
# ============================================

BASE_URL="http://localhost:8080"
PASS=0
FAIL=0

# 颜色
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

print_result() {
    local name="$1" status="$2" body="$3"
    if [ "$status" -ge 200 ] && [ "$status" -lt 300 ]; then
        echo -e "${GREEN}[PASS]${NC} $name (HTTP $status)"
        echo "  Response: $body"
        PASS=$((PASS + 1))
    else
        echo -e "${RED}[FAIL]${NC} $name (HTTP $status)"
        echo "  Response: $body"
        FAIL=$((FAIL + 1))
    fi
    echo ""
}

echo "============================================"
echo "  MediaController API 测试"
echo "============================================"
echo ""

# ------ 准备测试文件 ------
TEST_IMAGE="/Users/mac/Downloads/1.jpg"
TEST_AUDIO="/tmp/test_audio.wav"

# 用 python3 生成一个小的 wav 测试音频
python3 -c "
import wave, struct, math
f = wave.open('$TEST_AUDIO', 'w')
f.setnchannels(1)
f.setsampwidth(2)
f.setframerate(44100)
for i in range(44100):  # 1 秒
    v = int(32767 * math.sin(2 * math.pi * 440 * i / 44100))
    f.writeframes(struct.pack('h', v))
f.close()
" 2>/dev/null

if [ ! -f "$TEST_IMAGE" ]; then
    echo -e "${RED}测试图片不存在: $TEST_IMAGE${NC}"
    exit 1
fi
if [ ! -f "$TEST_AUDIO" ]; then
    echo -e "${RED}测试音频生成失败${NC}"
    exit 1
fi

echo "测试图片: $TEST_IMAGE"
echo "测试音频: $TEST_AUDIO"
echo ""

# ------ Step 1: 登录获取 Token ------
echo -e "${YELLOW}>>> Step 1: 登录获取 Token${NC}"
LOGIN_RESP=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/users/login" \
    -H "Content-Type: application/json" \
    -d '{"account":"test@test.com","password":"123456"}')

LOGIN_STATUS=$(echo "$LOGIN_RESP" | tail -1)
LOGIN_BODY=$(echo "$LOGIN_RESP" | sed '$d')

if [ "$LOGIN_STATUS" != "200" ]; then
    echo -e "${YELLOW}登录失败 (HTTP $LOGIN_STATUS)，尝试先注册...${NC}"

    REG_RESP=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/users/register" \
        -H "Content-Type: application/json" \
        -d '{"nickname":"tester","email":"test@test.com","password":"123456"}')
    REG_STATUS=$(echo "$REG_RESP" | tail -1)
    REG_BODY=$(echo "$REG_RESP" | sed '$d')
    echo "  注册: HTTP $REG_STATUS - $REG_BODY"

    LOGIN_RESP=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/users/login" \
        -H "Content-Type: application/json" \
        -d '{"account":"test@test.com","password":"123456"}')
    LOGIN_STATUS=$(echo "$LOGIN_RESP" | tail -1)
    LOGIN_BODY=$(echo "$LOGIN_RESP" | sed '$d')
fi

if [ "$LOGIN_STATUS" != "200" ]; then
    echo -e "${RED}登录失败，无法继续测试 (HTTP $LOGIN_STATUS)${NC}"
    echo "  $LOGIN_BODY"
    exit 1
fi

# 提取 accessToken (兼容无 jq 的情况)
if command -v jq &>/dev/null; then
    TOKEN=$(echo "$LOGIN_BODY" | jq -r '.accessToken')
else
    TOKEN=$(echo "$LOGIN_BODY" | python3 -c "import sys,json; print(json.load(sys.stdin)['accessToken'])")
fi

echo -e "${GREEN}登录成功${NC}, Token: ${TOKEN:0:20}..."
echo ""

AUTH="Authorization: Bearer $TOKEN"

# ------ Test 1: 上传图片 ------
echo -e "${YELLOW}>>> Test 1: POST /api/media/image (上传图片)${NC}"
RESP=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/media/image" \
    -H "$AUTH" \
    -F "file=@$TEST_IMAGE")
STATUS=$(echo "$RESP" | tail -1)
BODY=$(echo "$RESP" | sed '$d')
print_result "上传图片" "$STATUS" "$BODY"

# ------ Test 2: 上传音频 ------
echo -e "${YELLOW}>>> Test 2: POST /api/media/audio (上传音频)${NC}"
RESP=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/media/audio" \
    -H "$AUTH" \
    -F "file=@$TEST_AUDIO")
STATUS=$(echo "$RESP" | tail -1)
BODY=$(echo "$RESP" | sed '$d')
print_result "上传音频" "$STATUS" "$BODY"

# ------ Test 3: 访问上传的图片 ------
if command -v jq &>/dev/null; then
    IMG_URL=$(echo "$BODY" | jq -r '.url // empty')
else
    IMG_URL=$(echo "$BODY" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('url',''))" 2>/dev/null)
fi

# 用 Test 1 的结果重新获取
RESP1_BODY=$(curl -s -X POST "$BASE_URL/api/media/image" \
    -H "$AUTH" \
    -F "file=@$TEST_IMAGE")
if command -v jq &>/dev/null; then
    IMG_URL=$(echo "$RESP1_BODY" | jq -r '.url // empty')
else
    IMG_URL=$(echo "$RESP1_BODY" | python3 -c "import sys,json; print(json.load(sys.stdin).get('url',''))" 2>/dev/null)
fi

if [ -n "$IMG_URL" ]; then
    echo -e "${YELLOW}>>> Test 3: GET 上传的图片 URL${NC}"
    RESP=$(curl -s -w "\n%{http_code}" -o /dev/null "$IMG_URL")
    STATUS=$(echo "$RESP" | tail -1)
    print_result "访问上传图片" "$STATUS" "$IMG_URL"
fi

# ------ Test 4: enhance 接口 ------
echo -e "${YELLOW}>>> Test 4: POST /api/media/enhance (图片增强 - 调用远端)${NC}"
RESP=$(curl -s -w "\n%{http_code}" --max-time 60 -X POST "$BASE_URL/api/media/enhance" \
    -H "$AUTH" \
    -F "file=@$TEST_IMAGE")
STATUS=$(echo "$RESP" | tail -1)
BODY=$(echo "$RESP" | sed '$d')
print_result "图片增强(enhance)" "$STATUS" "$BODY"

# ------ Test 5: colorize 接口 ------
echo -e "${YELLOW}>>> Test 5: POST /api/media/colorize (图片上色 - 调用远端)${NC}"
RESP=$(curl -s -w "\n%{http_code}" --max-time 60 -X POST "$BASE_URL/api/media/colorize" \
    -H "$AUTH" \
    -F "file=@$TEST_IMAGE")
STATUS=$(echo "$RESP" | tail -1)
BODY=$(echo "$RESP" | sed '$d')
print_result "图片上色(colorize)" "$STATUS" "$BODY"

# ------ Test 6: 未认证访问应返回 401 ------
echo -e "${YELLOW}>>> Test 6: POST /api/media/image 无Token (应返回401)${NC}"
RESP=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/media/image" \
    -F "file=@$TEST_IMAGE")
STATUS=$(echo "$RESP" | tail -1)
BODY=$(echo "$RESP" | sed '$d')
if [ "$STATUS" = "401" ]; then
    echo -e "${GREEN}[PASS]${NC} 无Token返回401"
    PASS=$((PASS + 1))
else
    echo -e "${RED}[FAIL]${NC} 期望401，实际HTTP $STATUS"
    FAIL=$((FAIL + 1))
fi
echo ""

# ------ 清理 ------
rm -f "$TEST_AUDIO"

# ------ 汇总 ------
echo "============================================"
TOTAL=$((PASS + FAIL))
echo -e "  测试完成: ${GREEN}$PASS 通过${NC} / ${RED}$FAIL 失败${NC} / 共 $TOTAL"
echo "============================================"

exit $FAIL
