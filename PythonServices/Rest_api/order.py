from flask import make_response, abort
from config import db
from models import Order, OrderSchema


def read_all():

    orders = Order.query.all()

    # Serialize the data for the response
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

    # Can we insert this person?
    if existing_order is None:

        # Create a person instance using the schema and the passed in person
        schema = OrderSchema()
        new_order = schema.load(order, session=db.session).data

        # Add the person to the database
        db.session.add(new_order)
        db.session.commit()

        # Serialize and return the newly created person in the response
        data = schema.dump(new_order).data

        return data, 201

    # Otherwise, nope, person exists already
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

    # Are we trying to find a person that does not exist?
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

    # Otherwise go ahead and update!
    else:

        schema = OrderSchema()
        update = schema.load(order, session=db.session).data

        update.order_id = update_order.order_id

        # merge the new object into the old and commit it to the db
        db.session.merge(update)
        db.session.commit()

        # return updated person in the response
        data = schema.dump(update_order).data

        return data, 200


def delete(order_id):

    order = Order.query.filter(Order.order_id == order_id).one_or_none()

    # Did we find a person?
    if order is not None:
        db.session.delete(order)
        db.session.commit()
        return make_response(
            "Order {order_id} deleted".format(person_id=order_id), 200
        )

    else:
        abort(
            404,
            "Person not found for Id: {order_id}".format(person_id=order_id),
        )