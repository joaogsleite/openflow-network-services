package net.floodlightcontroller.natbox.listeners;

import org.projectfloodlight.openflow.protocol.OFPortDesc;
import org.projectfloodlight.openflow.types.DatapathId;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.PortChangeType;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.core.internal.OFSwitch;
import net.floodlightcontroller.natbox.core.NATRouter;
import net.floodlightcontroller.natbox.service.INATBoxService;
import net.floodlightcontroller.natbox.utils.OFUtils;

public class NATSwitchListener implements IOFSwitchListener {

	private INATBoxService natboxService;
	private IOFSwitchService switchService;

	public NATSwitchListener(INATBoxService natboxService, IOFSwitchService switchService){
		this.natboxService = natboxService;
		this.switchService = switchService;
	}

	@Override
	public void switchAdded(DatapathId dpid) {
		OFSwitch sw = (OFSwitch) switchService.getSwitch(dpid);
		for(NATRouter router : natboxService.getNats()){
			if(dpid.equals(router.L3Router().OFSwitch().getId())){
				OFUtils.forwardNewPacketsToController(sw);
			}
		}
	}

	@Override
	public void switchRemoved(DatapathId dpid) {
		for(NATRouter router : natboxService.getNats()){
			if(dpid.equals(router.L3Router().OFSwitch().getId())){
				natboxService.disableNat(router.getPublicIP());
			}
		}
	}

	@Override
	public void switchActivated(DatapathId switchId) {
		this.switchAdded(switchId);
	}

	@Override
	public void switchPortChanged(DatapathId switchId, OFPortDesc port, PortChangeType type) {}

	@Override
	public void switchChanged(DatapathId switchId) {}

	@Override
	public void switchDeactivated(DatapathId switchId) {}

}
