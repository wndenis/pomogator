import json

from flask import make_response, abort
from config import db
from models import Driver, DriverSchema, OrderPlan, OrderPlanSchema, Order, OrderSchema
from api import return_path
from ast import literal_eval


def read_all():

    orders = Driver.query.all()

    order_schema = DriverSchema(many=True)
    data = order_schema.dump(orders).data
    return data


# def read_one(driver_id, lat, lng):
# 
#     order = Driver.query.filter(Driver.driver_id == driver_id).one_or_none()
# 
#     if order is not None:
# 
#         search_for_path = OrderPlan.query.filter(OrderPlan.order_plan_id == (order).order_plan
#                                                  ).one_or_none()
# 
#         if search_for_path is not None:
#             order_plan_schema = OrderPlanSchema()
#             data = order_plan_schema.dump(search_for_path).data
#             data = literal_eval(data['paths'])
#             coords = [float(lat), float(lng)]
#             # coords = literal_eval(coords)
#             data.insert(0, coords)
#             data.append(coords)
#             data = return_path(data)
#             return json.loads(data)
#     else:
#         abort(
#             404
#         )'

def read_one(driver_id, lat, lng):

    order = Driver.query.filter(Driver.driver_id == driver_id).one_or_none()

    if order is not None:
        orders = Order.query.all()
        order_schema = OrderSchema(many=True)
        data = order_schema.dump(orders).data
        # data = literal_eval(data[0])
        path = []
        for i in data:
            path.append([float(i['latitude']), float(i['longitude'])])
        coords = [float(lat), float(lng)]
        # # coords = literal_eval(coords)
        path.insert(0, coords)
        path.append(coords)
        data = return_path(path)
        return json.loads(data)
    else:
        abort(
            404
        )

def update(order_id, order):

    update_order = Driver.query.filter(
        Driver.driver_id == order_id
    ).one_or_none()

    order_plan = order.get("order_plan")

    existing_order = (
        Driver.query.filter(Driver.order_plan == order_plan)
        .one_or_none()
    )

    if update_order is None:
        abort(
            404
        )

    elif (
        existing_order is not None and existing_order.driver_id != order_id
    ):
        abort(
            409
        )

    else:

        schema = DriverSchema()
        update = schema.load(order, session=db.session).data

        update.driver_id = update_order.driver_id

        # merge the new object into the old and commit it to the db
        db.session.merge(update)
        db.session.commit()

        data = schema.dump(update_order).data

        return data, 200

def create(client):

    orders = client.get("order_plan")

    existing_order = (
        Driver.query.filter(Driver.order_plan == orders)
        .one_or_none()
    )

    if existing_order is None:

        schema = DriverSchema()
        new_client = schema.load(client, session=db.session).data

        db.session.add(new_client)

        db.session.commit()

        data = schema.dump(new_client).data

        return data, 201

    else:
        abort(
            409
        )
