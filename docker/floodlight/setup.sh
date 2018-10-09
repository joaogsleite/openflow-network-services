apt-get update

# to run floodlight
apt-get install -y --no-install-recommends make
apt-get install -y --no-install-recommends ant 
apt-get install -y --no-install-recommends iputils-ping 
apt-get install -y --no-install-recommends vim
apt-get install -y --no-install-recommends python-pip

# for python test scripts
pip install --upgrade pip==9.0.3

rm -rf /var/lib/apt/lists/* 
