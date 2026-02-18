- First pushing the docker image to docker hub

<img width="1747" height="352" alt="image" src="https://github.com/user-attachments/assets/6fdcede6-1f59-40a4-b251-c05c5838b344" />

- And adding this in deployment file of backend, to automattically pull the image from docker hub


- clone the repo inside ec2 instance : ```git clone https://github.com/sarthak0401/SB-ECommerice-FullStack-WebApplication.git```


- Created ec2 instance to install k8s on it via k3s 

<img width="1867" height="945" alt="image" src="https://github.com/user-attachments/assets/309b47b2-7a59-43b3-9612-6fa287806aca" />
<img width="1579" height="374" alt="image" src="https://github.com/user-attachments/assets/7a6e457b-2b06-406d-9ad6-f3dd49763aa3" />



- ssh into the instance

<img width="1575" height="854" alt="image" src="https://github.com/user-attachments/assets/aea9ea0c-b9e2-4f8b-bcc8-84ccec5afa02" />


- Installing docker, kubernetes into the instance

```
sudo apt update
sudo apt install -y docker.io
sudo usermod -aG docker ubuntu
newgrp docker
```

- Installing k3s to create a kubernetes cluster
```
curl -sfL https://get.k3s.io | sh -
mkdir -p $HOME/.kube
sudo cp /etc/rancher/k3s/k3s.yaml $HOME/.kube/config
sudo chown $USER:$USER $HOME/.kube/config
unset KUBECONFIG
sudo chmod 644 /etc/rancher/k3s/k3s.yaml
```

- Verify that the nodes is created using this command
```kubectl get nodes```


- Installing helm for prometheus operator
```curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash```

- Installing prometheus agent + grafana using helm in monitoring namespace
```
kubectl create namespace monitoring  
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update

helm install monitoring prometheus-community/kube-prometheus-stack \
  -n monitoring --create-namespace \
  -f SB-ECommerice-FullStack-WebApplication/k8s/monitoring/values.yaml


cd SB-ECommerice-FullStack-WebApplication
kubectl apply -f k8s/
```
- This will apply all the definition files for k8s, that we have created



- Now we can see the pods and the services in default and monitoring namespace

<img width="1575" height="817" alt="image" src="https://github.com/user-attachments/assets/d29cfc8a-951f-4383-b73e-de9b69787138" />



- We could access the grafana using the public ip of the instance and the port 32000, as this is what we defined in the values.yaml, to fix the nodePort of the grafana, and likewise prometheus as well

<img width="1886" height="1050" alt="image" src="https://github.com/user-attachments/assets/f0c0e570-f002-4144-aaa5-6684f371955a" />

login using credentials :
username = admin
password = get it from kubectl get secret -n monitoring monitoring-grafana -o jsonpath="{.data.admin-password}" | base64 -d

- And prometheus available at port number 32001

<img width="1886" height="1050" alt="image" src="https://github.com/user-attachments/assets/e37efb26-00d9-4972-829b-6bec77f78a0e" />


- We can see the metrics are exposed at the endpoint `http://<public-ip-instance>:30471/actuator/prometheus`

<img width="1886" height="1050" alt="image" src="https://github.com/user-attachments/assets/4f1c767b-83c8-4198-880f-06dadffa1434" />


- Now we can add the prometheus data source and build the dashboard

<img width="1886" height="1050" alt="image" src="https://github.com/user-attachments/assets/047a01ef-a2e7-40f1-99a5-abf8441c5cdf" />

- Also we can see here we can get the graph created for custom metrics that we created such as login failure, login success, order failure, order success

- We can access the backend using port 30471

<img width="1914" height="1049" alt="image" src="https://github.com/user-attachments/assets/e739350a-47f7-4e8e-9ff4-3a8e4c1a79ff" />
<img width="1914" height="1049" alt="image" src="https://github.com/user-attachments/assets/d8cae97a-7401-4d3d-81b5-2ef338b85c1c" />
<img width="1914" height="1049" alt="image" src="https://github.com/user-attachments/assets/6c2e16c6-ef2c-4f62-a085-e9c3a98765be" />
<img width="1914" height="1049" alt="image" src="https://github.com/user-attachments/assets/6366546e-290b-4847-9220-e901c6056e6a" />
<img width="1914" height="1049" alt="image" src="https://github.com/user-attachments/assets/2aeb783f-2c6e-4bbd-8533-af17867306ae" />
<img width="1914" height="1049" alt="image" src="https://github.com/user-attachments/assets/74466a4a-df90-4745-8877-c9353205576a" />

- This way we can create the order using the public ip of the ec2-instance

- NOTE : Frontend have to pass the idempotency key, to ensure that we are not processing the same duplicate request, since Idempotency happens at the client side

<img width="1350" height="308" alt="image" src="https://github.com/user-attachments/assets/b8cd286e-462e-441c-8119-e944744cb9b1" />


- We can see the count of order is 1.

<img width="1196" height="311" alt="image" src="https://github.com/user-attachments/assets/39b99ed4-fc90-4445-a187-8aa188e3f535" /> 

- Even if we hit the same order again and again, its giving me the same response. Not creating duplicate order, because of the same idempotency key

- Also we have redis limiting set

<img width="1918" height="1015" alt="image" src="https://github.com/user-attachments/assets/9c61c780-45c3-4e2f-bc1c-910631417068" />

- We can see if same ip tries to hit the login url more than 5 times in a minute we get this response 429 Too many requests

- Also when we called multiple signin api request with wrong credentials, we can see there is a spike in the login failure rate

<img width="1867" height="1008" alt="image" src="https://github.com/user-attachments/assets/740199f2-a6da-42d1-82d9-9f389fe09cf3" />



