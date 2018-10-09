package net.floodlightcontroller.natbox.utils;

import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.TransportPort;

import net.floodlightcontroller.natbox.core.NatEntry;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.UDP;
import net.floodlightcontroller.packet.TCP;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.ICMP;

public class PacketUtils {
	
	public static IPv4 modifyPacketToSendOut(IPv4 ip, NatEntry entry){
		if(ip.getProtocol().equals(IpProtocol.UDP)){
			UDP packet = ((UDP)ip.getPayload());
			packet.setSourcePort(TransportPort.of(entry.getNatPort()));
			packet.setChecksum((short)0);
			ip.setPayload(packet);
		}
		else if(ip.getProtocol().equals(IpProtocol.TCP)){
			TCP packet = ((TCP)ip.getPayload());
			packet.setSourcePort(TransportPort.of(entry.getNatPort()));
			packet.setChecksum((short)0);
			ip.setPayload(packet);
		}
		else if(ip.getProtocol().equals(IpProtocol.ICMP)){
			ICMP packet = ((ICMP)ip.getPayload());
			packet.setIcmpIdentifier((short)entry.getNatPort());
			packet.setChecksum((short)0);
			ip.setPayload(packet);
		}
		return ip;
	}
	public static IPv4 modifyPacketToSendIn(IPv4 ip, NatEntry entry){
		if(ip.getProtocol().equals(IpProtocol.UDP)){
			UDP packet = ((UDP)ip.getPayload());
			packet.setDestinationPort(TransportPort.of(entry.getPrivatePort()));
			packet.setChecksum((short)0);
			ip.setPayload(packet);
		}
		else if(ip.getProtocol().equals(IpProtocol.TCP)){
			TCP packet = ((TCP)ip.getPayload());
			packet.setDestinationPort(TransportPort.of(entry.getPrivatePort()));
			packet.setChecksum((short)0);
			ip.setPayload(packet);
		}
		else if(ip.getProtocol().equals(IpProtocol.ICMP)){
			ICMP packet = ((ICMP)ip.getPayload());
			packet.setIcmpIdentifier((short)entry.getPrivatePort());
			packet.setChecksum((short)0);
			ip.setPayload(packet);
		}
		return ip;
	}
	public static Integer getDestinationPort(Ethernet packet){
		IPv4 ip = (IPv4) packet.getPayload();

		if(ip.getProtocol().equals(IpProtocol.TCP)){
			return ((TCP)ip.getPayload()).getDestinationPort().getPort();
		}
		if(ip.getProtocol().equals(IpProtocol.UDP)){
			return ((UDP)ip.getPayload()).getDestinationPort().getPort();
		}
		if(ip.getProtocol().equals(IpProtocol.ICMP)){
			return ((ICMP)ip.getPayload()).getIcmpIdentifier();
		}
		return 0;
	}
	public static Integer getSourcePort(Ethernet packet){
		IPv4 ip = (IPv4) packet.getPayload();

		if(ip.getProtocol().equals(IpProtocol.TCP)){
			return ((TCP)ip.getPayload()).getSourcePort().getPort();
		}
		if(ip.getProtocol().equals(IpProtocol.UDP)){
			return ((UDP)ip.getPayload()).getSourcePort().getPort();
		}
		if(ip.getProtocol().equals(IpProtocol.ICMP)){
			return ((ICMP)ip.getPayload()).getIcmpIdentifier();
		}
		return 0;
	}
}
