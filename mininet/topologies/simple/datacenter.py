from mininet.cli import CLI
from mininet.node import RemoteController, OVSSwitch
from mininet.net import Mininet
from mininet.log import setLogLevel, info
import requests as r 
from time import sleep
from functools import partial

#CONTROLLER_IP = 'floodlight'
CONTROLLER_IP = '172.18.0.3'
BASE = 'http://'+CONTROLLER_IP+':8080'
switch = partial( OVSSwitch )
net = Mininet( controller=RemoteController, switch=switch )
net.addController(RemoteController( 'c0', ip=CONTROLLER_IP, port=6653 ))


# setup topology
# ==============================================================================

# s1 
s1 = net.addSwitch('s1')

# s2 -- s1 -- s3
s2 = net.addSwitch('s2')
net.addLink(s2, s1, port1=1, port2=1)
s3 = net.addSwitch('s3')
net.addLink(s3, s1, port1=1, port2=2)

# s4 -- s2 -- s5
s4 = net.addSwitch('s4')
net.addLink(s4, s2, port1=1, port2=2)
s5 = net.addSwitch('s5')
net.addLink(s5, s2, port1=1, port2=3)

# s6 -- s3 -- s7
s6 = net.addSwitch('s6')
net.addLink(s6, s3, port1=1, port2=2)
s7 = net.addSwitch('s7')
net.addLink(s7, s3, port1=1, port2=3)


hosts = []
hosts.append(False)
for i in range(8):
	hosts.append(net.addHost('h'+str(i+1), mac='00:00:00:00:01:0'+str(i+1), ip='10.0.0.10'+str(i+1)+'/24'))
	#hosts[i+1].cmd('route add default gw 10.0.0.1')

net.addLink(hosts[1], s4, port1=1, port2=2)
net.addLink(hosts[2], s4, port1=1, port2=3)
net.addLink(hosts[3], s5, port1=1, port2=2)
net.addLink(hosts[4], s5, port1=1, port2=3)
net.addLink(hosts[5], s6, port1=1, port2=2)
net.addLink(hosts[6], s6, port1=1, port2=3)
net.addLink(hosts[7], s7, port1=1, port2=2)
net.addLink(hosts[8], s7, port1=1, port2=3)

net.start()
sleep(3)

body = { "network": { "gateway": "10.0.0.1", "name": "Network1" } }
r.put(BASE+"/networkService/v1.1/tenants/default/networks/Network1",json=body)

for i in range(8):
	body = {"attachment": {"id": "Network1", "mac": "00:00:00:00:01:0"+str(i+1)}}
	r.put(BASE+"/networkService/v1.1/tenants/default/networks/Network1/ports/port"+str(i+1)+"/attachment",json=body)


# testing
# ==============================================================================

CLI( net )
net.stop()

