
from flask import make_response, abort, jsonify
from config import db
from models import Client, ClientSchema


def read_all():

    client = Client.query.all()

    order_schema = ClientSchema(many=True)

    data = order_schema.dump(client).data

    return data


def read_one(client_id):

    client = Client.query.filter(Client.clinet_id == client_id).one_or_none()

    if client is not None:

        client_schema = ClientSchema()
        data = client_schema.dump(client).data
        return data

    else:
        abort(
            404
        )


def create(client):

    orders = client.get("orders")

    orders = repr(orders)

    existing_order = (
        Client.query.filter(Client.orders == orders)
        .one_or_none()
    )

    if existing_order is None:

        schema = ClientSchema()
        new_client = schema.load(client, session=db.session).data

        db.session.add(new_client)

        db.session.commit()

        data = schema.dump(new_client).data

        return data, 201

    else:
        abort(
            409
        )


def update(client_id, client):

    update_client = Client.query.filter(
        Client.client_id == client_id
    ).one_or_none()

    orders = client.get("orders")

    orders = repr(orders)

    existing_client = (
        Client.query.filter(Client.orders == orders)
            .one_or_none()
    )

    # Are we trying to find a person that does not exist?
    if update_client is None:
        abort(
            404
        )

    elif (
        existing_client is not None and existing_client.client_id != client_id
    ):
        abort(
            409
        )

    else:

        schema = ClientSchema()
        update = schema.load(client, session=db.session).data

        update.client_id = update_client.client_id

        db.session.merge(update)
        db.session.commit()

        data = schema.dump(update_client).data

        return data, 200


def delete(client_id):

    client = Client.query.filter(Client.client_id == client_id).one_or_none()

    if client is not None:
        db.session.delete(client)
        db.session.commit()
        return make_response(
            "{client_id} deleted".format(client_id=client_id), 200
        )

    else:
        abort(
            404,
            "Not found for Id: {client_id}".format(client_id=client_id),
        )
