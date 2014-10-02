#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys
import os
import xml.dom.minidom
import urllib
from ctypes import *

print os.path.abspath('') + '\\weatherd.dll'
libc = CDLL(os.path.abspath('') + '\\weatherd.dll')

libc.start_app.restype = c_bool
libc.set_weather.restype = c_bool
libc.destroy_app.restype = c_bool
libc.mainloop.restype = c_bool

addr = 'http://informer.gismeteo.ru/xml/27037_1.xml'
param = ['TEMPERATURE', 'PRESSURE', 'WIND']

class weathClass(list):
  def __init__(self, lst): # [temp, press, wind]
    self.temp = lst[0] # title
    self.press = lst[1] # link
    self.wind = lst[2] # date

class Parser():
  def __init__(self, xml_str):
    self.doc = xml.dom.minidom.parseString(xml_str)

  def parse(self):
    weath = []
    items = self.doc.getElementsByTagName('FORECAST')
    for i in items:
        max_val = map(lambda p: i.getElementsByTagName(p)[0].getAttribute('max'), param)
        min_val = map(lambda p: i.getElementsByTagName(p)[0].getAttribute('min'), param)
        weath.append(weathClass(zip(max_val, min_val)))
    return weath

class Application():
  def __init__(self):
    print 'start_app:', libc.start_app()
    parser = Parser(self.load(addr))
    self.set(self.format(parser.parse()))

  def format(self, weath):
    lweather = []
    lweather.append('Погода в Вологде: '.decode('UTF-8') + weath[0].temp[1] + ' .. ' + weath[0].temp[0])
    lweather.append('Давление: '.decode('UTF-8') + weath[0].press[1] + ' .. ' + weath[0].press[0] + 'мм рт.ст.'.decode('UTF-8'))
    lweather.append('Ветер: '.decode('UTF-8') + weath[0].wind[1] + ' .. ' + weath[0].wind[0] + ' м/с'.decode('UTF-8'))
    
    print lweather
    #raw_input()

    return lweather
 
  def load(self, url):
    up = urllib.urlopen(url)
    return up.read()

  def set(self, nlweather):
    print 'set_weather:', libc.set_weather(c_char_p(nlweather[0]), c_char_p(nlweather[1]), c_char_p(nlweather[2]))
    while True:
	    print libc.mainloop()

App = Application()
#raw_input()
print 'destroy_app:', libc.destroy_app()
#raw_input()
