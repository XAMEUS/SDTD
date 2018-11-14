import os
import paramiko
import time

from colors import colors
from instances import create_instances
from keys import keypair_exist

kafka_cmd = [
    'wget http://apache.mirrors.ovh.net/ftp.apache.org/dist/kafka/2.0.0/kafka_2.11-2.0.0.tgz',
    'tar -xvf kafka_2.11-2.0.0.tgz',
    'cd kafka_2.11-2.0.0',
    'bin/zookeeper-server-start.sh config/zookeeper.properties',
    'bin/kafka-server-start.sh config/server.properties'
]

def quick_deploy():

    key_name = input('Enter the key name to use: ')
    while not keypair_exist(key_name):
        print("Key name doesn't exist")
        key_name = input('Enter the key name to use: ')

    pem_path = input('Enter the private key path (absolute): ')
    while not os.path.isfile(pem_path):
        print("Invalid path : file not found")
        pem_path = input('Enter the private key path (absolute): ')

    launch_kafka(key_name, pem_path)

def install_cmd(client, commands):
    for cmd in commands:
        sstdin, stdout, stderr = client.exec_command(cmd)
        print(stdout.read().decode("utf-8"))

def launch_kafka(key_name, pem_path):
    global kafka_cmd
    instances = create_instances("eu-west-3", "t2.micro", "ami-08182c55a1c188dee", 1, 1, key_name)

    # Arbitrary waiting time for EC2 machine to launch the SSH server
    time.sleep(0)

    for i in instances:
        i.create_tags(Tags=[{'Key': 'Type', 'Value': 'Kafka'}])
        key = paramiko.RSAKey.from_private_key_file(pem_path)
        client = paramiko.SSHClient()
        client.set_missing_host_key_policy(paramiko.AutoAddPolicy())

        # Install kafka modules on the instance.
        c = 0
        while c < 3:
            print("Trying to connect to " + i.public_dns_name)
            try:
                client.connect(hostname=i.public_dns_name, username="ubuntu", pkey=key)
                install_cmd(client, kafka_cmd)
                client.close()
                break
            except Exception as e:
                print(colors.FAIL + str(e) + colors.ENDC)
                print("Retrying in a few seconds...")
                time.sleep(10)
                c += 1
