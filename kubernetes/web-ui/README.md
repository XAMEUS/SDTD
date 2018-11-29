Sur le master :
```bash
# doc: https://kubernetes.io/docs/tasks/access-application-cluster/web-ui-dashboard/
# deploy ui
kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/master/src/deploy/alternative/kubernetes-dashboard.yaml
# run proxy
kubectl proxy --address 0.0.0.0 --accept-hosts '.*'
# see roles
kubectl -n kube-system describe role kubernetes-dashboard-minimal
# give permissions
kubectl create -f dashboard-admin.yml
# view cluster-ip
kubectl -n kube-system get service kubernetes-dashboard
```

Depuis son ordinateur, faire un bridge :
```
ssh -D 9999 -C ubuntu@public_master_ip -i key.pem
```
Ensuite configurer le proxy dans firefox
Hôte SOCK: 127.0.0.1, PORT: 9999
Se connecter à l'adresse ip du cluster.
