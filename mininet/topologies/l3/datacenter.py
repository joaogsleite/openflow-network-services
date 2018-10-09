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

# s1 
s1 = net.addSwitch('s1', dpid='00:00:00:00:01:00')

# s2 -- s1 -- s3
s2 = net.addSwitch('s2', dpid='00:00:00:00:02:00')
net.addLink(s2, s1, port1=1, port2=1)
s3 = net.addSwitch('s3', dpid='00:00:00:00:03:00')
net.addLink(s3, s1, port1=1, port2=2)

# s4 -- s2 -- s5
s4 = net.addSwitch('s4', dpid='00:00:00:00:04:00')
net.addLink(s4, s2, port1=1, port2=2)
s5 = net.addSwitch('s5', dpid='00:00:00:00:05:00')
net.addLink(s5, s2, port1=1, port2=3)

# s6 -- s3 -- s7
s6 = net.addSwitch('s6', dpid='00:00:00:00:06:00')
net.addLink(s6, s3, port1=1, port2=2)
s7 = net.addSwitch('s7', dpid='00:00:00:00:07:00')
net.addLink(s7, s3, port1=1, port2=3)


hosts = []
hosts.append(False)

hosts.append(net.addHost('h1', mac='00:00:00:00:00:01', ip='40.0.0.101/24'))
hosts.append(net.addHost('h2', mac='00:00:00:00:00:02', ip='40.0.0.102/24'))
hosts.append(net.addHost('h3', mac='00:00:00:00:00:03', ip='50.0.0.103/24'))
hosts.append(net.addHost('h4', mac='00:00:00:00:00:04', ip='50.0.0.104/24'))
hosts.append(net.addHost('h5', mac='00:00:00:00:00:05', ip='60.0.0.105/24'))
hosts.append(net.addHost('h6', mac='00:00:00:00:00:06', ip='60.0.0.106/24'))
hosts.append(net.addHost('h7', mac='00:00:00:00:00:07', ip='70.0.0.107/24'))
hosts.append(net.addHost('h8', mac='00:00:00:00:00:08', ip='70.0.0.108/24'))
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

for i in range(1,8):
	body = {
		'name'    : 'network'+str(i),
		'network' : str(i)+'0.0.0.0/24',
		'gateway' : str(i)+'0.0.0.1',
		'switch'  : '00:00:00:00:0'+str(i)+':00',
	}
	print r.post(BASE+'/wm/l3routing/networks',json=body).json()

sleep(2)

hosts[1].cmd('route add default gw 40.0.0.1')
hosts[2].cmd('route add default gw 40.0.0.1')
hosts[3].cmd('route add default gw 50.0.0.1')
hosts[4].cmd('route add default gw 50.0.0.1')
hosts[5].cmd('route add default gw 60.0.0.1')
hosts[6].cmd('route add default gw 60.0.0.1')
hosts[7].cmd('route add default gw 70.0.0.1')
hosts[8].cmd('route add default gw 70.0.0.1')

sleep(2)

# testing
# ==============================================================================

CLI( net )
net.stop()

