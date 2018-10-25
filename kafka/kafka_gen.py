import boto3

image_id = 'ami-08182c55a1c188dee'
instance_type = 't2.micro'

def keypair_exist(name):
    """
    Check if the key pair already exist (avoid duplication)
    """
    ec2 = boto3.client('ec2')
    keypairs = ec2.describe_key_pairs()

    key_names = list(map(lambda key: key['KeyName'], keypairs['KeyPairs']))
    return name in key_names

def launch_kafka_instance(min_count, max_count):
    """
    Launch 'max_count' t2.micro instance(s) and wait until wait_until_running
    """
    ec2 = boto3.resource('ec2', region_name='eu-west-3')
    instances = ec2.create_instances(InstanceType=instance_type, ImageId=image_id, MinCount=1, MaxCount=1, KeyName='boto3-python')

    for inst in instances:
        inst.wait_until_running()
        inst.load()
        print("Instance ", inst.id, " is running, with IP = ", inst.public_ip_address)

if __name__ == '__main__':
    ec2 = boto3.client('ec2')

    # Create or use an existing key pair
    if keypair_exist("boto3-python"):
        print("Key pair 'boto3-python' already exists using it")
    else:
        print("Creating a new key pair")
        ec2.create_key_pair(KeyName='boto3-python')

    print("Creating a new instance")
    launch_kafka_instance(1, 1)

    ec2 = boto3.resource('ec2', region_name='eu-west-3')
    print("Terminating instances")
    ec2.instances.terminate()
