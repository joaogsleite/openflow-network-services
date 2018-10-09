
clean:
	mn --clean

.PHONY: wireshark
wireshark:
	wireshark

rules:
	ovs-ofctl show s$(SW)

flows:
	ovs-ofctl dump-flows s$(SW)

Makefile:
	

%: clean
	python "topologies/$@.py"
