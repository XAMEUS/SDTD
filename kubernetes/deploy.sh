ansible-playbook ../aws_ansible/create_instances.yml
sleep 15
ansible-playbook -i ../aws_ansible/hosts setup.yml
ansible-playbook -i ../aws_ansible/hosts master.yml
ansible-playbook -i ../aws_ansible/hosts slaves.yml
ansible-playbook -i ../aws_ansible/hosts mongodb.yml
ansible-playbook -i ../aws_ansible/hosts kafka_ansible.yml
