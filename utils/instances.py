import boto3
from colors import colors

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
