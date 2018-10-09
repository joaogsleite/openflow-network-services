package net.floodlightcontroller.natbox.core;

import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.TransportPort;

public class NatEntry{

	private IPv4Address privateIP;
	private MacAddress privateMac;
	private IPv4Address publicIP;
	private int privatePort;
	private int publicPort;
	private int natPort;
	private IpProtocol protocol;

	public NatEntry(){
		int max = 59999;
		int min = 56304;
		this.natPort = min + ((int)Math.random()*(max-min+1));
	}

	public int getNatPort(){
		return this.natPort;
	}
	public NatEntry setProtocol(IpProtocol procotol){
		this.protocol = procotol;
		return this;
	}
	public NatEntry setPrivatePort(int port){
		this.privatePort = port;
		return this;
	}
	public NatEntry setPublicPort(int port){
		this.publicPort = port;
		return this;
	}
	public NatEntry setPrivateMac(MacAddress mac){
		this.privateMac = mac;
		return this;
	}
	public IpProtocol getProtocol(){
		return this.protocol;
	}
	public MacAddress getPrivateMac(){
		return this.privateMac;
	}
	public int getPrivatePort(){
		return this.privatePort;
	}
	public int getPublicPort(){
		return this.publicPort;
	}
	
	public NatEntry setPublicIP(IPv4Address ip){
		this.publicIP = ip;
		return this;
	}
	public NatEntry setPrivateIP(IPv4Address ip){
		this.privateIP = ip;
		return this;
	}
	public IPv4Address getPublicIP(){
		return this.publicIP;
	}
	public IPv4Address getPrivateIP(){
		return this.privateIP;
	}
}
