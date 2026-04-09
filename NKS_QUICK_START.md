# NKS 배포 빠른 시작 가이드

## 🚀 5분 안에 배포하기

### 1단계: GitHub Secrets 설정 (한 번만)

```bash
# Docker Hub 확인
# 1. docker.io 로그인
# 2. Settings → Security → New Access Token 생성
# 3. 토큰 복사

# GitHub 저장소 열기
# Settings → Secrets and variables → New repository secret

# 다음 3개 추가:
# - DOCKER_HUB_USERNAME: 당신의 docker hub username
# - DOCKER_HUB_TOKEN: 위에서 생성한 토큰
# - KUBE_CONFIG: 아래 과정으로 생성
```

### KUBE_CONFIG 생성하기
```bash
# 터미널에서 실행
cat ~/.kube/config | base64 -w 0

# 출력된 긴 문자열 전체를 복사하여
# GitHub Secrets의 KUBE_CONFIG에 붙여넣기
```

---

### 2단계: 파일 수정

```bash
# k8s/deployment.yaml 열기
# YOUR_DOCKER_HUB_USERNAME을 당신의 docker hub username으로 변경

# 예:
# 변경 전: image: YOUR_DOCKER_HUB_USERNAME/bulletin-board:latest
# 변경 후: image: myusername/bulletin-board:latest
```

또는 터미널에서:
```bash
sed -i '' 's/YOUR_DOCKER_HUB_USERNAME/당신의_사용자명/g' k8s/deployment.yaml
```

---

### 3단계: 배포

```bash
# 모든 변경사항 커밋
git add .
git commit -m "[배포] NKS 파이프라인 구성"

# main 브랜치에 푸시
git push origin main
```

**완료!** GitHub Actions가 자동으로 실행됩니다.

---

## 📊 배포 진행 상황 확인

### GitHub Actions에서 확인
```
GitHub 저장소 → Actions 탭 → "Build and Deploy to NKS"
```

### 터미널에서 확인
```bash
# Pod 상태 확인 (5-10초 후)
kubectl get pods -n edu

# 모든 리소스 확인
kubectl get all -n edu

# 외부 IP 확인 (1-2분 후)
kubectl get svc bulletin-board -n edu

# 로그 보기 (실시간)
kubectl logs -n edu -l app=bulletin-board -f
```

---

## 🌐 애플리케이션 접근

```bash
# 1. 외부 IP 확인
kubectl get svc bulletin-board -n edu -o jsonpath='{.status.loadBalancer.ingress[0].ip}'

# 2. 브라우저에서 접근
# http://<위의_IP_주소>
```

예시:
```
http://203.110.123.45
```

---

## ✅ 배포 완료 체크리스트

- [ ] GitHub Secrets 3개 등록됨
- [ ] k8s/deployment.yaml에 실제 username 입력됨
- [ ] `git push origin main` 완료
- [ ] GitHub Actions 워크플로우 성공 (초록색 ✓)
- [ ] `kubectl get pods -n edu` 에서 Running 상태 pod 2개 보임
- [ ] 외부 IP 할당됨 (1-2분 소요)
- [ ] 브라우저에서 http://<IP> 접근 가능

---

## 🔄 다시 배포하기 (이후 업데이트)

```bash
# 코드 수정 후
git add .
git commit -m "[기능] 설명"
git push origin main

# 자동으로 재배포됨!
```

---

## 🆘 문제 해결

### 포드가 계속 재시작되는 경우
```bash
kubectl logs -n edu -l app=bulletin-board -f
# 에러 메시지 확인
```

### 외부 IP가 Pending인 경우
```bash
# 기다리기 (1-2분)
kubectl get svc bulletin-board -n edu -w

# 여전히 Pending이면:
kubectl describe svc bulletin-board -n edu
```

### 배포가 실패하는 경우
1. GitHub Actions 로그 확인
2. Secrets 올바르게 설정되었는지 확인
3. Docker Hub 토큰이 유효한지 확인
4. kubeconfig base64 인코딩이 올바른지 확인

---

## 📚 자세한 내용

더 자세한 배포 가이드는 `CLAUDE.md`의 "NKS 배포 파이프라인" 섹션을 참고하세요.

체크리스트는 `NKS_DEPLOYMENT_CHECKLIST.md`를 참고하세요.

---

## 💡 유용한 팁

### 로그 보기
```bash
# 최신 50줄만 보기
kubectl logs -n edu -l app=bulletin-board --tail=50

# 모든 pod의 로그 보기
kubectl logs -n edu -l app=bulletin-board -f

# 특정 pod 로그만 보기
kubectl logs -n edu <pod-name> -f
```

### Pod 접근
```bash
# Pod 내부 셸 접근
kubectl exec -it -n edu <pod-name> -- /bin/sh

# 환경 변수 확인
kubectl exec -n edu <pod-name> -- env
```

### 스케일링
```bash
# Replica 3개로 늘리기
kubectl scale deployment bulletin-board --replicas=3 -n edu

# 다시 2개로 줄이기
kubectl scale deployment bulletin-board --replicas=2 -n edu
```

### 롤백
```bash
# 이전 버전으로 되돌리기
kubectl rollout undo deployment/bulletin-board -n edu

# 배포 히스토리 보기
kubectl rollout history deployment/bulletin-board -n edu
```

---

## 🎯 배포 아키텍처

```
Your Code (GitHub main branch)
            ↓
GitHub Actions Workflow
    1. Maven 빌드 (1-2분)
    2. Docker 이미지 빌드 (1-2분)
    3. Docker Hub 푸시 (30초-1분)
            ↓
NKS Cluster (namespace: edu)
    1. ConfigMap 배포
    2. Deployment 배포 (2 replicas)
    3. LoadBalancer Service 배포
            ↓
External IP 할당 (1-2분)
            ↓
https://<external-ip>
```

---

## 🔐 보안 주의사항

⚠️ 절대 하지 말 것:
- GitHub에 `DOCKER_HUB_TOKEN` 커밋
- GitHub에 `KUBE_CONFIG` 커밋
- 개발 서버에 private 정보 노출

✅ 권장사항:
- GitHub Secrets 사용
- Docker Hub 레포지토리 비공개 설정
- 주기적으로 토큰 갱신
