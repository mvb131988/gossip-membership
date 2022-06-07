@echo off

CALL mvn assembly:assembly -DskipTests
xcopy .\target\gossip-membership.jar . /Y

docker build -t membership -f Dockerfile-app .

kubectl delete deployment membership1-deployment
kubectl delete deployment membership2-deployment
kubectl delete deployment membership3-deployment
kubectl delete deployment membership4-deployment

kubectl apply -f chart\membership1-deployment.yaml
kubectl apply -f chart\membership2-deployment.yaml
kubectl apply -f chart\membership3-deployment.yaml
kubectl apply -f chart\membership4-deployment.yaml