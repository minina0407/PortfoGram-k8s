##set -euo pipefail

NEW_APP_NAME=spring-boot
DOCKER_HUB_USERNAME=minimeisme
DOCKER_HUB_PASSWORD=minina8275!

echo ">>>> Building & pushing Spring Boot Demo App..."
(cd spring-boot-app; docker build -t "$DOCKER_HUB_USERNAME/local-spring-portfogram:latest" .)
sudo docker login -u "$DOCKER_HUB_USERNAME" -p "$DOCKER_HUB_PASSWORD"
sudo docker push "$DOCKER_HUB_USERNAME/local-spring-portfogram:latest"

helm_install() {
local chart_name=$1
local namespace=$chart_name
if [[ -n "${2:-}" ]]; then
namespace=$2
fi
local release_name=$chart_name
if [[ -n "${3:-}" ]]; then
release_name=$3
fi
echo ">>>> Installing helm chart $chart_name into namespace $namespace as release $release_name"
kubectl create namespace "$namespace" --dry-run=client -o yaml | kubectl apply -f -
(cd "helm/$chart_name" && helm dependency update >/dev/null && helm upgrade --namespace "$namespace" --install "$release_name" .)
}
helm_install kube-prometheus-stack

helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm_install tempo
helm_install promtail
helm_install loki

helm_install spring-boot default spring-boot

echo ">>>> Waiting max 5min for deployments to finish...(you may watch progress using k9s)"
kubectl wait --for=condition=ready --timeout=5m pod -n kube-prometheus-stack -l app.kubernetes.io/name=grafana

# Setup port forward for Grafana
echo ">>>> Setting up port-forward (end with Ctrl-C), you can login to Grafana now at http://localhost:3000"
kubectl port-forward -n kube-prometheus-stack deployment/kube-prometheus-stack-grafana 3000:3000

echo ">>>> Deploying Spring Boot App to Kubernetes..."
kubectl apply -f helm/spring-boot/templates/deployment.yaml
