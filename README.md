# SDTD
## requirements
- ansible 2.7.0
- libaraies python:
  - boto3

You need to have your AWS credentials present in ~/.aws/credentials


## deployment
The deployment of the infrastructure and the application can be done with the following command:
./runall.sh

## populate the database (for top)
Connect to the kubernetes master
./ssh_master

Once connected to the master, connect to web-server pod
kubectl exec -it web-server bash

Once connected to the pod run :
python3 /var/www/html/analyze.py 2017-01-01 2019-01-01
(it may take a while)

## access the website
Open a ssh tunnel with the website in order to access it
./tunnel_website

Then using your favorite web browser, go to :
http://localhost:8002

## removal of the infrastructure
./cleanall
