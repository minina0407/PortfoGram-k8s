# 배포 전략 — PortfoGram 멀티 서비스 구성

## 개요

실무에서는 단 하나의 서비스만 배포되지 않는다. 그리고 모든 서비스가 동일한 방식으로 배포되지도 않는다.
사용자가 직접 접근하는 API와, 내부 인증 서비스와, 비동기 메시지 워커는 각각 서비스 특성이 다르기 때문에
배포 방식도 달라야 한다.

이 문서는 PortfoGram을 4개 서비스로 분리하고, 각 서비스에 다른 배포 전략을 적용한 이유와 구현 방법을 정리한다.

---

## 왜 서비스를 나눴는가

목적은 MSA 아키텍처 전환이 아니다.
**플랫폼 운영 관점에서 서비스별로 다른 배포 전략을 검증하는 환경**을 만드는 것이 목적이다.

기존 모놀리식 구조에서는 포트폴리오 조회, 로그인, 채팅, 배치 작업이 하나의 프로세스에 묶여 있었다.
이 경우 "사용자 트래픽 API만 Blue/Green으로 배포하고 싶다"는 요구사항을 구현할 수가 없다.
서비스를 분리해야 서비스 특성에 맞는 배포 전략을 독립적으로 선택할 수 있다.

---

## 서비스 구조

```
PortfoGram-k8s/              # app repo (Gradle multi-module)
├── common/                  # 공유 라이브러리 (auth, user entity, exception 등)
├── portfogram-api/          # 포트폴리오/댓글/이미지 API
├── portfogram-auth/         # 인증/사용자 관리
├── portfogram-chat/         # 채팅 메시지 워커
└── portfogram-batch/        # 통계 집계 배치
```

각 서비스는 `common` 모듈에 의존하며, 독립적인 Dockerfile과 GitHub Actions 워크플로우를 가진다.

---

## 배포 전략 매핑

| 서비스 | 전략 | 도구 | 이유 |
|---|---|---|---|
| portfogram-api | **Blue/Green** | Argo Rollouts | 사용자 직접 접근 → 트래픽 전환 전 preview 환경에서 검증 필요 |
| portfogram-auth | **Rolling Update** | Kubernetes Deployment | JWT 무상태 서비스 → 빠른 배포, maxSurge/maxUnavailable 명시 |
| portfogram-chat | **Canary** | Argo Rollouts | 메시지 처리 오류를 소량 트래픽에서 먼저 감지 후 점진 전환 |
| portfogram-batch | **Build-only** | GitHub Actions | 상시 실행 불필요 → 이미지만 빌드, 필요 시 k8s Job으로 실행 |

---

## 1. portfogram-api — Blue/Green

### 전략 설명

두 개의 환경(Blue = 현재 버전, Green = 신규 버전)을 동시에 운영하고,
검증이 완료되면 한 번에 트래픽을 전환한다.

```
사용자 요청 → active Service → Blue (현재)
                               Green (신규) ← preview Service (QA/검증용)

           → 승인 후 → active Service → Green
                       Blue 스케일다운
```

### 왜 이 서비스에 Blue/Green인가

포트폴리오 조회, 댓글 작성, 이미지 업로드 등 사용자가 직접 접근하는 API다.
배포 중 오류가 발생했을 때 사용자가 즉시 영향을 받는다.

Blue/Green은 신규 버전을 preview 서비스로 먼저 배포해 내부 테스트를 마친 뒤
트래픽을 전환하기 때문에, 사용자 영향 없이 검증 시간을 확보할 수 있다.
문제 발생 시 롤백은 active Service의 셀렉터를 되돌리는 것으로 즉시 가능하다.

### 핵심 설정

```yaml
# values-portfogram-api-dev.yaml
strategy:
  type: blueGreen

rollout:
  blueGreen:
    autoPromotionEnabled: false   # 수동 승인 후 트래픽 전환
    scaleDownDelaySeconds: 30     # 전환 후 30초 대기 후 구버전 종료
```

`autoPromotionEnabled: false`가 핵심이다. ArgoCD Rollouts의 대시보드 또는 CLI로 직접 승인해야 트래픽이 전환된다.

---

## 2. portfogram-auth — Rolling Update

### 전략 설명

기존 Pod를 하나씩 새 버전으로 교체한다.
`maxSurge`와 `maxUnavailable`로 동시에 올릴 수 있는 Pod 수와 줄일 수 있는 Pod 수를 제어한다.

```
maxSurge: 1, maxUnavailable: 0

Step 1: [v1, v1, v1] + [v2] 추가 → [v1, v1, v1, v2]
Step 2: v1 하나 종료 → [v1, v1, v2]
Step 3: [v1, v1, v2] + [v2] 추가 → [v1, v1, v2, v2]
Step 4: v1 하나 종료 → [v1, v2, v2]
...
완료: [v2, v2, v2]
```

### 왜 이 서비스에 Rolling Update인가

JWT 기반 인증은 상태가 없다. 어떤 Pod에서 토큰을 검증하든 결과가 같다.
서비스 간 세션 동기화가 필요 없기 때문에 기존 Pod와 새 Pod가 동시에 트래픽을 받아도 문제가 없다.

이전 모놀리식 구조에서는 `maxSurge`, `maxUnavailable`을 기본값으로 사용했다.
기본값(`maxSurge: 25%`, `maxUnavailable: 25%`)은 레플리카 수가 적을 때 의도치 않게 서비스가 중단될 수 있다.
레플리카가 2개인 경우 `maxUnavailable: 25%`는 반올림되어 1이 되는데,
동시에 `maxSurge: 25%`도 반올림되어 1이 되면 실제로는 `[1개 감소 후 1개 추가]`로 동작한다.

명시적으로 `maxUnavailable: 0`으로 설정해 **어느 시점에도 가용한 Pod 수가 줄지 않음을 보장**한다.

### 핵심 설정

```yaml
# values-portfogram-auth-dev.yaml
strategy:
  type: rollingUpdate
  rollingUpdate:
    maxSurge: 1       # 동시에 추가할 수 있는 Pod 수
    maxUnavailable: 0 # 동시에 줄일 수 있는 Pod 수 (0 = 완전 무중단)
```

---

## 3. portfogram-chat — Canary

### 전략 설명

신규 버전을 전체 트래픽의 일부(20%)에만 먼저 노출하고,
문제가 없으면 비율을 단계적으로 늘린다.

```
Step 1: 트래픽 20% → v2, 80% → v1
Step 2: 30초 대기 (모니터링)
Step 3: 트래픽 50% → v2, 50% → v1
Step 4: 30초 대기 (모니터링)
Step 5: 트래픽 100% → v2
```

### 왜 이 서비스에 Canary인가

RabbitMQ 큐에서 메시지를 소비하는 워커 서비스다.
메시지 처리 로직의 버그는 메시지 유실이나 중복 처리로 이어질 수 있는데,
이런 문제는 소량 트래픽에서는 빠르게 감지되지만 전체 전환 후에는 복구가 어렵다.

Canary를 통해 20% 메시지만 신규 버전으로 라우팅해서 에러율, 처리 지연을 확인한 후 점진적으로 늘린다.

### 핵심 설정

```yaml
# values-portfogram-chat-dev.yaml
strategy:
  type: canary

rollout:
  canary:
    steps:
      - setWeight: 20
      - pause:
          duration: 30s
      - setWeight: 50
      - pause:
          duration: 30s
      # 이후 자동으로 100%
```

---

## 4. portfogram-batch — Build-only

### 전략 설명

이미지를 빌드하고 Docker Hub에 푸시하는 것까지만 수행한다.
manifest repo에 태그를 커밋하지 않기 때문에 ArgoCD가 sync를 트리거하지 않는다.
클러스터에 상시 실행되는 Deployment가 없다.

필요할 때 k8s Job 또는 CronJob으로 실행한다.

```bash
# 수동 실행 예시
kubectl create job portfolio-stats-$(date +%Y%m%d) \
  --image=minimeisme/portfogram-batch:SHA \
  -n portfogram \
  -- java -javaagent:/opentelemetry-javaagent.jar -jar /app.jar \
     --spring.batch.job.names=portfolioStatsSummaryJob
```

### 왜 이 서비스에 Build-only인가

통계 집계, 데이터 정리 등 배치 작업은 항상 실행될 필요가 없다.
24시간 Pod를 유지하는 것은 컴퓨팅 낭비다.

Build-only 전략은 이미지만 최신 상태로 유지하고,
실행은 스케줄이나 운영자 판단에 맡긴다.
이미지 태그(SHA)를 관리하면 특정 버전을 언제든 실행하거나 롤백할 수 있다.

### CI 워크플로우에서 manifest 업데이트 단계가 없음

```yaml
# portfogram-batch.yml — 다른 서비스 워크플로우와의 차이점
jobs:
  build:
    steps:
      - checkout
      - setup buildx
      - docker login
      - build and push        ← 여기까지만
      # manifest repo checkout 없음
      # values 파일 수정 없음
      # git push 없음
```

---

## CI/CD 파이프라인 구조

### GitOps 흐름

```
[개발자]
   │  git push (main)
   ▼
[GitHub Actions]
   │  1. path filter로 변경된 서비스만 트리거
   │  2. Docker 이미지 빌드 & 푸시
   │  3. manifest repo의 values 파일에 새 태그 커밋 (build-only 제외)
   ▼
[portfogram-manifest repo]
   │  values-portfogram-api-dev.yaml: tag: "abc1234"
   ▼
[ArgoCD]
   │  manifest repo 변경 감지 (polling / webhook)
   │  Helm 렌더링 → k8s 리소스 비교
   │  sync → Kubernetes 적용
   ▼
[Kubernetes]
   Blue/Green / Rolling Update / Canary 전략으로 배포
```

### ArgoCD는 이벤트를 "수신"하지 않는다

기존 구조에서는 `repository_dispatch`로 manifest repo 워크플로우를 트리거했다.
이 방식은 두 repo의 워크플로우 간 의존성을 만들고, 오류 추적을 어렵게 한다.

변경된 구조에서는 **CI가 manifest repo에 직접 커밋**한다.
ArgoCD는 이 저장소를 주기적으로 폴링(기본 3분)하거나 webhook으로 변경을 감지해 sync를 수행한다.

```
이전: app-repo CI → repository_dispatch → manifest-repo CI → git push
이후: app-repo CI → git push to manifest-repo (직접)
```

의존 관계가 줄고, 배포 이력이 manifest repo의 git log 한 곳에 남는다.

### 서비스별 path filter

```yaml
on:
  push:
    branches: [main]
    paths:
      - 'portfogram-api/**'
      - 'common/**'      # 공유 코드 변경도 트리거
```

`common/` 변경은 모든 서비스에 영향을 주기 때문에 4개 워크플로우 모두 `common/**`를 포함한다.

---

## manifest repo 변경사항

### Helm 차트 (apps/spring-boot/)

기존 차트는 Argo Rollouts의 Blue/Green만 지원했다.
`strategy.type` 값으로 세 가지 전략을 선택할 수 있도록 `deployment.yaml` 템플릿을 수정했다.

```yaml
# deployment.yaml (핵심 로직)
{{- if eq .Values.strategy.type "rollingUpdate" }}
apiVersion: apps/v1
kind: Deployment
{{- else }}
apiVersion: argoproj.io/v1alpha1
kind: Rollout
{{- end }}
...
  strategy:
    {{- if eq .Values.strategy.type "rollingUpdate" }}
    type: RollingUpdate
    rollingUpdate:
      maxSurge: {{ .Values.strategy.rollingUpdate.maxSurge }}
      maxUnavailable: {{ .Values.strategy.rollingUpdate.maxUnavailable }}
    {{- else if eq .Values.strategy.type "blueGreen" }}
    blueGreen:
      ...
    {{- else if eq .Values.strategy.type "canary" }}
    canary:
      steps: ...
    {{- end }}
```

서비스마다 다른 values 파일을 사용한다:

| values 파일 | 이미지 | 전략 |
|---|---|---|
| `values-portfogram-api-dev.yaml` | `minimeisme/portfogram-api` | blueGreen |
| `values-portfogram-auth-dev.yaml` | `minimeisme/portfogram-auth` | rollingUpdate |
| `values-portfogram-chat-dev.yaml` | `minimeisme/portfogram-chat` | canary |

### ArgoCD Application (argocd/applications/)

```
argocd/applications/
├── portfogram-api.yaml    # ← 신규 (spring-boot.yaml 대체)
├── portfogram-auth.yaml   # ← 신규
├── portfogram-chat.yaml   # ← 신규
└── kustomization.yaml     # spring-boot → 3개 Application으로 교체
```

각 Application은 동일한 Helm 차트(`apps/spring-boot`)를 참조하되, 서비스별 values 파일을 지정한다:

```yaml
# portfogram-api.yaml
source:
  helm:
    valueFiles:
      - values-portfogram-api-dev.yaml
  path: apps/spring-boot
```

---

## 다음 단계

**ApplicationSet 도입**

서비스가 3개가 되면 ArgoCD Application을 각각 관리하는 것이 번거롭다.
ApplicationSet의 List Generator를 사용하면 서비스 목록만 정의해서 Application을 일괄 생성할 수 있다.

```yaml
apiVersion: argoproj.io/v1alpha1
kind: ApplicationSet
spec:
  generators:
    - list:
        elements:
          - service: portfogram-api
            strategy: blueGreen
          - service: portfogram-auth
            strategy: rollingUpdate
          - service: portfogram-chat
            strategy: canary
  template:
    metadata:
      name: '{{service}}'
    spec:
      source:
        helm:
          valueFiles:
            - 'values-{{service}}-dev.yaml'
```

**portfogram-charts 분리**

공유 Helm 차트의 소비자가 3개(api/auth/chat)가 됐다.
공유 차트를 별도 저장소로 분리하고, ArgoCD Application에서 별도 repoURL로 참조하는 것이 자연스러운 다음 단계다.
