# NKS 배포 체크리스트

## 1단계: 사전 준비 (1회만 수행)

### GitHub 저장소 설정
- [ ] GitHub에 프로젝트 푸시 완료
- [ ] 저장소의 Settings → Secrets and variables 접근

### GitHub Secrets 등록
- [ ] `DOCKER_HUB_USERNAME` 추가
  ```
  값: Docker Hub 사용자명 (예: myusername)
  ```

- [ ] `DOCKER_HUB_TOKEN` 추가
  ```
  값: Docker Hub Personal Access Token
  생성: docker.io → Settings → Security → New Access Token
  권한: Read & Write
  ```

- [ ] `KUBE_CONFIG` 추가
  ```bash
  # 로컬에서 실행:
  cat ~/.kube/config | base64 -w 0
  # 출력된 전체 값을 복사하여 GitHub Secrets에 추가
  ```

### NKS 클러스터 준비
- [ ] NKS 클러스터 생성 완료
- [ ] kubectl 설정 완료 (`~/.kube/config` 존재)
- [ ] 클러스터 연결 확인
  ```bash
  kubectl cluster-info
  kubectl get nodes
  ```
- [ ] `edu` namespace 생성
  ```bash
  kubectl create namespace edu
  kubectl get namespace edu
  ```

### Docker Hub 준비
- [ ] Docker Hub 계정 생성
- [ ] `bulletin-board` 레포지토리 생성 (설정 → Visibility)
  - 비공개: 배포 보안 권장
  - 공개: 누구나 접근 가능
- [ ] Personal Access Token 생성 및 확인

---

## 2단계: 코드 수정 (배포 전 1회)

### k8s/deployment.yaml 수정
```bash
# YOUR_DOCKER_HUB_USERNAME을 실제 username으로 변경
# 예: myusername을 docker hub username으로 변경

# 편집기에서 열기:
vi k8s/deployment.yaml

# 또는 sed 사용:
sed -i '' 's/YOUR_DOCKER_HUB_USERNAME/실제_사용자명/g' k8s/deployment.yaml
```

변경 예시:
```yaml
# 변경 전:
image: YOUR_DOCKER_HUB_USERNAME/bulletin-board:latest

# 변경 후:
image: myusername/bulletin-board:latest
```

---

## 3단계: 초기 배포

### 로컬 테스트 (선택)
```bash
# Kubernetes 매니페스트 검증
kubectl apply -f k8s/ --dry-run=client -o yaml

# 실제 배포 (로컬에서 테스트)
kubectl apply -f k8s/
```

### GitHub를 통한 자동 배포
```bash
# main 브랜치에 푸시
git add .
git commit -m "[CI/CD] NKS 배포 파이프라인 구성"
git push origin main
```

### 배포 진행 상황 모니터링
1. GitHub 저장소 → Actions 탭 열기
2. "Build and Deploy to NKS" 워크플로우 확인
3. 각 단계 로그 확인:
   - ✓ Checkout code
   - ✓ Set up JDK 17
   - ✓ Build with Maven
   - ✓ Log in to Docker Hub
   - ✓ Build and push Docker image
   - ✓ Configure kubectl
   - ✓ Apply Kubernetes manifests
   - ✓ Verify deployment

### 배포 완료 확인
```bash
# Pod 상태 확인 (Running 상태여야 함)
kubectl get pods -n edu -l app=bulletin-board
# NAME                               READY   STATUS    RESTARTS   AGE
# bulletin-board-5d4f5b7c8d-abc12    1/1     Running   0          2m
# bulletin-board-5d4f5b7c8d-def45    1/1     Running   0          2m

# Service 외부 IP 확인
kubectl get svc bulletin-board -n edu
# NAME              TYPE           CLUSTER-IP      EXTERNAL-IP     PORT(S)
# bulletin-board    LoadBalancer   10.0.1.100      203.110.xxx.xxx 80:30123/TCP

# 애플리케이션 접근 테스트
curl http://<EXTERNAL-IP>
# 또는 브라우저에서 http://<EXTERNAL-IP> 접속
```

---

## 4단계: 이후 배포 (매번)

### 코드 변경 후 배포
```bash
# 1. 코드 수정 및 커밋
git add .
git commit -m "[기능] 설명"

# 2. main 브랜치에 푸시
git push origin main

# 3. GitHub Actions 자동 실행
# → Docker 이미지 빌드
# → Docker Hub에 푸시
# → NKS에 롤아웃 (자동 재배포)
```

### 배포 상태 모니터링
```bash
# 실시간 배포 상태 확인
kubectl rollout status deployment/bulletin-board -n edu --timeout=5m

# 로그 확인
kubectl logs -n edu -l app=bulletin-board -f

# 최신 이미지 확인
kubectl get deployment bulletin-board -n edu -o jsonpath='{.spec.template.spec.containers[0].image}'
```

---

## 5단계: 트러블슈팅

### 배포 실패 시 확인 항목
- [ ] GitHub Actions 워크플로우 로그 확인
- [ ] `DOCKER_HUB_TOKEN` 유효성 확인 (만료되지 않았는지)
- [ ] `KUBE_CONFIG` 유효성 확인 (base64 올바르게 인코딩되었는지)
- [ ] Docker Hub 레포지토리에 이미지가 푸시되었는지 확인

### Pod 에러 확인
```bash
# Pod 상세 정보
kubectl describe pod -n edu <pod-name>

# 로그 확인
kubectl logs -n edu <pod-name> -p  # 이전 로그
kubectl logs -n edu <pod-name>     # 현재 로그

# 이벤트 확인
kubectl get events -n edu --sort-by='.lastTimestamp'
```

### 롤백
```bash
# 이전 버전으로 롤백
kubectl rollout undo deployment/bulletin-board -n edu

# 롤백 상태 확인
kubectl rollout status deployment/bulletin-board -n edu
```

---

## 참고: 주요 명령어

```bash
# 배포 상태 확인
kubectl get all -n edu

# Pod 자세히 보기
kubectl get pods -n edu -o wide

# 특정 Pod 로그 따라가기
kubectl logs -n edu -l app=bulletin-board -f --tail=50

# 환경 변수 확인
kubectl exec -n edu <pod-name> -- env | grep SPRING

# Pod 재시작
kubectl rollout restart deployment/bulletin-board -n edu

# 커스텀 이미지로 배포
kubectl set image deployment/bulletin-board \
  bulletin-board=myusername/bulletin-board:v2.0.0 \
  -n edu

# 리소스 스케일링
kubectl scale deployment bulletin-board --replicas=3 -n edu
```

---

## 예상 배포 시간

- Maven 빌드: ~1-2분
- Docker 이미지 빌드: ~1-2분
- Docker Hub 푸시: ~30초-1분
- NKS 배포: ~1-2분
- **총 소요 시간: 약 4-7분**

---

## 보안 관련 주의사항

- [ ] `DOCKER_HUB_TOKEN`을 절대 repository에 커밋하지 말 것
- [ ] `KUBE_CONFIG`도 절대 repository에 커밋하지 말 것 (GitHub Secrets만 사용)
- [ ] Docker Hub 레포지토리를 비공개로 설정 권장
- [ ] 정기적으로 Personal Access Token 갱신

---

## 다음 단계

배포 후 추가 고려사항:
- [ ] 모니터링 (Prometheus, Grafana)
- [ ] 로깅 (ELK, Loki)
- [ ] 오토스케일링 설정 (HPA)
- [ ] Ingress 설정 (도메인 바인딩)
- [ ] TLS/SSL 인증서 설정
