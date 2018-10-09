
package net.floodlightcontroller.natbox.service;
 
import java.util.Collection;

import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IPv4AddressWithMask;
import org.projectfloodlight.openflow.types.OFPort;

import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.l3routing.core.L3Router;
import net.floodlightcontroller.natbox.core.NATRouter;
 
public interface INATBoxService extends IFloodlightService {

	public Boolean enableNat(IPv4AddressWithMask network, IPv4Address publicIP, OFPort publicPort);
	public Boolean disableNat(IPv4Address publicIP);
	public Collection<NATRouter> getNats();
	public NATRouter getNat(IPv4AddressWithMask network);
	public NATRouter getNat(IPv4Address publicIP);
	public Collection<IPv4AddressWithMask> getNetworks();
	public Collection<IPv4Address> getPublicIPs();
}
