#!/bin/sh

export ANSIBLE_HOST_KEY_CHECKING=False

ansible-playbook aws_ansible/create_instances.yml

ansible-playbook -i aws_ansible/hosts kubernetes/setup.yml
ansible-playbook -i aws_ansible/hosts kubernetes/master.yml
ansible-playbook -i aws_ansible/hosts kubernetes/slaves.yml

#ansible-playbook -i aws_ansible/hosts kubernetes/mongodb.yml

#ansible-playbook -i aws_ansible/hosts spark/setup.yml