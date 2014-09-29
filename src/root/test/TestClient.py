import socket
import sys

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

server_address = ('localhost', 12304)

try:
    while True:
        sent = sock.sendto(raw_input(), server_address)
        #data, server = sock.recvfrom(256)
        #print data, server
finally:
    sock.close()