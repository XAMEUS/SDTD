import os
import time
import re
import time

from subprocess import call
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

    # pem_path = input('Enter the private key path (absolute): ')
    # while not os.path.isfile(pem_path):
    #     print("Invalid path : file not found")
    #     pem_path = input('Enter the private key path (absolute): ')

    launch_kubernetes(key_name)

def launch_kubernetes(key_name):
    """
    Launch kubernetes Instances
    """
    global kafka_cmd
    instances = create_instances("eu-west-3", "t2.micro", "ami-08182c55a1c188dee", 2, 2, key_name)

    time.sleep(15)

    # Liste the ip addresses of each launched instances,
    ip_list = list()
    for inst in instances:
        ip_list.append(inst.public_ip_address)
    ip_list.sort()

    # Replace master and slaves ip in hosts file
    master_str = "\nmaster ansible_host=" + ip_list[0] + "\n"
    slaves_str = "\n"
    for i in range(1, 2):
        slaves_str += "slave" + str(i) + " ansible_host=" + ip_list[i] + "\n"

    with open(os.path.dirname(__file__) + "/../kubernetes/hosts", "r+") as f:
        new_host = re.sub(r'(?<=# BEGIN_SLAVES)((.|[\n])*)(?=# END_SLAVES)', slaves_str, f.read())
        new_host = re.sub(r'(?<=# BEGIN_MASTER)((.|[\n])*)(?=# END_MASTER)', master_str, new_host)
        f.seek(0)
        f.write(new_host)
        f.truncate()

    # Launch ansible
    os.chdir(os.path.dirname(__file__) + "/../kubernetes")
    os.environ["ANSIBLE_HOST_KEY_CHECKING"] = "False"

    call(["ansible-playbook", "-i", "hosts", "setup.yml"])
    call(["ansible-playbook", "-i", "hosts", "master.yml"])
    call(["ansible-playbook", "-i", "hosts", "slaves.yml"])
