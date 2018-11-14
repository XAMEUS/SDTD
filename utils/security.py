import boto3
from botocore.exceptions import ClientError
from colors import colors

ec2 = boto3.client('ec2')

def call_choice_security(choice_input):
    """
    Call the right script depending on the user's choice
    """

    if choice_input == "1":
        list_security_groups()
    elif choice_input == "2":
        setup_create_security_group()

def list_security_groups():
    try:
        response = ec2.describe_security_groups() #GroupIds=['sg-7e87a516']
        for security in response['SecurityGroups']:
            # print(security)
            print(colors.HEADER + "GroupId:", security['GroupId'] + colors.ENDC)
            print('\t' + "VpcId:", security['VpcId'])
            if 'Tags' in security:
                print('\ttags:')
                for tag in security['Tags']:
                    print('\t\t' + tag['Key'] + ':', tag['Value'])
            if 'Description' in security:
                print('\tdescription: ' + security['Description'])
            print('\tin:')
            for ip in security['IpPermissions']:
                protocol = ip['IpProtocol']
                if protocol == '-1':
                    protocol = 'all'
                for i in ip['IpRanges']:
                    if 'FromPort' in ip:
                        print('\t\t', colors.HEADER + i['CidrIp'] + ':' + str(ip['FromPort']), colors.OKBLUE + protocol + colors.ENDC)
                    else:
                        print('\t\t', colors.HEADER + i['CidrIp'], colors.OKBLUE + protocol + colors.ENDC)
            print('\tout:')
            for ip in security['IpPermissionsEgress']:
                protocol = ip['IpProtocol']
                if protocol == '-1':
                    protocol = 'all'
                for i in ip['IpRanges']:
                    if 'FromPort' in ip:
                        print('\t\t', colors.HEADER + i['CidrIp'] + ':' + str(ip['FromPort']), colors.OKBLUE + protocol + colors.ENDC)
                    else:
                        print('\t\t', colors.HEADER + i['CidrIp'], colors.OKBLUE + protocol + colors.ENDC)
    except ClientError as e:
        print(e)

# print("Do you want to create a new security group?", colors.OKGREEN + "y" + colors.ENDC + "/" +colors.FAIL + "n" + colors.ENDC)
#
# r = input()
#
# if (r == 'y'):
def setup_create_security_group():
    GroupName = input('Enter GroupName: ')
    Description = input('Enter Description: ')
    VpcId = input('Enter VpcId: ')
    create_security_group(GroupName, Description, VpcId)

def create_security_group(GroupName, Description, VpcId):
    ec2 = boto3.client('ec2')
    response = ec2.describe_vpcs()
    vpc_id = response.get('Vpcs', [{}])[0].get('VpcId', '')
    try:
        response = ec2.create_security_group(GroupName=GroupName,
                                             Description=Description,
                                             VpcId=vpc_id)
        security_group_id = response['GroupId']
        print('Security group created ' + colors.HEADER + security_group_id + colors.ENDC + ' in vpc ' + colors.HEADER + vpc_id + colors.ENDC + '.')

        print('Enabling SSH')
        data = ec2.authorize_security_group_ingress(
            GroupId=security_group_id,
            IpPermissions=[ # default ssh open
                {'IpProtocol': 'tcp',
                 'FromPort': 22,
                 'ToPort': 22,
                 'IpRanges': [{'CidrIp': '0.0.0.0/0'}]}
            ])
        print('Created %s' % data)
    except ClientError as e:
        print(e)
