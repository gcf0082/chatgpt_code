from flask import Flask, request

from flask_sqlalchemy import SQLAlchemy

from flask_security import SQLAlchemyUserDatastore, Security, login_required, current_user, UserMixin, RoleMixin

app = Flask(__name__)

app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///users.db'

app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

app.config['SECRET_KEY'] = 'super-secret'

db = SQLAlchemy(app)

# 定义 Role 数据模型

class Role(db.Model, RoleMixin):

    id = db.Column(db.Integer(), primary_key=True)

    name = db.Column(db.String(80), unique=True)

    description = db.Column(db.String(255))

# 定义 User 数据模型

class User(db.Model, UserMixin):

    id = db.Column(db.Integer, primary_key=True)

    email = db.Column(db.String(255), unique=True)

    password = db.Column(db.String(255))

    active = db.Column(db.Boolean())

    confirmed_at = db.Column(db.DateTime())

    roles = db.relationship('Role', secondary='roles_users',

                            backref=db.backref('users', lazy='dynamic'))

    def get_auth_token(self):

        """

        生成一个唯一的授权令牌

        """

        from itsdangerous import TimedJSONWebSignatureSerializer as Serializer

        s = Serializer(app.config['SECRET_KEY'], expires_in=3600)

        return s.dumps({'id': self.id}).decode('utf-8')

    @staticmethod

    def verify_auth_token(token):

        """

        验证授权令牌，如果验证通过，则返回对应的用户对象

        """

        from itsdangerous import TimedJSONWebSignatureSerializer as Serializer

        s = Serializer(app.config['SECRET_KEY'])

        try:

            data = s.loads(token)

        except:

            return None

        return User.query.get(data['id'])

# 定义 roles_users 表

roles_users = db.Table('roles_users',

                       db.Column('user_id', db.Integer(), db.ForeignKey('user.id')),

                       db.Column('role_id', db.Integer(), db.ForeignKey('role.id')))

# 定义文件角色表

file_roles = {

    'file1.txt': ['admin', 'editor'],

    'file2.txt': ['editor', 'viewer'],

    'file3.txt': ['viewer']

}

# 初始化 Flask-Security

user_datastore = SQLAlchemyUserDatastore(db, User, Role)

security = Security(app, user_datastore)

# 登录接口

@app.route('/login', methods=['POST'])

def login():

    email = request.json.get('email')

    password = request.json.get('password')

    user = user_datastore.get_user(email)

    if not user or not user.active or not user.check_password(password):

        return {'message': 'Invalid credentials'}

    access_token = user.get_auth_token()

    return {'access_token': access_token}

# 注销接口

@app.route('/logout', methods=['POST'])

@login_required

def logout():

    user_datastore.logout_user()

    return {'message': 'Logout success'}

# 获取文件内容接口

@app.route('/file/<file_name>', methods=['GET'])

@login_required

def get_file_content(file_name):

    if file_name not in file_roles:

        return {'message': 'File not found'}

    user = current_user

    for role in user.roles:

        if role.name in file_roles[file_name]:

            with open(file_name, 'r') as f:

                content = f.read()

            return {'content': content}

    return {'message': 'Access denied'}

if __name__ == '__main__':

    db.create_all()

    # 创建角色

    admin_role = Role(name='admin', description='Administrator')

    editor_role = Role(name='editor', description='Editor')

    viewer_role = Role(name='viewer', description='Viewer')

    # 创建用户

    admin_user = user_datastore.create_user(

        email='admin@example.com',

        password='password'

    )

    editor_user = user_datastore.create_user(

        email='editor@example.com',

        password='password'

    )

    viewer_user = user_datastore.create_user(

        email='viewer@example.com',

        password='password'

    )

    # 分配角色

    user_datastore.add_role_to_user(admin_user, admin_role)

    user_datastore.add_role_to_user(admin_user, editor_role)

    user_datastore.add_role_to_user(admin_user, viewer_role)

    user_datastore.add_role_to_user(editor_user, editor_role)

    user_datastore.add_role_to_user(editor_user, viewer_role)

    user_datastore.add_role_to_user(viewer_user, viewer_role)

    db.session.commit()

    app.run()
