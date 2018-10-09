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

NUM=6

# OF Switches
switches = []
switches.append(False)
for s in range(NUM):
	switches.append(net.addSwitch('s'+str(s+1), dpid='00:00:00:00:0'+str(s+1)+':00'))

# Hosts
hosts = []
hosts.append(False)
for h in range(NUM):
	hosts.append(net.addHost('h'+str(h+1), mac='00:00:00:00:02:0'+str(h+1), ip='10.0.0.10'+str(h+1)))

# connect hosts to switch
for l in range(NUM):
	net.addLink(hosts[l+1], switches[l+1], port1=1, port2=1)

# s2 -- s1 -- s3
net.addLink(switches[1], switches[2], port1=2, port2=2)
net.addLink(switches[1], switches[3], port1=3, port2=2)

# s4 -- s6 -- s5
net.addLink(switches[6], switches[4], port1=2, port2=2)
net.addLink(switches[6], switches[5], port1=3, port2=2)

# s2 -- s4  &  s3 -- s5
net.addLink(switches[2], switches[4], port1=3, port2=3)
net.addLink(switches[3], switches[5], port1=3, port2=3)

net.start()
sleep(5)

# setup controller
# ==============================================================================
body = { "network": { "gateway": "10.0.0.1", "name": "Network1" } }
r.put(BASE+"/networkService/v1.1/tenants/default/networks/Network1",json=body)

for i in range(NUM):
	body = {"attachment": {"id": "Network1", "mac": "00:00:00:00:02:0"+str(i+1)}}
	r.put(BASE+"/networkService/v1.1/tenants/default/networks/Network1/ports/port"+str(i+1)+"/attachment",json=body)

sleep(2)

# testing
# ==============================================================================
CLI( net )
net.stop()
