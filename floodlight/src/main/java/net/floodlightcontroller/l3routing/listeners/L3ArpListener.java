package net.floodlightcontroller.l3routing.listeners;

import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.OFPort;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.util.OFMessageUtils;
import net.floodlightcontroller.packet.ARP;

import net.floodlightcontroller.l3routing.utils.*;
import net.floodlightcontroller.l3routing.core.*;
import net.floodlightcontroller.l3routing.service.*;

public class L3ArpListener implements IOFMessageListener{

	private IL3RoutingService service;
	public L3ArpListener(IL3RoutingService service){
		this.service = service;
	}

	@Override
    public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		
		Ethernet packet = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		OFPort inPort = OFMessageUtils.getInPort((OFPacketIn) msg);

		/* catch only ARP request packets */
		if(ArpUtils.isArpRequest(packet)){	
			/* answer only to ARP requests to a existing L3Network gateway */
			IPv4Address targetIp = ((ARP)packet.getPayload()).getTargetProtocolAddress();
			for(L3Router router : this.service.getRouters()){
				if(router.getGateway().equals(targetIp)){
					/* generate ARP reply packet and send it */
					if(sw.equals(router.OFSwitch())){
						ArpUtils.replyToArpRequest(sw, inPort, packet);
						return Command.STOP;
					}
				}
			}				
		}
		return Command.CONTINUE;
	}

	@Override
	public String getName() {
		return "L3ArpListener";
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		return false;
	}
}
