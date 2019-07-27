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
    logger.info('Client created')
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

logger = init_logger('here_api_logger')

if __name__ == "__main__":
    try:
        while True:
            routingApi = create_clients()
            r = create_redis()
            client_path = update_path()
            response = routingApi.truck_route(client_path[0],
                                              client_path[1],
                                              [herepy.RouteMode.truck, herepy.RouteMode.fastest, herepy.RouteMode.traffic_default])
            print(response)
    except KeyboardInterrupt:
        logger.exception('Keyboard interrupt!')
        exit(0)
    finally:
        logger.debug('Clean up')
