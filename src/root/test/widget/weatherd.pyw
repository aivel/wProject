#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys
import os
import xml.dom.minidom
import urllib
from ctypes import *

libc = CDLL(os.path.abspath('') + '\\weatherd.dll')

libc.start_app.restype = c_bool
libc.set_weather.restype = c_bool
libc.destroy_app.restype = c_bool
libc.mainloop.restype = c_bool

if len(sys.argv) < 3:
  sys.exit

country_code = sys.argv[1]
city = sys.argv[2]

import socket
import json

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

server_address = ('localhost', 12304)

json_obj = {"country": country_code, "city": city}
json_obj = json.dumps(json_obj)
lweather = []

try:
    sent = sock.sendto(json_obj, server_address)
    data, server = sock.recvfrom(256)
    decoder = json.JSONDecoder()
    json_result = decoder.decode(data)

    success = json_result["success"]
    if success:
        weather = json_result["weather"]
        temp = weather["temperature"]
        city_name = weather["cityName"]
        min_temperature = weather["minTemperature"]
        effect = weather["effect"]
        country_name = weather["countryName"]
        wind_speed = weather["windSpeed"]
        wind_angle = weather["windAngle"]

        lweather.append(country_name + ", " + city_name)
        lweather.append(`temp` + " °C")
        lweather.append(effect)
        lweather.append("Wind: " + `wind_angle` + " degrees, " + `wind_speed` + " m/s")
    else:
        print "Failed to get weather: " + json_result["error"]
    print "\n\n"
except Exception as e:
    print e
sock.close()

if len(lweather) < 4:
  sys.exit

print lweather

print 'start_app:', libc.start_app()
print 'set_weather:', libc.set_weather(c_char_p(lweather[0].decode('utf-8')), 
    c_char_p(lweather[1].decode('utf-8')), 
    c_char_p(lweather[2].decode('utf-8')), 
    c_char_p(lweather[3].decode('utf-8')))

while True:
  xs = libc.mainloop()

print 'destroy_app:', libc.destroy_app()