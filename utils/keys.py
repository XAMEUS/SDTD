import boto3

from colors import colors

def launch_key_setup():
    """
    Print choices
    """
    print()
    print("-- Key manager --")
    print("[1] Create a new key pair")
    print("[2] List key pairs")
    print("[3] Remove a key pair")
    call_choice(input("Your choice ? "))

def call_choice(choice_input):
    """
    Call the right script depending on the user's choice
    """
    if choice_input != "1" and choice_input != "2" and choice_input != "3":
        launch_key_setup()

    if choice_input == "1":
        setup_new_key()
    elif choice_input == "2":
        list_keys()
    elif choice_input == "3":
        setup_delete_key()

def keypair_exist(name):
    """
    Check if the key pair already exist (avoid duplication)
    """
    ec2 = boto3.client('ec2')
    keypairs = ec2.describe_key_pairs()

    key_names = list(map(lambda key: key['KeyName'], keypairs['KeyPairs']))
    return name in key_names

#### KEY CREATION ####

def create_key(key_name, save_path):
    """
    Create a new key
    """
    ec2 = boto3.client('ec2')

    try:
        # Create the key and save the it in the specified file.
        f = open(save_path, 'w')
        response = ec2.create_key_pair(KeyName=key_name)
        f.write(response['KeyMaterial'])
        f.close()

        print(colors.OKGREEN + "Key '{}' created with fingerprint '{}'".format(response["KeyName"], response["KeyFingerprint"]) + colors.ENDC)
    except Exception as e:
        print(e)

def setup_new_key():
    """
    Display the setup menu for key creation
    """
    key_name = input("Enter the key's name: ")

    # Ask for a unique key name.
    while keypair_exist(key_name):
        print("Key name already exists.")
        key_nsetup_delete_keyame = input("Enter the key's name: ")

    pkey_filename = input("Enter the key save path (absolute): ")
    fingerprint = create_key(key_name, pkey_filename)

    launch_key_setup()

#### KEY DELETION ####

def delete_key(key_name):
    """
    Delete a key pair
    """
    ec2 = boto3.client('ec2')
    ec2.delete_key_pair(KeyName=key_name)
    print("Key " + color.OKBLUE + "{} was successfully deleted".format(key_name) + color.ENDC)

def setup_delete_key():
    """
    Display the UI to delete a key
    """
    key_name = input("Enter the key name to delete: ")

    # Check if the key exists.
    while not keypair_exist(key_name):
        print("{} key doesn't exist".format(key_name))
        key_name = input("Enter the key name to delete: ")

    delete_key(key_name)
    launch_key_setup()

#### KEY LIST ####

def list_keys():
    """
    List all existing key pairs
    """
    ec2 = boto3.client('ec2')
    response = ec2.describe_key_pairs()

    # Print each key
    keys = response['KeyPairs']
    print("Existing key pairs:", colors.HEADER + str(len(keys)) + colors.ENDC)
    for k in keys:
        print("{}  {}".format(k["KeyName"], k["KeyFingerprint"]))

    launch_key_setup()
