package net.floodlightcontroller.natbox.service;

import java.util.Collection;
import java.util.TreeMap;

import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IPv4AddressWithMask;
import org.projectfloodlight.openflow.types.OFPort;

import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.l3routing.core.L3Router;
import net.floodlightcontroller.l3routing.service.IL3RoutingService;
import net.floodlightcontroller.natbox.core.NATRouter;

public class NATBoxService implements INATBoxService{	

	protected IL3RoutingService l3routingService;

	public void addl3RoutingService(IL3RoutingService service){
		this.l3routingService = service;
	}
	protected TreeMap<IPv4AddressWithMask,NATRouter> networks = new TreeMap<>();
	protected TreeMap<IPv4Address,NATRouter> nats = new TreeMap<>();
	
	@Override
	public Boolean enableNat(IPv4AddressWithMask network, IPv4Address publicIP, OFPort publicPort){
		L3Router router = l3routingService.getRouter(network);
		if(router != null){
			NATRouter nat = new NATRouter(router,publicIP, publicPort);
			if(router.getNetwork() != null){
				networks.put(router.getNetwork(), nat);
				nats.put(publicIP, nat);
				return true;
			}
		}
		return false;
	}

	@Override
	public Boolean disableNat(IPv4Address publicIP){
		NATRouter nat = nats.get(publicIP);
		if(nat != null){
			nats.remove(publicIP);
			networks.remove(nat.getPrivateNetwork());
			return true;
		}
		return false;
	}

	@Override
	public Collection<NATRouter> getNats(){ 
		return this.nats.values(); 
	}
	
	@Override
	public NATRouter getNat(IPv4AddressWithMask network){
		return this.networks.get(network);
	}

	@Override
	public NATRouter getNat(IPv4Address publicIP){
		return this.nats.get(publicIP);
	}

	@Override
	public Collection<IPv4AddressWithMask> getNetworks(){ 
		return this.networks.keySet();
	}

	@Override
	public Collection<IPv4Address> getPublicIPs(){ 
		return this.nats.keySet();
	}
}
