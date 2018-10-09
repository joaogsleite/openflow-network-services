
#import os
from socket import socket, AF_INET, SOCK_STREAM, SOL_SOCKET, SO_REUSEADDR

server = socket(AF_INET, SOCK_STREAM)
server.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
server.bind(('0.0.0.0',10123))
server.listen(3)

while True:
	print 'server listenning...'

	connection,address = server.accept()
	received = connection.recv(2048)
	print 'Received: ' + received
	
	send = received + ' from server'
	connection.send(send)
	print 'Sent: ' + send

	connection.close()
