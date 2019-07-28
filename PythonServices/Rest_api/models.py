from datetime import datetime
from config import db, ma


class Order(db.Model):
    __tablename__ = "order"
    order_id = db.Column(db.Integer, primary_key=True)
    description = db.Column(db.String(32))
    latitude = db.Column(db.String(32))
    longitude = db.Column(db.String(32))
    timestamp = db.Column(
        db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow
    )

class OrderPlan(db.Model):
    __tablename__ = "order_plan"
    order_plan_id = db.Column(db.Integer, primary_key=True)
    paths = db.Column(db.String)

class OrderSchema(ma.ModelSchema):
    class Meta:
        model = Order
        sqla_session = db.session

class OrderPlanSchema(ma.ModelSchema):
    class Meta:
        model = OrderPlan
        sqla_session = db.session

class Client(db.Model):
    __tablename__ = "client"
    client_id = db.Column(db.Integer, primary_key=True)
    orders = db.Column(db.String)

class ClientSchema(ma.ModelSchema):
    class Meta:
        model = Client
        sqla_session = db.session
