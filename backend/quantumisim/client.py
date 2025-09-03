import requests

import sys



# --- CONFIGURATION ---

# The base URL for your Spring Boot application

BASE_URL = "http://localhost:8080/api/activation"



# File paths for the documents to be uploaded

# Make sure these files exist in the same directory as the script

ID_DOCUMENT_PATH = "document.png"

SELFIE_IMAGE_PATH = "service.jpg" # Using a different image for selfie for clarity



# --- API Call Functions (Mirrors Postman Requests) ---



def request_1_verify_document(filepath):

    """

    Corresponds to 'Request 1: Verify Document'.

    Uploads the ID document and returns the challenge and extracted name.

    """

    print("--- [Step 1] Verifying Document ---")

    try:

        with open(filepath, 'rb') as f:

            files = {'idDocument': (filepath, f, 'image/png')}

            response = requests.post(f"{BASE_URL}/verify-document", files=files)

            

            # This will raise an error for 4xx or 5xx status codes

            response.raise_for_status()



            data = response.json()

            challenge = data.get("challenge")

            extracted_name = data.get("extractedName")

            

            if not challenge or not extracted_name:

                print("Error: Server response did not contain challenge or extractedName.")

                return None, None

                

            print(f"Success! Challenge received: {challenge}")

            print(f"Extracted Name: {extracted_name}")

            return challenge, extracted_name



    except FileNotFoundError:

        print(f"ERROR: The file '{filepath}' was not found.")

        return None, None

    except requests.exceptions.RequestException as e:

        print(f"Error during document verification: {e}")

        print(f"Server response: {e.response.text if e.response else 'No response'}")

        return None, None



def request_2_verify_face(filepath, name):

    """

    Corresponds to 'Request 2: Verify Face'.

    Uploads a selfie for facial verification.

    """

    print("\n--- [Step 2] Verifying Face ---")

    try:

        with open(filepath, 'rb') as f:

            files = {'selfieImage': (filepath, f, 'image/jpeg')}

            data = {'expectedName': name}

            response = requests.post(f"{BASE_URL}/verify-face", files=files, data=data)

            

            response.raise_for_status()

            

            print("Success! Facial verification passed.")

            return True



    except FileNotFoundError:

        print(f"ERROR: The file '{filepath}' was not found.")

        return False

    except requests.exceptions.RequestException as e:

        print(f"Error during facial verification: {e}")

        print(f"Server response: {e.response.text if e.response else 'No response'}")

        return False



def request_3_get_public_key():

    """

    Corresponds to 'Request 3: Get Public Key'.

    Fetches the public key from the server.

    """

    print("\n--- [Step 3] Fetching Public Key ---")

    try:

        response = requests.get(f"{BASE_URL}/get-public-key")

        response.raise_for_status()

        

        data = response.json()

        public_key = data.get("publicKey")

        

        print("Success! Public Key received.")

        return public_key

        

    except requests.exceptions.RequestException as e:

        print(f"Error fetching public key: {e}")

        return None



def request_4_get_signature(challenge):

    """

    Corresponds to 'Request 4: Get Signature'.

    Asks the server to sign the challenge.

    """

    print("\n--- [Step 4] Fetching Signature for Challenge ---")

    try:

        params = {'challenge': challenge}

        response = requests.get(f"{BASE_URL}/get-signature-for-challenge", params=params)

        response.raise_for_status()



        data = response.json()

        signature = data.get("signature")

        

        print("Success! Signature received.")

        return signature



    except requests.exceptions.RequestException as e:

        print(f"Error fetching signature: {e}")

        return None



def request_5_activate_isim(challenge, signature, public_key):

    """

    Corresponds to 'Request 5: Verify Hardware and Activate'.

    Sends the final payload to activate the iSIM.

    """

    print("\n--- [Step 5] Activating iSIM (Hardware Proof-of-Presence) ---")

    print("Please be ready to press the button on the Arduino device NOW.")

    try:

        # This is the critical JSON payload

        payload = {

            "challenge": challenge,

            "signature": signature,

            "publicKey": public_key

        }

        

        print("Sending final payload to server...")

        response = requests.post(f"{BASE_URL}/verify-hardware-and-activate", json=payload)

        

        response.raise_for_status()

        

        print("\n✅ ✅ ✅ ACTIVATION SUCCEEDED! ✅ ✅ ✅")

        print("iSIM Profile Provisioned:")

        print(response.json())



    except requests.exceptions.RequestException as e:

        print(f"\n❌ ❌ ❌ ACTIVATION FAILED! ❌ ❌ ❌")

        print(f"Error during final activation: {e}")

        print(f"Server response: {e.response.text if e.response else 'No response'}")



# --- Main Execution Flow ---

if __name__ == "__main__":

    # Step 1: Call the first endpoint and store the results in variables

    challenge_from_server, name_from_server = request_1_verify_document(ID_DOCUMENT_PATH)

    

    # Check if Step 1 was successful before proceeding

    if not (challenge_from_server and name_from_server):

        print("\nAborting flow due to failure in Step 1.", file=sys.stderr)

        sys.exit(1)

        

    # Step 2: Use the 'name' from Step 1 to verify the face

    if not request_2_verify_face(SELFIE_IMAGE_PATH, name_from_server):

        print("\nAborting flow due to failure in Step 2.", file=sys.stderr)

        sys.exit(1)

        

    # Step 3: Get the public key

    public_key_from_server = request_3_get_public_key()

    if not public_key_from_server:

        print("\nAborting flow due to failure in Step 3.", file=sys.stderr)

        sys.exit(1)

        

    # Step 4: Use the 'challenge' from Step 1 to get a signature

    signature_from_server = request_4_get_signature(challenge_from_server)

    if not signature_from_server:

        print("\nAborting flow due to failure in Step 4.", file=sys.stderr)

        sys.exit(1)

        

    # Step 5: Use all the collected pieces of data to make the final call

    request_5_activate_isim(

        challenge=challenge_from_server,

        signature=signature_from_server,

        public_key=public_key_from_server

    )
