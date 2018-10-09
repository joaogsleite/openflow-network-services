package net.floodlightcontroller.natbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.projectfloodlight.openflow.protocol.OFType;

import net.floodlightcontroller.core.*;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.core.module.*;
import net.floodlightcontroller.l3routing.service.IL3RoutingService;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.natbox.listeners.*;
import net.floodlightcontroller.natbox.service.*;
import net.floodlightcontroller.natbox.web.NATBoxWebRoutable;

public class NATBox implements IFloodlightModule {

	protected IFloodlightProviderService floodlightProviderService;
	protected IRestApiService restApiService;
	protected IOFSwitchService switchService;
	protected NATBoxService natBoxService;
	protected IL3RoutingService l3routingService;

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
    	l.add(INATBoxService.class);
    	return l;
	}
	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		Map<Class<? extends IFloodlightService>, IFloodlightService> m = new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
		this.natBoxService = new NATBoxService();
    	m.put(INATBoxService.class, this.natBoxService);
    	return m;
	}
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		l.add(IL3RoutingService.class);
		l.add(IRestApiService.class);
		l.add(IOFSwitchService.class);
        return l;
	}
	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		this.floodlightProviderService = context.getServiceImpl(IFloodlightProviderService.class);
		this.restApiService = context.getServiceImpl(IRestApiService.class);
		this.switchService = context.getServiceImpl(IOFSwitchService.class);
		this.l3routingService = context.getServiceImpl(IL3RoutingService.class);
	}
	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
		restApiService.addRestletRoutable(new NATBoxWebRoutable());
		floodlightProviderService.addOFMessageListener(OFType.PACKET_IN, new IPv4Listener(this.natBoxService));
		switchService.addOFSwitchListener(new NATSwitchListener(this.natBoxService, this.switchService));
		natBoxService.addl3RoutingService(this.l3routingService);
	}
}
