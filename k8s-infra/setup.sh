echo 'setting up k8s infra, assuming kubectl is configured to point at a valid cluster'
echo 'has been tested using minikube'

echo switching to specific user
su cornelius

# check if kubectl exists
kubectl version --client --short || exit

# change dir and check if dashboard file exists, download if necessary
cd manifests/dashboard || exit
if [ ! -f /dashboard.yaml ]; then
    echo " dashboard k8s File not found! -> downloading it"
    touch dashboard.yaml
    echo "# k8s dashboard file" > dashboard.yaml
    git add dashboard.yaml
    curl https://raw.githubusercontent.com/kubernetes/dashboard/v2.0.0/aio/deploy/recommended.yaml > dashboard.yaml
fi
echo 'applying dashboard to k8s'
kubectl apply -f dashboard.yaml

echo "applying further manifests"
