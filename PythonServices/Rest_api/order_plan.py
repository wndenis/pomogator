import json
import time

from flask import make_response, abort, jsonify
from config import db
from models import OrderPlan, OrderPlanSchema
from api import return_path
from ast import literal_eval

def read_all():

    orders = OrderPlan.query.all()

    order_schema = OrderPlanSchema(many=True)

    data = order_schema.dump(orders).data

    return data


def read_one(order_plan_id):

    order = OrderPlan.query.filter(OrderPlan.order_plan_id == order_plan_id).one_or_none()

    if order is not None:

        order_plan_schema = OrderPlanSchema()
        data = order_plan_schema.dump(order).data
        return data

    else:
        abort(
            404
        )


def create(order_plan):

    paths = order_plan.get("paths")

    paths = repr(paths)

    existing_order = (
        OrderPlan.query.filter(OrderPlan.paths == paths)
        .one_or_none()
    )

    if existing_order is None:

        schema = OrderPlanSchema()
        new_order_plan = schema.load(order_plan, session=db.session).data

        db.session.add(new_order_plan)

        db.session.commit()

        data = schema.dump(new_order_plan).data

        return data, 201

    else:
        abort(
            409
        )


def update(order_plan_id, order_plan):

    update_order_plan = OrderPlan.query.filter(
        OrderPlan.order_plan_id == order_plan_id
    ).one_or_none()

    paths = order_plan.get("orders")

    paths = repr(paths)

    existing_order = (
        OrderPlan.query.filter(OrderPlan.paths == paths)
            .one_or_none()
    )

    # Are we trying to find a person that does not exist?
    if update_order_plan is None:
        abort(
            404
        )

    elif (
        existing_order is not None and existing_order.order_plan_id != order_plan_id
    ):
        abort(
            409
        )

    # Otherwise go ahead and update!
    else:

        schema = OrderPlanSchema()
        update = schema.load(order_plan, session=db.session).data

        update.order_id = update_order_plan.order_id

        # merge the new object into the old and commit it to the db
        db.session.merge(update)
        db.session.commit()

        # return updated person in the response
        data = schema.dump(update_order_plan).data

        return data, 200


def delete(order_plan_id):

    order_plan = OrderPlan.query.filter(OrderPlan.order_plan_id == order_plan_id).one_or_none()

    if order_plan is not None:
        db.session.delete(order_plan)
        db.session.commit()
        return make_response(
            "{order_plan_id} deleted".format(order_plan_id=order_plan_id), 200
        )

    else:
        abort(
            404,
            "Not found for Id: {order_plan_id}".format(order_plan_id=order_plan_id),
        )

def get_path(order_plan_id):
    order_plan = OrderPlan.query.filter(OrderPlan.order_plan_id == order_plan_id).one_or_none()
    if order_plan is not None:
        order_plan_schema = OrderPlanSchema()
        data = order_plan_schema.dump(order_plan).data
        data = literal_eval(data['paths'])
        data = return_path(data)
        return json.loads(data)