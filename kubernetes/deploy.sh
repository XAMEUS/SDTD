export ANSIBLE_HOST_KEY_CHECKING=False
ansible-playbook ../aws_ansible/create_instances.yml
sleep 15
ansible-playbook -i ../aws_ansible/hosts setup.yml
ansible-playbook -i ../aws_ansible/hosts master.yml
ansible-playbook -i ../aws_ansible/hosts slaves.yml
#ansible-playbook -i ../aws_ansible/hosts mongo/mongodb.yml
ansible-playbook -i ../aws_ansible/hosts kafka/kafka_ansible.yml
