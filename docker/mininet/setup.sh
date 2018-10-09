apt-get update

# to run mininet
apt-get install -y --no-install-recommends curl 
apt-get install -y --no-install-recommends iproute2 
apt-get install -y --no-install-recommends iputils-ping 
apt-get install -y --no-install-recommends mininet 
apt-get install -y --no-install-recommends net-tools 
apt-get install -y --no-install-recommends tcpdump
apt-get install -y --no-install-recommends vim
apt-get install -y --no-install-recommends x11-xserver-utils
apt-get install -y --no-install-recommends xterm
apt-get install -y --no-install-recommends python-pip
apt-get install -y --no-install-recommends make

# wireshark
DEBIAN_FRONTEND=noninteractive apt-get install -yq --no-install-recommends wireshark

# to test DHCP
apt-get install -y --no-install-recommends isc-dhcp-client

# to make rest requests in python
pip install --upgrade pip==9.0.3
pip install requests

rm -rf /var/lib/apt/lists/* 

chmod +x /entrypoint.sh
