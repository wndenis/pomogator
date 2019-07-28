import os
from config import db
from models import Order, OrderPlan

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
        'paths': '[[52.52282, 13.37011], [52.50341, 13.44429]]'
    }
]

db.create_all()

for order in ORDERS:
    o = Order(latitude=order.get("latitude"), longitude=order.get("longitude"))
    db.session.add(o)

for order in ORDER_PLAN:
    o = OrderPlan(paths=order.get("paths"))
    db.session.add(o)

db.session.commit()


