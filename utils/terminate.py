import boto3

ec2 = boto3.resource('ec2', region_name='eu-west-3')
ec2.instances.stop()
ec2.instances.terminate()
