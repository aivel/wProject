# coding=utf-8
from random import random
import socket
import json
import string
from win32api import Sleep
import win32file
import random, string
import win32pipe

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

server_address = ('localhost', 12305)


def randomword(length):
    return ''.join(random.choice(string.lowercase) for i in range(length))


while True:
    print "Enter country code: "
    # country_code = raw_input()
    country_code = "RU"
    print "Enter city: "
    # city = raw_input()
    city = "Saint-Petersburg"
    json_obj = {"country": country_code, "city": city}
    json_obj = json.dumps(json_obj)
    try:
        sent = sock.sendto(json_obj, server_address)
        data, server = sock.recvfrom(256)
        decoder = json.JSONDecoder()
        json_result = decoder.decode(data)

        success = json_result["success"]
        is_IDL = json_result["isIDL"]
        if success:
            weather = json_result["weather"]
            temp = weather["temperature"]
            city_name = weather["cityName"]
            min_temperature = weather["minTemperature"]
            effect = weather["effect"]
            country_name = weather["countryName"]
            wind_speed = weather["windSpeed"]
            wind_angle = weather["windAngle"]
            if is_IDL:
                print "Got weather from offline server"
            print country_name + ", " + city_name
            print `temp` + " °C"
            print effect
            print "Wind: " + `wind_angle` + " degrees, " + `wind_speed` + " m/s"
        else:
            print "Failed to get weather: " + json_result["error"]

        print "\n\n"
    except Exception as e:
        print e
sock.close()