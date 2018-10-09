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

# h1 -- s1 -- s2 -- h2
hosts = []
hosts.append(False)

# h1 -- s1
s1 = net.addSwitch('s1')
hosts.append(net.addHost('h1', mac='00:00:00:00:01:01', ip='10.0.0.101'))
net.addLink(hosts[1], s1)

# s2 -- h2
s2 = net.addSwitch('s2')
hosts.append(net.addHost('h2', mac='00:00:00:00:01:02', ip='10.0.0.102'))
net.addLink(hosts[2], s2)

# s1 -- s2
net.addLink(s1, s2)

net.start()
sleep(3)


# setup controller
# ==============================================================================
body = { "network": { "gateway": "10.0.0.1", "name": "Network1" } }
r.put(BASE+"/networkService/v1.1/tenants/default/networks/Network1",json=body)

for i in range(2):
	body = {"attachment": {"id": "Network1", "mac": "00:00:00:00:01:0"+str(i+1)}}
	r.put(BASE+"/networkService/v1.1/tenants/default/networks/Network1/ports/port"+str(i+1)+"/attachment",json=body)


# testing
# ==============================================================================

CLI( net )
net.stop()

