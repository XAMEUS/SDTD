#!/bin/bash

HOST=$(ansible-inventory -i ./aws_ansible/hosts --host master1 | grep ansible_host | grep -oE '[0-9\.]+')
KEY=$(cat ./aws_ansible/hosts | grep ansible_ssh_private_key_file | cut -d = -f 2)
USER=$(cat ./aws_ansible/hosts | grep ansible_user | cut -d = -f 2)

echo ssh -NL 8001:127.0.0.1:8001 -i "$KEY" $USER@$HOST

ssh -NL 8001:127.0.0.1:8001 -i "$KEY" $USER@$HOST
