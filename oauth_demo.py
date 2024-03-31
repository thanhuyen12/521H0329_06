import os
import webbrowser
from urllib.parse import parse_qs

import requests

CLIENT_ID = os.getenv('GITHUB_CLIENT_ID')
CLIENT_SECRET = os.getenv('GITHUB_CLIENT_SECRET')
AUTH_ENDPOINT = f"https:github.com/login/oauth/authorize?response_type=code&client_id={CLIENT_ID}"
TOKEN_ENDPOINT = "https://github.com/login/oauth/access_token"
USER_ENDPOINT = "https://api.github.com/user"

webbrowser.open(AUTH_ENDPOINT, new=2)
print(f"If the web browser is not opened, please use this Authorization URL: {AUTH_ENDPOINT}")
auth_code = input("Enter the auth code: ")

request = requests.post(TOKEN_ENDPOINT, data=dict(client_id=CLIENT_ID, client_secret=CLIENT_SECRET, code=auth_code,))
request = parse_qs(request.content.decode('utf-8'))
token = request['access_token'][0]

user_data = requests.get(USER_ENDPOINT, headers=dict(Authorization=f"token {token}"))
username = user_data.json()["login"]
print(f"Your GitHub's username: {username}")