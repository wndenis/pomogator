import os
from config import db
from models import Order

if os.path.exists("order.db"):
    os.remove("order.db")
# Data to initialize database with
ORDERS = [
    {"latitude": "1.0", "longitude": "5.0"},
    {"latitude": "2.0", "longitude": "6.0"},
    {"latitude": "3.0", "longitude": "7.0"},
]

db.create_all()

for order in ORDERS:
    o = Order(latitude=order.get("latitude"), longitude=order.get("longitude"))
    db.session.add(o)

db.session.commit()


