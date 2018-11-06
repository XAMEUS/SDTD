from keys import call_choice_key
from instances import call_choice_instance
from quick_deploy import quick_deploy

def print_choices():
    """
    Print the choice list
    """
    print()
    print("What do you want to do ?")
    print("[1] Configure key pairs")
    print("[2] Manage EC2 instances")
    print("[3] Fast deploy")
    call_choice(input("Your choice ? "))

def launch_instance_setup():
    """
    Print choice list
    """
    print()
    print("-- Instance manager --")
    print("[1] Launch a new instance")
    print("[2] List instances")
    print("[3] Terminate all instances")

    try:
        choice_input = input("Your choice ? ")
        if choice_input != "1" and choice_input != "2" and choice_input != "3":
            launch_instance_setup()

        call_choice_instance(choice_input)
        launch_instance_setup()

    except EOFError:
        print_choices()

def launch_key_setup():
    """
    Print choices
    """
    print()
    print("-- Key manager --")
    print("[1] Create a new key pair")
    print("[2] List key pairs")
    print("[3] Remove a key pair")

    try:
        choice_input = input("Your choice ? ")

        if choice_input != "1" and choice_input != "2" and choice_input != "3":
            launch_key_setup()

        call_choice_key(choice_input)
        launch_key_setup()

    except EOFError:
        print_choices()

def call_choice(choice_input):
    """
    Call the right script depending on the user's choice
    """
    if choice_input != "1" and choice_input != "2" and choice_input != "3":
        print_choices()

    if choice_input == "1":
        launch_key_setup()
    elif choice_input == "2":
        launch_instance_setup()
    elif choice_input == "3":
        quick_deploy()

if __name__ == '__main__':
    print("SDTD - v0.0.1")
    print_choices()
