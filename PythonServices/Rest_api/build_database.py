import os
from config import db
from models import Order, OrderPlan, Client, Driver

if os.path.exists("api.db"):
    os.remove("api.db")
# Data to initialize database with
ORDERS = [
    {"latitude": "1.0", "longitude": "5.0"},
    {"latitude": "2.0", "longitude": "6.0"},
    {"latitude": "3.0", "longitude": "7.0"},
]

ORDER_PLAN = [
    {
        'paths': '[[52.52282, 13.37011], [52.50341, 13.44429], [53.52282, 14.37011], [52.52282, 12.011]]'
    }
]

CLIENTS = [
    {
        'orders': '[[52.52282, 13.37011], [52.50341, 13.44429]]'
    }
]
DRIVERS = [
    {
        'order_plan': '1'
    }
]

db.create_all()

for order in ORDERS:
    o = Order(latitude=order.get("latitude"), longitude=order.get("longitude"))
    db.session.add(o)

for order in ORDER_PLAN:
    o = OrderPlan(paths=order.get("paths"))
    db.session.add(o)

for order in CLIENTS:
    o = Client(orders=order.get("orders"))
    db.session.add(o)

for order in DRIVERS:
    o = Driver(order_plan=order.get("order_plan"))
    db.session.add(o)

db.session.commit()


