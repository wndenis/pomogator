import datetime

import pytz
import requests
from constants import *
import herepy
import redis
import logging
from logging.handlers import RotatingFileHandler


def convert_address_to_coor(address):
    # resp = geocoderApi.free_form("красный октябрь")
    return herepy.geocoder_api.GeocoderApi(APP_ID, APP_CODE).free_form(address)


def get_shortest_path(coordinates, client):
    return client.truck_route(coordinates, [herepy.RouteMode.truck, herepy.RouteMode.fastest])


def update_path():
    return [[52.52282, 13.37011],
            [52.50341, 13.44429]]


def create_clients():
    return herepy.RoutingApi(APP_ID, APP_CODE)


def init_logger(logger_name, max_volume_of_log_file_megabytes=100):
    logger = logging.getLogger(logger_name)  # Create a log with the same name as the script that created it
    logger.setLevel('DEBUG')
    filehandler_dbg = RotatingFileHandler(logger_name + '.log',  mode='a', maxBytes=max_volume_of_log_file_megabytes * 1024 * 1024,
                                          backupCount=2, encoding=None, delay=0) # Create handlers and set their logging level
    filehandler_dbg.setLevel('DEBUG')
    streamformatter = logging.Formatter(fmt='[%(asctime)s]:%(levelname)s:%(threadName)s:\t%(message)s',
                                        datefmt='%Y-%m-%d %H:%M:%S') # We only want to see certain parts of the message
    filehandler_dbg.setFormatter(streamformatter)  # Apply formatters to handlers
    logger.addHandler(filehandler_dbg)  # Add handlers to logger

    logger.info('New session started')

    return logger

def create_redis():
    return redis.Redis(host='localhost', port=6379, socket_timeout=5, socket_connect_timeout=5)

def return_path(paths):
    keys = []
    values = []
    for i, x in enumerate(paths):
        print(x)
        values.append(','.join(map(str, x)) )
        if i == 0:
            keys.append('start')
        elif i == len(paths) - 1:
            keys.append('end')
        else:
            keys.append('destination' + str(i))
    values.append('fastest;truck')
    keys.append('mode')
    keys.append('app_id')
    values.append('7FL7O7fEGIJ1XPtoy9Fk')
    keys.append('app_code')
    values.append('7KnIQ-A-QX2FULgcERVvqA')
    keys.append('maxSpeed')
    values.append('80kph')
    # print(keys)
    # print(values)
    # keys.append('departure')
    # values.append(str(datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")).replace(' ','T')+'+03')

    payload = dict(zip(keys, values))
    r = requests.get('https://wse.api.here.com/2/findsequence.json', params=payload)
    print(r.json())
    return r.text
