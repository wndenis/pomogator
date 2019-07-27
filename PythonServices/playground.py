from constants import *
import herepy

geocoderApi = herepy.GeocoderApi("fx1LPieiEyOPBkSpUwYO", "c8rCqh0ooTCImrQ3pJ94SQ")
resp = geocoderApi.free_form("красный октябрь, г. Москва")
print(resp.as_dict()['Response'])


#
# api = herepy.routing_api.RoutingApi(APP_ID, APP_CODE)
# api.intermediate_route()
