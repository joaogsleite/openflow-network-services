package net.floodlightcontroller.l3routing.service;

import java.util.Collection;
import java.util.TreeMap;

import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IPv4AddressWithMask;

import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.l3routing.core.L3Router;

public class L3RoutingService implements IL3RoutingService{	

	protected IOFSwitchService switchService;

	public void addSwitchService(IOFSwitchService switchService){
		this.switchService = switchService;
	}
	protected TreeMap<IPv4AddressWithMask,L3Router> networks = new TreeMap<>();
	
	@Override
	public void setupNetwork(String name, IPv4AddressWithMask network, IPv4Address gateway, DatapathId switchId){
		L3Router router = new L3Router(switchService.getSwitch(switchId));
		
		router.setupNetwork(name,network,gateway);
		networks.put(network, router);
	}

	@Override
	public void removeNetwork(IPv4AddressWithMask network){
		networks.get(network).removeNetwork();
		networks.remove(network);
	}

	@Override
	public void removeNetwork(String name) {
		for(L3Router router : networks.values())
			if(router.getNetworkName().equals(name))
				this.removeNetwork(router.getNetwork());
	}

	@Override
	public Collection<L3Router> getRouters(){ 
		return this.networks.values(); 
	}
	
	@Override
	public L3Router getRouter(IPv4AddressWithMask network){
		return this.networks.get(network);
	}

	@Override
	public L3Router getRouter(String name){
		for(L3Router router : this.networks.values()){
			if(router.getNetworkName().equals(name)){
				return router;
			}
		}
		return null;
	}

	@Override
	public Collection<IPv4AddressWithMask> getNetworks(){ 
		return this.networks.keySet();
	}
}
