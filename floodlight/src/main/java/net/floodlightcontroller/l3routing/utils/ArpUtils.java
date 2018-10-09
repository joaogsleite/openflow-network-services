package net.floodlightcontroller.l3routing.utils;

import java.util.Collections;

import org.projectfloodlight.openflow.protocol.OFPacketOut;
import org.projectfloodlight.openflow.types.ArpOpcode;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFBufferId;
import org.projectfloodlight.openflow.types.OFPort;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.packet.ARP;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPacket;

public class ArpUtils {

	public static void replyToArpRequest(IOFSwitch sw, OFPort inPort, Ethernet eth){
		if(eth.getEtherType()==EthType.ARP)
			replyToArpRequest(sw, inPort, (ARP)eth.getPayload());
	}

	public static void replyToArpRequest(IOFSwitch sw, OFPort inPort, ARP packet) {
		MacAddress swMacAddress = sw.getPort(inPort).getHwAddr();

		IPacket arpReplyPacket = new Ethernet()
			.setEtherType(EthType.ARP)
			.setSourceMACAddress(swMacAddress)
			.setDestinationMACAddress(packet.getSenderHardwareAddress())
			.setPayload(new ARP()
				.setHardwareType(ARP.HW_TYPE_ETHERNET)
				.setProtocolType(ARP.PROTO_TYPE_IP)
				.setHardwareAddressLength((byte) 6)
				.setProtocolAddressLength((byte) 4)
				.setOpCode(ARP.OP_REPLY)
				.setSenderHardwareAddress(swMacAddress)
				.setSenderProtocolAddress(packet.getTargetProtocolAddress())
				.setTargetHardwareAddress(packet.getSenderHardwareAddress())
				.setTargetProtocolAddress(packet.getSenderProtocolAddress())
			);

		OFPacketOut arpReply = sw.getOFFactory().buildPacketOut()
			.setBufferId(OFBufferId.NO_BUFFER)
			.setInPort(OFPort.ANY)
			.setActions(Collections.singletonList(
				sw.getOFFactory().actions()
				.output(inPort, 0xffFFffFF)
			))
			.setData(arpReplyPacket.serialize())
			.build();

		sw.write(arpReply);
	}

	public static boolean isArpRequest(Ethernet eth){
		if(eth.getEtherType() == EthType.ARP){
			ARP packet = (ARP) eth.getPayload();
			return packet.getOpCode() == ArpOpcode.REQUEST;
		}
		else return false;
	}
}
