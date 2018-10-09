package net.floodlightcontroller.natbox.listeners;

import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IPv4AddressWithMask;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.natbox.core.NATRouter;
import net.floodlightcontroller.natbox.core.NatEntry;
import net.floodlightcontroller.natbox.service.INATBoxService;
import net.floodlightcontroller.natbox.utils.OFUtils;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;

public class IPv4Listener implements IOFMessageListener {

	private INATBoxService service;
	public IPv4Listener(INATBoxService service){
		this.service = service;
	}

	public NATRouter getNatRouter(Ethernet packet){
		IPv4 ip = (IPv4) packet.getPayload();
		for(IPv4AddressWithMask network : service.getNetworks()){
			if(network.contains(ip.getSourceAddress()) && !network.contains(ip.getDestinationAddress()))
				return service.getNat(network);
		}
		for(IPv4Address publicIP : service.getPublicIPs()){
			if(ip.getDestinationAddress().equals(publicIP))
				return service.getNat(publicIP);
		}
		return null;
	}

	@Override
    public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		
		Ethernet packet = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		if(packet.getEtherType() != EthType.IPv4)
			return Command.CONTINUE;

		NATRouter router = getNatRouter(packet);
		if(router == null) return Command.CONTINUE;

		NatEntry entry = router.getOrGenerateNatEntry(packet);

		OFUtils.programNatEntry(entry, router);
		OFUtils.translateAndSendPacket(router, packet, entry);

		return Command.STOP;
	}

	@Override
	public String getName() {
		return "NATIPv4Listener";
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		if(type==OFType.PACKET_IN){
			return name.equals("virtualizer") || name.equals("forwarding");
		}
		return false;
	}
}
