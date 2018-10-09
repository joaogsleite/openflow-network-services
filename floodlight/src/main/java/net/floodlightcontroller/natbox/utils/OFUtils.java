package net.floodlightcontroller.natbox.utils;

import java.util.ArrayList;
import java.util.Collections;

import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.protocol.OFFlowAdd;
import org.projectfloodlight.openflow.protocol.OFPacketOut;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.OFBufferId;
import org.projectfloodlight.openflow.types.OFPort;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.natbox.core.NATRouter;
import net.floodlightcontroller.natbox.core.NatEntry;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.util.FlowModUtils;

public class OFUtils {

	public static void forwardNewPacketsToController(IOFSwitch sw){
		Match match = sw.getOFFactory().buildMatch()
			.setExact(MatchField.ETH_TYPE, EthType.IPv4)
			.build();	
		ArrayList<OFAction> actionList = new ArrayList<OFAction>();
		actionList.add(sw.getOFFactory().actions().buildOutput()
			.setMaxLen(0xffFFffFF)
			.setPort(OFPort.CONTROLLER)
			.build()
		);
		OFFlowAdd flow = sw.getOFFactory().buildFlowAdd().setBufferId(OFBufferId.NO_BUFFER)
			.setHardTimeout(0).setIdleTimeout(0)
			.setOutPort(OFPort.CONTROLLER)
			.setActions(actionList).setMatch(match)
			.setPriority(FlowModUtils.PRIORITY_LOW)
			.build();
		sw.write(flow);
	}

	public static void programNatEntry(NatEntry entry, NATRouter router) {
		IOFSwitch sw = router.L3Router().OFSwitch();
		/*if(entry.getProtocol() == IpProtocol.ICMP) return;

		OFFactory factory = sw.getOFFactory();
		
		Builder builder = factory.buildMatch()
			.setExact(MatchField.ETH_TYPE, EthType.IPv4)
			.setExact(MatchField.IPV4_SRC, entry.getPrivateIP())
			.setExact(MatchField.IPV4_DST, entry.getPublicIP());
		Builder builderBack = factory.buildMatch()
			.setExact(MatchField.ETH_TYPE, EthType.IPv4)
			.setExact(MatchField.IPV4_SRC, entry.getPublicIP())
			.setExact(MatchField.IPV4_DST, entry.getPrivateIP());

		ArrayList<OFAction> actions = new ArrayList<OFAction>();
		actions.add(factory.actions().buildSetField()
			.setField(factory.oxms().buildIpv4Src().setValue(pubGatewayIP).build())
			.build()
		);
		ArrayList<OFAction> actionsBack = new ArrayList<OFAction>();
		actionsBack.add(factory.actions().buildSetField()
			.setField(factory.oxms().buildIpv4Dst().setValue(entry.getPrivateIP()).build())
			.build()
		);
		
		if(entry.getProtocol().equals(IpProtocol.UDP)){
			builder.setExact(MatchField.UDP_SRC, entry.getPrivatePort());
			builder.setExact(MatchField.UDP_DST, entry.getPublicPort());
			builderBack.setExact(MatchField.UDP_SRC, entry.getPublicPort());
			builderBack.setExact(MatchField.UDP_DST, entry.getNatPort());
			actions.add(factory.actions().buildSetField()
				.setField(factory.oxms().buildUdpSrc().setValue(entry.getNatPort()).build())
				.build()
			);
			actionsBack.add(factory.actions().buildSetField()
				.setField(factory.oxms().buildUdpDst().setValue(entry.getPrivatePort()).build())
				.build()
			);
		}
		else if(entry.getProtocol().equals(IpProtocol.TCP)){
			builder.setExact(MatchField.TCP_SRC, entry.getPrivatePort());
			builder.setExact(MatchField.TCP_DST, entry.getPublicPort());
			builderBack.setExact(MatchField.TCP_SRC, entry.getPublicPort());
			builderBack.setExact(MatchField.TCP_DST, entry.getNatPort());
			actions.add(factory.actions().buildSetField()
				.setField(factory.oxms().buildTcpSrc().setValue(entry.getNatPort()).build())
				.build()
			);
			actionsBack.add(factory.actions().buildSetField()
				.setField(factory.oxms().buildTcpDst().setValue(entry.getPrivatePort()).build())
				.build()
			);
		}
		else return;

		/*OFFlowAdd flow = factory.buildFlowAdd().setBufferId(OFBufferId.NO_BUFFER)
			.setHardTimeout(0).setIdleTimeout(0)
			.setActions(actions).setMatch(builder.build())
			.setPriority(FlowModUtils.PRIORITY_MED)
			.build();
		sw.write(flow);

		OFFlowAdd flowBack = factory.buildFlowAdd().setBufferId(OFBufferId.NO_BUFFER)
			.setHardTimeout(0).setIdleTimeout(0)
			.setActions(actionsBack).setMatch(builderBack.build())
			.setPriority(FlowModUtils.PRIORITY_MED)
			.build();
		sw.write(flowBack);*/
	}

	public static void translateAndSendPacket(NATRouter router, Ethernet eth, NatEntry entry) {
		IPv4 ip = (IPv4) eth.getPayload();
		IOFSwitch sw = router.L3Router().OFSwitch();
		
		if(ip.getSourceAddress().equals(entry.getPrivateIP())){
			ip.setSourceAddress(router.getPublicIP());
			eth.setSourceMACAddress(sw.getPort(router.getPublicPort()).getHwAddr());
			ip = PacketUtils.modifyPacketToSendOut(ip, entry);
		}
		else{
			ip.setDestinationAddress(entry.getPrivateIP());
			eth.setDestinationMACAddress(entry.getPrivateMac());
			ip = PacketUtils.modifyPacketToSendIn(ip, entry);
		}
		ip.setChecksum((short)0);
		eth.setPayload(ip);
		OFPacketOut po = sw.getOFFactory().buildPacketOut()
    		.setData(eth.serialize())
    		.setActions(Collections.singletonList((OFAction) sw.getOFFactory().actions().output(OFPort.FLOOD, 0xffFFffFF)))
    		.setInPort(OFPort.CONTROLLER)
    		.build();
		sw.write(po);
	}

}
