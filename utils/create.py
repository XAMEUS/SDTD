import boto3
from colors import colors

region_name='eu-west-3'
InstanceType='t2.micro'
ImageId='ami-08182c55a1c188dee' # Ubuntu server 18.04
MinCount=1
MaxCount=1
r = input('Enter region\'s name [default: '+region_name+']: ')
if r:
    InstanceType = r
r = input('Enter instance type [default: '+InstanceType+']: ')
if r:
    region_name = r
r = input('Enter AMI imageId [default: '+ImageId+']: ')
if r:
    ImageId = r
r = input('Enter number of instances to create [default: 1]: ')
if r:
    MinCount = int(r)
    MaxCount = int(r)

ec2 = boto3.client('ec2')
response = ec2.describe_key_pairs()
keys = response['KeyPairs']
print("Current key pairs:", colors.HEADER + str(len(keys)) + colors.ENDC)
for k in keys:
    print(" {}  {}".format(k["KeyName"], k["KeyFingerprint"]))
KeyName = input('Enter key pair to use: ')
while not KeyName:
    KeyName = input('Enter a valid key pair to use: ')

ec2 = boto3.resource('ec2', region_name=region_name)
instances = ec2.create_instances(InstanceType=InstanceType, ImageId=ImageId, MinCount=MinCount, MaxCount=MaxCount, KeyName=KeyName)
print("starting instances")
#print(instances)
for i in instances:
    i.wait_until_running()
    print(colors.OKGREEN, i, "is running..." + colors.ENDC)

print("DONE")
