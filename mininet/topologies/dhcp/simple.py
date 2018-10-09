
from mininet.cli import CLI
from mininet.node import RemoteController
from mininet.net import Mininet
from mininet.log import setLogLevel, info

def host_setup(host):
	etc = '/tmp/etc-%s' % host
	host.cmd('mkdir -p', etc)
	host.cmd('mount --bind /etc', etc)
	host.cmd('mount -n -t tmpfs tmpfs /etc')
	host.cmd('ln -s %s/* /etc/' % etc)
	host.cmd('rm /etc/resolv.conf')
	host.cmd('cp %s/resolv.conf /etc/' % etc)

net = Mininet( controller=RemoteController )

net.addController(RemoteController( 'c0', ip='192.168.0.51', port=6653 ))

s1 = net.addSwitch('s1', dpid='1')
h1 = net.addHost('h1', mac='00:00:00:00:01:01', ip=None)
host_setup(h1)
net.addLink(h1, s1)
h2 = net.addHost('h2', mac='00:00:00:00:01:02', ip=None)
net.addLink(h2, s1)
host_setup(h2)

s2 = net.addSwitch('s2', dpid='2')
h3 = net.addHost('h3', mac='00:00:00:00:02:03', ip=None)
net.addLink(h3, s2)

net.addLink(s1, s2)

net.start()

h1.sendCmd('dhclient')

CLI( net )

net.stop()
