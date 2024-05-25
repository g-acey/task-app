from flask import Flask, jsonify, request
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import create_engine
from sqlalchemy_utils import database_exists, create_database
from datetime import datetime

db = SQLAlchemy()

class TaskCategory(db.Model):
    __tablename__ = 'task_category'
    category_id = db.Column(db.String(6), primary_key=True)
    category_name = db.Column(db.String(30), nullable=False)

    def __repr__(self):
        return f'<TaskCategory {self.category_name}>'

class TaskStatus(db.Model):
    __tablename__ = 'task_status'
    status_id = db.Column(db.String(6), primary_key=True)
    status_name = db.Column(db.String(30), nullable=False)

    def __repr__(self):
        return f'<TaskStatus {self.status_name}>'

class Task(db.Model):
    __tablename__ = 'task'
    task_id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    title = db.Column(db.String(255), nullable=False)
    description = db.Column(db.String(1000), nullable=False)
    status_id = db.Column(db.String(6), db.ForeignKey('task_status.status_id'), nullable=False)
    category_id = db.Column(db.String(6), db.ForeignKey('task_category.category_id'), nullable=False)
    started_time = db.Column(db.DateTime, nullable=False, default=datetime.utcnow)
    finished_time = db.Column(db.DateTime, nullable=True, default=None)

    status = db.relationship('TaskStatus', backref=db.backref('tasks', lazy=True))
    category = db.relationship('TaskCategory', backref=db.backref('tasks', lazy=True))

    def __repr__(self):
        return f'<Task {self.title}>'

def insert_initial_data():
    categories = [
        TaskCategory(category_id='C001', category_name='Normal'),
        TaskCategory(category_id='C002', category_name='Urgent'),
        TaskCategory(category_id='C003', category_name='Important')
    ]

    statuses = [
        TaskStatus(status_id='S001', status_name='New'),
        TaskStatus(status_id='S002', status_name='In Progress'),
        TaskStatus(status_id='S003', status_name='Done')
    ]

    # Check if data already exists to avoid duplicates
    if not TaskCategory.query.first():
        db.session.bulk_save_objects(categories)

    if not TaskStatus.query.first():
        db.session.bulk_save_objects(statuses)

    db.session.commit()

def create_app():
    
    app = Flask(__name__)
    database = "task_inventory"

    # if using docker
    app.config["SQLALCHEMY_DATABASE_URI"] = f"mysql://root:root@db/{database}"
    app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False
    
    
    engine = create_engine(app.config["SQLALCHEMY_DATABASE_URI"])
    
    if not database_exists(engine.url):
        create_database(engine.url)
        print(f"Database '{database}' created.")
    
    db.init_app(app)
    
    with app.app_context():
        db.create_all()
        insert_initial_data()
        
    return app
    
    
app = create_app()
    
@app.route('/')
def first_page():
    print("API for Task App")
    return "API for Task App"
    
@app.route('/api/addtask', methods=['POST'])
def add_new_task():
    try:
        
        data = request.json
        title = data.get('title')
        description = data.get('description')
        category_name = data.get('category')
        
        # Query to get the category_id based on category_name
        category = TaskCategory.query.filter_by(category_name=category_name).first()
        if not category:
            return jsonify({'error': 'Invalid category name provided'}), 400

        new_task = Task(
            title = title,
            description = description,
            category_id = category.category_id,
            status_id = "S001",
        )
        
        db.session.add(new_task)
        db.session.commit()
        
        return jsonify({'message': 'New Task Created Successfully', 'id': new_task.task_id})
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    
@app.route('/api/alltask', methods=['GET'])
def get_all_task():
    try:
        all_task = Task.query.all()
        
        all_task_list = []
        
        for item in all_task:
            all_task_list.append({
                'task_id': item.task_id,
                'title': item.title,
                'description': item.description,
                'status': item.status.status_name,
                'category': item.category.category_name,
                'started_time': item.started_time,
                'finished_time': item.finished_time,
            })
        return jsonify({'task': all_task_list})
    except Exception as e:
        return jsonify({'error': str(e)}), 500 

# Define route for retrieving tasks based on status and category
@app.route('/api/taskbycategoryandstatus', methods=['GET'])
def get_tasks():
    # Extract status and category parameters from the request query string
    status_name = request.args.get('status')
    category_name = request.args.get('category')

    # Query the database based on the provided parameters
    if status_name and category_name:
        category = TaskCategory.query.filter_by(category_name=category_name).first()
        status = TaskStatus.query.filter_by(status_name=status_name).first()
        if category and status:
            tasks = Task.query.filter_by(status_id=status.status_id, category_id=category.category_id).all()
        else:
            return jsonify({'error': 'Category not found!'}), 404
    else:
        return jsonify({'error': 'Both status and category parameters are required!'}), 400

    # Serialize the results into JSON
    task_list = [{
        'task_id': task.task_id,
        'title': task.title,
        'description': task.description,
        'status': task.status.status_name,
        'category': task.category.category_name,
        'started_time': task.started_time.strftime('%Y-%m-%d %H:%M:%S'),  # Format datetime
        'finished_time': task.finished_time.strftime('%Y-%m-%d %H:%M:%S') if task.finished_time else None
    } for task in tasks]

    # Return the JSON response
    return jsonify(task_list)
                
@app.route('/api/editstatus/<int:id>', methods=['PUT'])
def edit_task(id):
    try:
        task = Task.query.get(id)
        if not task:
            return jsonify({'message': 'Item not found!'}), 404

        # Get the current status of the task
        current_status = task.status.status_name

        # Determine the next status based on the current status
        if current_status == 'New':
            next_status = TaskStatus.query.filter_by(status_name='In Progress').first()
        elif current_status == 'In Progress':
            next_status = TaskStatus.query.filter_by(status_name='Done').first()
            task.finished_time = datetime.utcnow()
        else:
            return jsonify({'message': 'Task is already done or in an unrecognized state!'}), 400

        # Update the task status
        if next_status:
            task.status_id = next_status.status_id
            db.session.commit()
            return jsonify({'message': 'Task status updated successfully!'})
        else:
            return jsonify({'error': 'Next status not found!'}), 500
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    

if __name__ == '__main__':
    app.run(debug=True)