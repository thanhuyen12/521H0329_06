import os
import webbrowser
from urllib.parse import parse_qs

import requests

CLIENT_ID = os.getenv('GITHUB_CLIENT_ID')
CLIENT_SECRET = os.getenv('GITHUB_CLIENT_SECRET')
AUTH_ENDPOINT = f"https:github.com/login/oauth/authorize?response_type=code&client_id={CLIENT_ID}"
TOKEN_ENDPOINT = "https://github.com/login/oauth/access_token"
USER_ENDPOINT = "https://api.github.com/user"

def get_auth_code(client_id):
    auth_url = f"{AUTH_ENDPOINT}?response_type=code&client_id={client_id}"
    webbrowser.open(auth_url, new=2)
    print(f"If the web browser is not opened, please use this Authorization URL: {auth_url}")
    return input("Enter the auth code: ")

def get_access_token(client_id, client_secret, auth_code):
    response = requests.post(TOKEN_ENDPOINT, data=dict(client_id=client_id, client_secret=client_secret, code=auth_code))
    response_data = parse_qs(response.content.decode('utf-8'))
    return response_data['access_token'][0]

def get_user_info(access_token):
    headers = {'Authorization': f'token {access_token}'}
    response = requests.get(USER_ENDPOINT, headers=headers)
    user_data = response.json()
    return user_data

if __name__ == "__main__":
    client_id = os.getenv('GITHUB_CLIENT_ID')
    client_secret = os.getenv('GITHUB_CLIENT_SECRET')

    auth_code = get_auth_code(client_id)
    access_token = get_access_token(client_id, client_secret, auth_code)
    user_info = get_user_info(access_token)

    print("User Information:")
    print(f"Username: {user_info['login']}")
    print(f"Name: {user_info['name']}")
    print(f"Email: {user_info['email']}")
    print(f"Company: {user_info['company']}")
    print(f"Bio: {user_info['bio']}")
