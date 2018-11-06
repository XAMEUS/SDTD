import boto3

from colors import colors
from keys import keypair_exist

# Default settings
region_name = 'eu-west-3'
InstanceType = 't2.micro'
ImageId = 'ami-08182c55a1c188dee' # Ubuntu server 18.04
MinCount = 1
MaxCount = 1

valid_regions = ["", "eu-west-1", "eu-west-2", "eu-west-3"]
valid_instances = ["", "t2.micro"]

def call_choice_instance(choice_input):
    """
    Call the right script depending on the user's choice
    """

    if choice_input == "1":
        setup_launch_instance()
    elif choice_input == "2":
        list_instances()
    elif choice_input == "3":
        terminate_all()

def terminate_all():
    """
    Terminate all instances
    """
    print("Terminating all EC2 instances in eu-west-3")
    ec2 = boto3.resource('ec2', region_name='eu-west-3')

    for i in ec2.instances.all():
        if i.state['Name'] != "terminated":
            i.stop()
            i.terminate()

def create_instances(region_name, instance_type, image_id, min, max, key_name):
    """
    Create an instance
    """
    ec2 = boto3.resource('ec2', region_name=region_name)
    instances = ec2.create_instances(InstanceType=instance_type, ImageId=image_id, MinCount=min, MaxCount=max, KeyName=key_name)

    # Wait until instances are in running state
    for i in instances:
        i.wait_until_running()
        i.load()
        print(colors.OKGREEN, i, "is running..." + colors.ENDC)

    return instances

def setup_launch_instance():
    """
    Prompt for instance creation
    """
    global region_name, InstanceType, ImageId, MinCount, MaxCount, valid_regions, valid_instances
    # Ask for region
    r = input("Enter region's name [default: " + region_name + "]: ")
    while not (r in valid_regions):
        r = input("Enter region's name [default: " + region_name + "]: ")
    if r != "":
        region_name = r

    # Ask for instance type
    r = input('Enter instance type [default: ' + InstanceType + ']: ')
    while not (r in valid_instances):
        r = input('Enter instance type [default: ' + InstanceType + ']: ')
    if r != "":
        InstanceType = r

    # Ask for AMI image id
    r = input('Enter AMI imageId [default: '+ImageId+']: ')
    if r != "":
        ImageId = r

    # Ask for the number of instances to create
    r = input('Enter number of instances to create [default: 1]: ')
    if r != "":
        MinCount = int(r)
        MaxCount = int(r)

    # Ask for key name to use
    key_name = input('Enter the key name to use: ')
    while not keypair_exist(key_name):
        key_name = input('Enter the key name to use: ')

    create_instances(region_name, InstanceType, ImageId, MinCount, MaxCount, key_name)

def list_instances():
    """
    List all active Instances
    """
    ec2 = boto3.client('ec2')
    reservations = ec2.describe_instances()

    c = 0

    for reservation in reservations['Reservations']:
        for instance in reservation['Instances']:
            print(colors.HEADER + "instanceId:", instance['InstanceId'] + colors.ENDC)
            if 'Tags' in instance:
                print('\ttags:')
                for tag in instance['Tags']:
                    print('\t\t' + tag['Key'] + ':', tag['Value'])
            print("\ttype: " + instance['InstanceType'])
            print("\tstate:", instance['State']['Name'] + ',', 'Code:', instance['State']['Code'])
            for interface in instance['NetworkInterfaces']:
                print('\t' + interface['NetworkInterfaceId'] + ':', interface['MacAddress'], )
                for ip in interface['PrivateIpAddresses']:
                    association = ip['Association']
                    if 'PrivateIpAddress' in ip:
                        privateIpAddress = ip['PrivateIpAddress']
                        print("\t\tprivate ip: " + colors.OKBLUE + privateIpAddress + colors.ENDC)
                    if 'PrivateDnsName' in ip:
                        privateDnsName = ip['PrivateDnsName']
                        print("\t\tprivate dns name: " + colors.OKBLUE + privateDnsName + colors.ENDC)
                    if association:
                        if 'PublicIp' in association:
                            publicIp = association['PublicIp']
                            print("\t\tpublic ip: " + colors.OKBLUE + publicIp + colors.ENDC)
                        if 'PublicDnsName' in association:
                            publicDnsName = association['PublicDnsName']
                            print("\t\tpublic dns name: " + colors.OKBLUE + publicDnsName + colors.ENDC)
                print("\t\tsubnet: " + colors.OKBLUE + interface['SubnetId'] + colors.ENDC)
            c += 1

    print('total', c)
