from flask import make_response, abort
from config import db
from models import Order, OrderSchema


def read_all():

    orders = Order.query.all()

    order_schema = OrderSchema(many=True)
    data = order_schema.dump(orders).data
    return data


def read_one(order_id):

    order = Order.query.filter(Order.order_id == order_id).one_or_none()

    if order is not None:

        # Serialize the data for the response
        order_schema = OrderSchema()
        data = order_schema.dump(order).data
        return data

    else:
        abort(
            404
        )


def create(order):

    latitude = order.get("latitude")
    longitude = order.get("longitude")

    existing_order = (
        Order.query.filter(Order.latitude == latitude)
        .filter(Order.longitude == longitude)
        .one_or_none()
    )

    if existing_order is None:

        schema = OrderSchema()
        new_order = schema.load(order, session=db.session).data

        # Add the person to the database
        db.session.add(new_order)
        db.session.commit()

        data = schema.dump(new_order).data

        return data, 201

    else:
        abort(
            409
        )


def update(order_id, order):

    update_order = Order.query.filter(
        Order.order_id == order_id
    ).one_or_none()

    latitude = order.get("latitude")
    longitude = order.get("longitude")

    existing_order = (
        Order.query.filter(Order.longitude == longitude)
        .filter(Order.latitude == latitude)
        .one_or_none()
    )

    if update_order is None:
        abort(
            404
        )

    elif (
        existing_order is not None and existing_order.order_id != order_id
    ):
        abort(
            409
        )

    else:

        schema = OrderSchema()
        update = schema.load(order, session=db.session).data

        update.order_id = update_order.order_id

        # merge the new object into the old and commit it to the db
        db.session.merge(update)
        db.session.commit()

        data = schema.dump(update_order).data

        return data, 200


def delete(order_id):

    order = Order.query.filter(Order.order_id == order_id).one_or_none()

    if order is not None:
        db.session.delete(order)
        db.session.commit()
        return make_response(
            "Order {order_id} deleted".format(order_id=order_id), 200
        )

    else:
        abort(
            404,
            "Person not found for Id: {order_id}".format(order_id=order_id),
        )