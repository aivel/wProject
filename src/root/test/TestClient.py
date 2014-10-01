# coding=utf-8
import socket
import json

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

server_address = ('localhost', 12304)

while True:
    print "Enter country code: "
    country_code = raw_input()
    #country_code = "RU"
    print "Enter city: "
    city = raw_input()
    #city = "Saint-Petersburg"
    json_obj = {"country":country_code,"city":city}
    json_obj = json.dumps(json_obj)
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