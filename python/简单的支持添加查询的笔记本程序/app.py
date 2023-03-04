from flask import Flask, render_template, request
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import or_

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///notes.db'
db = SQLAlchemy(app)

class Note(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    url = db.Column(db.String(200))
    content = db.Column(db.String(1000))
    tag = db.Column(db.String(100))

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/add', methods=['GET', 'POST'])
def add_note():
    if request.method == 'POST':
        url = request.form['url']
        content = request.form['content']
        tag = request.form['tag']
        note = Note(url=url, content=content, tag=tag)
        db.session.add(note)
        db.session.commit()
    return render_template('add_note.html')

@app.route('/view')
def view_notes():
    notes = Note.query.all()
    return render_template('view_notes.html', notes=notes)

@app.route('/search', methods=['GET', 'POST'])
def search_notes():
    if request.method == 'POST':
        keyword = request.form['keyword']
        search_results = db.session.query(Note).filter(or_(
            Note.url.like(f'%{keyword}%'),
            Note.content.like(f'%{keyword}%'),
            Note.tag.like(f'%{keyword}%')
        )).all()
        return render_template('view_notes.html', notes=search_results)
    else:
        return render_template('search_notes.html')

if __name__ == '__main__':
    app.run(host='0.0.0.0',debug=True)
