
from socket import socket, AF_INET, SOCK_STREAM

client = socket(AF_INET,SOCK_STREAM)
client.connect(('10.0.0.101',10123))

msg = 'example message'
client.send(msg)
print 'Sent: ' + msg

received = client.recv(2048)
print 'Received: ' + received

client.close()
