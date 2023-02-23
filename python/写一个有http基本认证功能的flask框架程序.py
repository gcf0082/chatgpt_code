
from flask import Flask, request

from flask_httpauth import HTTPBasicAuth

app = Flask(__name__)

auth = HTTPBasicAuth()

@auth.verify_password

def verify_password(username, password):

    if username == 'admin' and password == 'secret':

        return True

    else:

        return False

@app.route('/file')

@auth.login_required

def get_file():

    file_name = request.args.get('file_name')

    with open(file_name, 'r') as f:

        file_content = f.read()

    return file_content

if __name__ == '__main__':

    app.run()
