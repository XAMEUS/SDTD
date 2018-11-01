import boto3

from colors import colors
from key import keypair_exist

# Default settings
region_name = 'eu-west-3'
InstanceType = 't2.micro'
ImageId = 'ami-08182c55a1c188dee' # Ubuntu server 18.04
MinCount = 1
MaxCount = 1

valid_regions = ["eu-west-1", "eu-west-2", "eu-west-3"]
valid_instances = ["t2.micro"]

def launch_create_instance():
    print()
    print("-- Instance manager --")
    print("[1] Launch a new instance")
    print("[2] List instances")
    call_choice(input("Your choice ? "))

def call_choice(choice_input):
    """
    Call the right script depending on the user's choice
    """
    if choice_input != "1" and choice_input != "2" and choice_input != "3":
        launch_key_setup()

    if choice_input == "1":
        pass
    elif choice_input == "2":
        pass

def create_instances(region_name, instance_type, image_id, min, max, key_name):
    ec2 = boto3.resource('ec2', region_name=region_name)
    instances = ec2.create_instances(InstanceType=InstanceType, ImageId=ImageId, MinCount=MinCount, MaxCount=MaxCount, KeyName=KeyName)

def setup_launch_instance():
    # Ask for region
    r = input("Enter region's name [default: " + region_name + "]: ")
    while not r or r in valid_regions:
        r = input("Enter region's name [default: " + region_name + "]: ")
    if r:
        region_name = r

    # Ask for instance type
    r = input('Enter instance type [default: ' + InstanceType + ']: ')
    while not r or r in valid_instances:
        r = input('Enter instance type [default: ' + InstanceType + ']: ')
    if r:
        InstanceType = r

    # Ask for AMI image id
    r = input('Enter AMI imageId [default: '+ImageId+']: ')
    if r:
        ImageId = r

    # Ask for the number of instances to create
    r = input('Enter number of instances to create [default: 1]: ')
    if r:
        MinCount = int(r)
        MaxCount = int(r)

    # Ask for key name to use
    key_name = input('Enter the key name to use: ')
    while not keypair_exist(r):
        key_name = input('Enter the key name to use: ')

    create_instances(region_name, InstanceType, ImageId, MinCount, MaxCount, key_name)


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
