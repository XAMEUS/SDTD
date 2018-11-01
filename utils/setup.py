from keys import launch_key_setup

def print_choices():
    """
    Print the choice list
    """
    print()
    print("What do you want to do ?")
    print("[1] Configure key pairs")
    print("[2] Launch EC2 instances")
    call_choice(input("Your choice ? "))

def call_choice(choice_input):
    """
    Call the right script depending on the user's choice
    """
    if choice_input != "1" and choice_input != "2":
        print_choices()

    if choice_input == "1":
        launch_key_setup()
    elif choice_input == "2":
        pass

if __name__ == '__main__':
    print("SDTD - v0.0.1")
    print_choices()
