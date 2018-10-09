package net.floodlightcontroller.l3routing.core;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IPv4AddressWithMask;
import net.floodlightcontroller.core.internal.OFSwitch;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceListener;
import net.floodlightcontroller.l3routing.utils.OFUtils;

public class L3OFRulesTask extends TimerTask {

	public static final int PERIOD = 10 * 1000;
	private L3Router router;
	private Timer timer;

	public L3OFRulesTask(L3Router router){
		this.router = router;
		this.timer = new Timer();
		timer.schedule(this, 0, PERIOD);

		OFUtils.setupFlood(this.router, EthType.ARP);
		OFUtils.setupFlood(this.router, EthType.LLDP);
	}

	@Override
	public void run(){
		
		for(IDevice host: this.router.getHosts())
			OFUtils.setupTargetHostMacChanger(this.router, host);
		
		OFUtils.setupUnknownHostsPacketsToController(this.router);
		OFUtils.setupArpReqToController(this.router);
		OFUtils.setupNetworkForwarding(this.router);
	}

	public void clean(){
		this.cancel();
		this.timer.purge();
	}
}
