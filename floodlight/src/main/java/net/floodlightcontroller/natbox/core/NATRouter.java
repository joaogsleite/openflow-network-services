package net.floodlightcontroller.natbox.core;

import java.util.TreeMap;

import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IPv4AddressWithMask;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFPort;

import net.floodlightcontroller.l3routing.core.L3Router;
import net.floodlightcontroller.natbox.core.NatEntry;
import net.floodlightcontroller.natbox.utils.PacketUtils;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;

public class NATRouter {

	public TreeMap<IPv4Address,TreeMap<Integer,NatEntry>> privMatch = new TreeMap<>();
	public TreeMap<IPv4Address,TreeMap<Integer,NatEntry>> pubMatch = new TreeMap<>();
	public IPv4Address publicIP;
	public OFPort publicPort;
	public L3Router router;

	public NATRouter(L3Router router, IPv4Address publicIP, OFPort publicPort){
		this.router = router;
		this.publicPort = publicPort;
		this.publicIP = publicIP;
	}
	public NATRouter setPublicIP(IPv4Address ip){
		this.publicIP = ip;
		return this;
	}
	public IPv4Address getPublicIP(){
		return this.publicIP;
	}
	public OFPort getPublicPort(){
		return this.publicPort;
	}
	public NATRouter setPublicPort(OFPort publicPort){
		this.publicPort = publicPort;
		return this;
	}
	public L3Router L3Router(){
		return this.router;
	}
	public NATRouter setupNAT(IPv4Address publicIP){
		this.publicIP = publicIP;
		return this;
	}
	public IPv4AddressWithMask getPrivateNetwork(){
		return this.router.getNetwork();
	}

	private NatEntry addNatEntry(NatEntry entry){
		if(entry==null) return null;
		if(!pubMatch.containsKey(entry.getPublicIP()))
			pubMatch.put(entry.getPublicIP(), new TreeMap<>());
		if(!privMatch.containsKey(entry.getPrivateIP()))
			privMatch.put(entry.getPrivateIP(), new TreeMap<>());
		if(!privMatch.get(entry.getPrivateIP()).containsKey(entry.getPrivatePort())){
			pubMatch.get(entry.getPublicIP()).put(entry.getNatPort(), entry);
			privMatch.get(entry.getPrivateIP()).put(entry.getPrivatePort(), entry);
		}
		return entry;
	}
	public NatEntry getOrGenerateNatEntry(Ethernet packet){
		
		MacAddress srcMac = packet.getSourceMACAddress();
		IpProtocol proto = ((IPv4)packet.getPayload()).getProtocol();
		IPv4Address srcIP = ((IPv4)packet.getPayload()).getSourceAddress();
		IPv4Address dstIP = ((IPv4)packet.getPayload()).getDestinationAddress();
		int srcPort = PacketUtils.getSourcePort(packet);
		int dstPort = PacketUtils.getDestinationPort(packet);

		NatEntry entry = null;
		if(getPrivateNetwork().contains(srcIP) && !getPrivateNetwork().contains(dstIP)){	
			if(privMatch.containsKey(srcIP))
				entry = privMatch.get(srcIP).get(srcPort);
			
			if(entry == null)
				entry = new NatEntry().setPrivateMac(srcMac).setProtocol(proto)
					.setPrivateIP(srcIP).setPublicIP(dstIP)
					.setPrivatePort(srcPort).setPublicPort(dstPort);
		}
		else if(this.publicIP.equals(dstIP)){
			if(pubMatch.containsKey(srcIP))
				entry = pubMatch.get(srcIP).get(dstPort);
		}

		return this.addNatEntry(entry);
	}
}
