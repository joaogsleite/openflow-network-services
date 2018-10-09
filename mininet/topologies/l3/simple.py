from mininet.cli import CLI
from mininet.node import RemoteController, OVSSwitch
from mininet.net import Mininet
from mininet.log import setLogLevel, info
import requests as r 
from time import sleep
from functools import partial

CONTROLLER_IP = '172.18.0.3'
BASE = 'http://'+CONTROLLER_IP+':8080'

switch = partial( OVSSwitch )
net = Mininet( controller=RemoteController, switch=switch )
net.addController(RemoteController( 'c0', ip=CONTROLLER_IP, port=6653 ))

# h1 -- s1
s1 = net.addSwitch('s1', dpid='00:00:00:00:01:00')
h1 = net.addHost('h1', mac='00:00:00:00:01:01', ip='192.168.1.100/24')
net.addLink(h1, s1, port1=1, port2=1)

# h2 -- s2
s2 = net.addSwitch('s2', dpid='00:00:00:00:02:00')
h2 = net.addHost('h2', mac='00:00:00:00:02:01', ip='192.168.2.100/24')
net.addLink(h2, s2, port1=1, port2=1)

# s1 -- s2
net.addLink(s1, s2, port1=2, port2=2)

net.start()

sleep(3)

# setup networks on the L3Routing module
body = {
	'name'    : 'network1',
	'network' : '192.168.1.0/24',
	'gateway' : '192.168.1.1',
	'switch'  : '00:00:00:00:01:00',
}
print r.post(BASE+'/wm/l3routing/networks',json=body).json()

body = {
	'name'    : 'network2',
	'network' : '192.168.2.0/24',
	'gateway' : '192.168.2.1',
	'switch'  : '00:00:00:00:02:00',
}
print r.post(BASE+'/wm/l3routing/networks',json=body).json()

sleep(2)

# setup default gateway on hosts
h1.sendCmd('route add default gw 192.168.1.1')
h2.sendCmd('route add default gw 192.168.2.1')

CLI( net )
net.stop()

