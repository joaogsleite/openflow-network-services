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

# h1 -- s1 -- s2 -- s3 -- h2

# h1 -- s1
s1 = net.addSwitch('s1', dpid='00:00:00:00:01:00')
h1 = net.addHost('h1', mac='00:00:00:00:01:01', ip='10.0.0.100/24')
net.addLink(h1, s1, port1=1, port2=1)

# s1 -- s2
s2 = net.addSwitch('s2', dpid='00:00:00:00:02:00')
net.addLink(s1, s2, port1=2, port2=1)

# s2 -- s3
s3 = net.addSwitch('s3', dpid='00:00:00:00:03:00')
net.addLink(s2, s3, port1=2, port2=1)

# s3 -- h2
h2 = net.addHost('h2', mac='00:00:00:00:03:01', ip='30.0.0.200/24')
net.addLink(h2, s3, port1=1, port2=2)


net.start()
sleep(3)


# controller setup
# ==============================================================================

# setup networks on the L3Routing module
body = {
	'name'    : 'network1',
	'network' : '10.0.0.0/24',
	'gateway' : '10.0.0.1',
	'switch'  : '00:00:00:00:01:00',
}
print r.post(BASE+'/wm/l3routing/networks',json=body).json()

body = {
	'name'    : 'network2',
	'network' : '20.0.0.0/24',
	'gateway' : '20.0.0.1',
	'switch'  : '00:00:00:00:02:00',
}
print r.post(BASE+'/wm/l3routing/networks',json=body).json()

body = {
	'name'    : 'network2',
	'network' : '30.0.0.0/24',
	'gateway' : '30.0.0.1',
	'switch'  : '00:00:00:00:03:00',
}
print r.post(BASE+'/wm/l3routing/networks',json=body).json()

sleep(2)

# setup nat on the NATBox module
body = { 
	'name'         : 'network1',
	'public-ip'    : '20.0.0.22',
	'public-port'  : '2',
}
print r.post(BASE+'/wm/natbox/networks/'+body.get('name'),json=body).json()

body = { 
	'name'         : 'network2',
	'public-ip'    : '30.0.0.33',
	'public-port'  : '2',
}
print r.post(BASE+'/wm/natbox/networks/'+body.get('name'),json=body).json()

sleep(2)


# devices setup
# ==============================================================================

h1.cmd('route add default gw 10.0.0.1')
h2.cmd('route add default gw 30.0.0.1')


# testing
# ==============================================================================

CLI( net )
net.stop()

