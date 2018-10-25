import boto3
from colors import colors

ec2 = boto3.client('ec2')
response = ec2.describe_key_pairs()
keys = response['KeyPairs']
print("Current key pairs:", colors.HEADER + str(len(keys)) + colors.ENDC)
for k in keys:
    print("{}  {}".format(k["KeyName"], k["KeyFingerprint"]))
print()

print("Do you want to create a new key pair?", colors.OKGREEN + "y" + colors.ENDC + "/" +colors.FAIL + "n" + colors.ENDC)

r = input()

if (r == 'y'):
    k = input('Enter the key\'s Name: ')
    fname = input('Enter the key.pem filename: ')
    response = ec2.create_key_pair(KeyName=k)
    f = open(fname, 'w')
    f.write(response['KeyMaterial'])
    f.close()
    print(colors.OKGREEN + "Key '{}' created with fingerprint '{}'".format(response["KeyName"], response["KeyFingerprint"]) + colors.ENDC)
