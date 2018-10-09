package net.floodlightcontroller.l3routing.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TreeMap;

import javax.annotation.Nonnull;

import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IPv4AddressWithMask;

import net.floodlightcontroller.core.IOFConnectionBackend;
import net.floodlightcontroller.core.internal.IOFSwitchManager;
import net.floodlightcontroller.core.internal.OFSwitch;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceListener;

import net.floodlightcontroller.l3routing.core.*;
import net.floodlightcontroller.l3routing.listeners.*;
import net.floodlightcontroller.l3routing.service.*;

public class L3DeviceListener implements IDeviceListener{

	private IL3RoutingService service;

	public L3DeviceListener(IL3RoutingService service){
		this.service = service;
	}

	@Override
	public void deviceAdded(IDevice device) {
		for(IPv4Address ip : device.getIPv4Addresses()){
			for(IPv4AddressWithMask network : this.service.getNetworks()){
				if(network.contains(ip)){
					this.service.getRouter(network).addHost(device); 
					return;
				}
			}
		}
	}
	@Override
	public void deviceRemoved(IDevice device) {
		for(IPv4AddressWithMask network : this.service.getNetworks()){
				this.service.getRouter(network).removeHost(device); 
		}
	}
	
	@Override public void deviceMoved(IDevice device){
		
		this.deviceAdded(device);
	}
	@Override public void deviceIPV4AddrChanged(IDevice device){
		this.deviceRemoved(device);
		this.deviceAdded(device);
	}
	@Override public void deviceIPV6AddrChanged(IDevice device){}
	@Override public void deviceVlanChanged(IDevice device){
		this.deviceRemoved(device);
		this.deviceAdded(device);
	}
	@Override public boolean isCallbackOrderingPrereq(String type, String name) {
		return false;
	}
	@Override public boolean isCallbackOrderingPostreq(String type, String name) {
		return false;
	}
	@Override public String getName() {
		return "L3DeviceListener";
	}
}
