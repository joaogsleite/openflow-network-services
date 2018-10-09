
package net.floodlightcontroller.l3routing.service;
 
import java.util.Collection;

import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IPv4AddressWithMask;

import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.l3routing.core.L3Router;
 
public interface IL3RoutingService extends IFloodlightService {

	public void setupNetwork(String name, IPv4AddressWithMask network, IPv4Address gateway, DatapathId switchId);

	public void removeNetwork(IPv4AddressWithMask network);
	public void removeNetwork(String name);

	public Collection<IPv4AddressWithMask> getNetworks();

	public L3Router getRouter(IPv4AddressWithMask network);
	public L3Router getRouter(String name);

	public Collection<L3Router> getRouters();

}
