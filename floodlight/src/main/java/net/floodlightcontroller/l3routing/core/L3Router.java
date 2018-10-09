package net.floodlightcontroller.l3routing.core;

import java.awt.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TreeMap;

import javax.annotation.Nonnull;

import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IPv4AddressWithMask;
import org.python.modules._collections.Collections;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.devicemanager.IDevice;

public class L3Router {

	public IPv4AddressWithMask network;
	private String name;
	private L3OFRulesTask openFlowRules;
	public IPv4Address gateway;
	private ArrayList<IDevice> hosts = new ArrayList<>();
	private Timer timer;
	private IOFSwitch sw;

	public L3Router(IOFSwitch sw){
		this.sw = sw;
	}	
	public L3Router setupNetwork(String name, IPv4AddressWithMask network, IPv4Address gateway){
		this.network = network;
		this.name = name;
		this.gateway = gateway;
		this.openFlowRules = new L3OFRulesTask(this);
		return this;
	}
	public L3Router removeNetwork(){
		this.network = null;
		this.openFlowRules.clean();
		this.openFlowRules = null;
		return this;
	}
	public IPv4AddressWithMask getNetwork(){
		return this.network;
	}
	public L3Router setGateway(IPv4Address ip){
		this.gateway = ip;
		return this;
	}
	public IPv4Address getGateway(){
		return this.gateway;
	}
	public String getNetworkName(){
		return this.name;
	}
	public L3Router addHost(IDevice device){
		if(!this.hosts.contains(device))
			this.hosts.add(device);
		return this;
	}
	public IDevice getHost(IPv4Address ip){
		for(IDevice host : hosts)
			for(IPv4Address host_ip : host.getIPv4Addresses())
				if(host_ip.equals(ip))
					return host;
		return null;
	}
	public L3Router removeHost(IDevice device){
		if(this.hosts.contains(device))
			this.hosts.remove(device);
		return this;
	}
	protected Collection<? extends IDevice> getHosts(){
		return (Collection<? extends IDevice>) this.hosts;
	}

	// OFSwitch

	public IOFSwitch OFSwitch(){
		return this.sw;
	}
	public OFFactory getOFFactory(){
		return this.sw.getOFFactory();
	}
	public void write(OFMessage msg){
		this.sw.write(msg);
	}
	public DatapathId getId(){
		return this.sw.getId();
	}
}
