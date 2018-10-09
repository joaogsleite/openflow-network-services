package net.floodlightcontroller.l3routing;

import java.awt.List;
import java.io.DataInput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.projectfloodlight.openflow.protocol.OFType;

import net.floodlightcontroller.core.*;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.core.module.*;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.restserver.IRestApiService;

import net.floodlightcontroller.l3routing.web.*;
import net.floodlightcontroller.l3routing.service.*;
import net.floodlightcontroller.l3routing.listeners.*;

public class L3Routing implements IFloodlightModule {

	protected IFloodlightProviderService floodlightProviderService;
	protected IRestApiService restApiService;
	protected IOFSwitchService switchService;
	protected IDeviceService deviceService;
	protected L3RoutingService routingService;

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
    	l.add(IL3RoutingService.class);
    	return l;
	}
	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		Map<Class<? extends IFloodlightService>, IFloodlightService> m = new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
		this.routingService = new L3RoutingService();
    	m.put(IL3RoutingService.class, this.routingService);
    	return m;
	}
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		l.add(IRestApiService.class);
		l.add(IOFSwitchService.class);
		l.add(IDeviceService.class);
        return l;
	}
	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		this.floodlightProviderService = context.getServiceImpl(IFloodlightProviderService.class);
		this.restApiService = context.getServiceImpl(IRestApiService.class);
		this.switchService = context.getServiceImpl(IOFSwitchService.class);
		this.deviceService = context.getServiceImpl(IDeviceService.class);
	}
	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
		restApiService.addRestletRoutable(new L3RoutingWebRoutable());
		floodlightProviderService.addOFMessageListener(OFType.PACKET_IN, new L3ArpListener(this.routingService));
		deviceService.addListener(new L3DeviceListener(this.routingService));
		routingService.addSwitchService(this.switchService);
	}
}
