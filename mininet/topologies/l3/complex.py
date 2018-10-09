from mininet.cli import CLI
from mininet.node import RemoteController, OVSSwitch
from mininet.net import Mininet
from mininet.log import setLogLevel, info
import requests as r 
from time import sleep
from functools import partial

#CONTROLLER_IP = 'floodlight'
CONTROLLER_IP = '172.18.0.2'
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
	hosts.append(net.addHost('h'+str(h+1), mac='00:00:00:00:0'+str(h+1)+':01', ip=str(h+1)+'0.0.0.100/24'))

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


# controller setup
# ==============================================================================
for n in range(NUM):
	body = {
		'name'    : 'network'+str(n+1),
		'network' : str(n+1)+'0.0.0.0/24',
		'gateway' : str(n+1)+'0.0.0.1',
		'switch'  : '00:00:00:00:0'+str(n+1)+':00',
	}
	print r.post(BASE+'/wm/l3routing/networks',json=body).json()

sleep(2)

# hosts setup
# ==============================================================================
for h in range(NUM):
	hosts[h+1].cmd('route add default gw '+str(h+1)+'0.0.0.1')


# testing
# ==============================================================================
CLI( net )
net.stop()
