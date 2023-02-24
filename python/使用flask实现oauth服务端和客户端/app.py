from flask import Flask, redirect, url_for, session, request, jsonify, render_template
from flask_login import LoginManager, UserMixin, login_user, logout_user, login_required
from flask_oauthlib.client import OAuth
from functools import wraps
import os

app = Flask(__name__)
app.secret_key = 'secret_key'

# User roles and file access control list
ROLES = {
    'admin': ['file1.txt', 'file2.txt', 'file3.txt'],
    'user1': ['file1.txt', 'file2.txt'],
    'user2': ['file2.txt', 'file3.txt'],
}

oauth = OAuth(app)
login_manager = LoginManager(app)
login_manager.login_view = 'login'

github = oauth.remote_app(
    'github',
    consumer_key='<your_consumer_key>',
    consumer_secret='<your_consumer_secret>',
    request_token_params={'scope': 'user:email'},
    base_url='https://api.github.com/',
    request_token_url=None,
    access_token_method='POST',
    access_token_url='https://github.com/login/oauth/access_token',
    authorize_url='https://github.com/login/oauth/authorize'
)

# User model for Flask-Login
class User(UserMixin):
    def __init__(self, id):
        self.id = id

# User loader for Flask-Login
@login_manager.user_loader
def load_user(user_id):
    return User(user_id)

# OAuth callback
@app.route('/oauth_callback')
@github.authorized_handler
def oauth_callback(resp):
    if resp is None:
        return 'Access denied: reason=%s error=%s' % (
            request.args['error'],
            request.args['error_description']
        )
    session['github_token'] = (resp['access_token'], '')
    return redirect(url_for('index'))

# Authorization decorator
def requires_authorization(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        if 'github_token' not in session:
            return redirect(url_for('login'))
        return f(*args, **kwargs)
    return decorated

# Login route
@app.route('/login')
def login():
    return github.authorize(callback=url_for('oauth_callback', _external=True))

# Logout route
@app.route('/logout')
@login_required
def logout():
    session.pop('github_token', None)
    logout_user()
    return redirect(url_for('index'))

# Index route
@app.route('/')
@requires_authorization
@login_required
def index():
    me = github.get('user').json()
    role = me['login']
    user = User(me['id'])
    login_user(user)
    files = ROLES.get(role, [])
    return render_template('index.html', files=files)

# File route
@app.route('/file/<filename>')
@requires_authorization
@login_required
def file(filename):
    me = github.get('user').json()
    role = me['login']
    files = ROLES.get(role, [])
    if filename not in files:
        return jsonify({'error': 'Access denied'})
    with open(filename, 'r') as f:
        file_content = f.read()
    return jsonify({'content': file_content})


