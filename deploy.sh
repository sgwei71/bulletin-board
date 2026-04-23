#!/bin/bash

# 배포 스크립트 - 서버에서 실행

set -e

cd /home/user1/bulletin-board

echo "🔄 기존 컨테이너 중지 및 제거..."
docker-compose down || true

echo "📥 .env 파일 확인..."
if [ ! -f .env ]; then
    echo "❌ .env 파일이 없습니다. 먼저 .env 파일을 생성해주세요."
    exit 1
fi

echo "🐳 Docker 이미지 풀..."
docker-compose pull

echo "🚀 새 컨테이너 시작..."
docker-compose up -d

echo "⏳ 헬스체크 대기 중..."
sleep 5

echo "✅ 배포 완료!"
echo ""
echo "📋 컨테이너 상태:"
docker-compose ps

echo ""
echo "🔗 접속 URL: http://223.130.155.149:8080"
