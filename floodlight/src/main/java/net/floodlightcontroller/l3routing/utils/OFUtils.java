package net.floodlightcontroller.l3routing.utils;

import java.util.ArrayList;
import java.util.Collections;

import org.projectfloodlight.openflow.protocol.OFFlowAdd;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.protocol.instruction.OFInstructionApplyActions;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.*;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.devicemanager.*;
import net.floodlightcontroller.l3routing.core.*;
import net.floodlightcontroller.util.FlowModUtils;

public class OFUtils {

	public static void setupFlood(L3Router router, EthType type){
		Match match = router.getOFFactory().buildMatch()
			.setExact(MatchField.ETH_TYPE, type)
			.build();
		
		OFFlowAdd flow = router.getOFFactory().buildFlowAdd()
			.setBufferId(OFBufferId.NO_BUFFER)
			.setHardTimeout(0).setIdleTimeout(0)
			.setOutPort(OFPort.FLOOD)
			.setActions(Collections.singletonList(
				router.getOFFactory().actions().buildOutput()
				.setMaxLen(0xffFFffFF).setPort(OFPort.FLOOD)
				.build()
			))
			.setMatch(match)
			.setPriority(FlowModUtils.PRIORITY_LOW)
			.build();	
			
		router.write(flow);
	}

	public static void setupNetworkForwarding(L3Router router){

		Match match = router.getOFFactory().buildMatch()
			.setExact(MatchField.ETH_TYPE, EthType.IPv4)
			.setMasked(MatchField.IPV4_SRC, router.getNetwork())
			.build();
		
		OFFlowAdd flow = router.getOFFactory().buildFlowAdd()
			.setBufferId(OFBufferId.NO_BUFFER)
			.setHardTimeout(L3OFRulesTask.PERIOD).setIdleTimeout(0)
			.setOutPort(OFPort.FLOOD)
			.setActions(Collections.singletonList(
				router.getOFFactory().actions().buildOutput()
				.setMaxLen(0xffFFffFF).setPort(OFPort.FLOOD)
				.build()
			))
			.setMatch(match)
			.setPriority(FlowModUtils.PRIORITY_MED_HIGH)
			.build();
		router.write(flow);
	}

	public static void setupUnknownHostsPacketsToController(L3Router router){
		
		Match match = router.getOFFactory().buildMatch()
			.setExact(MatchField.ETH_TYPE, EthType.IPv4)
			.setMasked(MatchField.IPV4_DST, router.getNetwork())
			.build();	

		OFFlowAdd flow = router.getOFFactory().buildFlowAdd()
			.setBufferId(OFBufferId.NO_BUFFER)
			.setHardTimeout(L3OFRulesTask.PERIOD).setIdleTimeout(0)
			.setOutPort(OFPort.CONTROLLER)
			.setActions(Collections.singletonList(
				router.getOFFactory().actions().buildOutput()
				.setMaxLen(0xffFFffFF).setPort(OFPort.CONTROLLER)
				.build()
			))
			.setMatch(match)
			.setPriority(FlowModUtils.PRIORITY_MED_LOW)
			.build();
		router.write(flow);
	}

	public static void setupArpReqToController(L3Router router){
		
		Match match = router.getOFFactory().buildMatch()
			.setExact(MatchField.ETH_TYPE, EthType.ARP)
			.setExact(MatchField.ARP_OP, ArpOpcode.REQUEST)
			.setExact(MatchField.ARP_TPA, router.getGateway())
			.build();	

		OFFlowAdd flow = router.getOFFactory().buildFlowAdd()
			.setBufferId(OFBufferId.NO_BUFFER)
			.setHardTimeout(L3OFRulesTask.PERIOD).setIdleTimeout(0)
			.setOutPort(OFPort.CONTROLLER)
			.setActions(Collections.singletonList(
				router.getOFFactory().actions().buildOutput()
				.setMaxLen(0xffFFffFF).setPort(OFPort.CONTROLLER)
				.build()
			))
			.setMatch(match)
			.setPriority(FlowModUtils.PRIORITY_HIGH)
			.build();
		router.write(flow);
	}

	public static void setupTargetHostMacChanger(L3Router router, IDevice host){
		
		OFPort port = null;
		MacAddress mac = host.getMACAddress();
		for(SwitchPort point : host.getAttachmentPoints())
			if(router.getId().equals(point.getNodeId()))
				port = point.getPortId();

		for(IPv4Address ip : host.getIPv4Addresses()){

			Match match = router.getOFFactory().buildMatch()
				.setExact(MatchField.ETH_TYPE, EthType.IPv4)
				.setExact(MatchField.IPV4_DST, ip)
				.build();
			
			ArrayList<OFAction> actionList = new ArrayList<OFAction>();
			actionList.add(
				router.getOFFactory().actions().buildSetField()
				.setField(router.getOFFactory().oxms().buildEthDst().setValue(mac).build())
				.build()
			);
			actionList.add(router.getOFFactory().actions().buildOutput()
				.setMaxLen(0xffFFffFF).setPort(port).build()
			);
			OFInstructionApplyActions applyActions = router.getOFFactory().instructions()
				.buildApplyActions().setActions(actionList).build();

			OFFlowAdd flow = router.getOFFactory().buildFlowAdd()
				.setBufferId(OFBufferId.NO_BUFFER)
				.setHardTimeout(L3OFRulesTask.PERIOD).setIdleTimeout(0)
				.setOutPort(port)
				.setPriority(FlowModUtils.PRIORITY_HIGH)
				.setMatch(match)
				.setInstructions(Collections.singletonList(applyActions))
				.build();
			router.write(flow);	
		}	
	}
}
