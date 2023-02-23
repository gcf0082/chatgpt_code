from flask import Flask, request, session
from flask_restful import Resource, Api

app = Flask(__name__)
app.secret_key = 'supersecretkey'
api = Api(app)

# Resource to handle login requests
class Login(Resource):
    def post(self):
        # Get the username and password from the request data
        data = request.get_json()
        username = data.get('username')
        password = data.get('password')

        # Check if the username and password are correct
        if username == 'admin' and password == 'password':
            # Set the user id in the session
            session['user_id'] = 1

            return {'message': 'Logged in'}, 200

        return {'message': 'Invalid credentials'}, 401

# Resource to handle logout requests
class Logout(Resource):
    def post(self):
        # Clear the user id from the session
        session.pop('user_id', None)

        return {'message': 'Logged out'}, 200

# Resource to handle file read requests
class File(Resource):
    def get(self, filename):
        # Check if the user is logged in
        if 'user_id' not in session:
            return {'message': 'Authorization required'}, 401

        # Check if the file exists and is readable
        file_path = os.path.join('files', filename)
        if not os.path.exists(file_path) or not os.access(file_path, os.R_OK):
            return {'message': 'File not found'}, 404

        # Read the file contents
        with open(file_path, 'r') as f:
            content = f.read()

        return {'content': content}, 200

# Add routes to the API
api.add_resource(Login, '/login')
api.add_resource(Logout, '/logout')
api.add_resource(File, '/file/<string:filename>')

if __name__ == '__main__':
    app.run(debug=True)
