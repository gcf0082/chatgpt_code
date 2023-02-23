from flask import Flask, request, jsonify

import jwt

import datetime

app = Flask(__name__)

app.config['SECRET_KEY'] = 'supersecretkey'

# A dictionary to store the user credentials

users = {

    'admin': 'password'

}

# A decorator to check if the user is authenticated

def login_required(func):

    def wrapper(*args, **kwargs):

        token = request.headers.get('Authorization')

        if not token:

            return {'message': 'Authorization required'}, 401

        try:

            data = jwt.decode(token, app.config['SECRET_KEY'], algorithms=['HS256'])

            user = data['username']

            if user not in users:

                raise Exception()

        except:

            return {'message': 'Invalid token'}, 401

        return func(*args, **kwargs)

    return wrapper

# Resource to handle login requests

@app.route('/login', methods=['POST'])

def login():

    # Get the username and password from the request data

    data = request.get_json()

    username = data.get('username')

    password = data.get('password')

    # Check if the username and password are correct

    if username in users and users[username] == password:

        # Generate a JWT token

        token = jwt.encode({'username': username, 'exp': datetime.datetime.utcnow() + datetime.timedelta(minutes=30)}, app.config['SECRET_KEY'], algorithm='HS256')

        return jsonify({'token': token}), 200

    return {'message': 'Invalid credentials'}, 401

# Resource to handle file read requests

@app.route('/file/<string:filename>', methods=['GET'])

@login_required

def file(filename):

    # Check if the file exists and is readable

    file_path = os.path.join('files', filename)

    if not os.path.exists(file_path) or not os.access(file_path, os.R_OK):

        return {'message': 'File not found'}, 404

    # Read the file contents

    with open(file_path, 'r') as f:

        content = f.read()

    return {'content': content}, 200

if __name__ == '__main__':

    app.run(debug=True)
