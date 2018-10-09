package net.floodlightcontroller.natbox.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

import net.floodlightcontroller.l3routing.core.L3Router;
import net.floodlightcontroller.l3routing.service.IL3RoutingService;
import net.floodlightcontroller.natbox.core.NATRouter;
import net.floodlightcontroller.natbox.service.INATBoxService;

import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IPv4AddressWithMask;
import org.projectfloodlight.openflow.types.OFPort;
import org.restlet.resource.*;
import org.restlet.resource.ServerResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnableAndDisableResource extends ServerResource {

    protected static final Logger log = LoggerFactory.getLogger(EnableAndDisableResource.class);
    
    @Put
    @Post
    public Object enable(String jsonString) throws IOException {

        if (jsonString == null) {
            setStatus(org.restlet.data.Status.CLIENT_ERROR_BAD_REQUEST, "One or more required fields missing.");
            return null;
        }

        String networkName = (String) getRequestAttributes().get("network-name");
        JsonNode json      = new ObjectMapper().readTree(jsonString);
        String ip          = json.get("public-ip").asText();
        int port           = json.get("public-port").asInt();

        INATBoxService natbox       = (INATBoxService) getContext().getAttributes().get(INATBoxService.class.getCanonicalName());
        IL3RoutingService l3routing = (IL3RoutingService) getContext().getAttributes().get(IL3RoutingService.class.getCanonicalName());
        
        L3Router router             = l3routing.getRouter(networkName);
        IPv4Address publicIP        = IPv4Address.of(ip);
        OFPort publicPort           = OFPort.of(port);
        IPv4AddressWithMask network = router.getNetwork();

        natbox.enableNat(network, publicIP, publicPort);

        List<Map> maps = new ArrayList<>();
        maps.add(ImmutableMap.of("enabled", true));

        return maps;
    }

    @Delete
    public Object disable(String jsonString) throws IOException {

        if (jsonString == null) {
            setStatus(org.restlet.data.Status.CLIENT_ERROR_BAD_REQUEST, "One or more required fields missing.");
            return null;
        }

        String networkName = (String) getRequestAttributes().get("network-name");

        INATBoxService natbox = (INATBoxService) getContext().getAttributes().get(INATBoxService.class.getCanonicalName());
        IL3RoutingService l3routing = (IL3RoutingService) getContext().getAttributes().get(IL3RoutingService.class.getCanonicalName());
        
        L3Router router =  l3routing.getRouter(networkName);
        IPv4AddressWithMask network = router.getNetwork();

        NATRouter nat = natbox.getNat(network);
        IPv4Address publicIP = nat.getPublicIP();
        natbox.disableNat(publicIP);

        List<Map> maps = new ArrayList<>();
        maps.add(ImmutableMap.of("disabled", true));

        return maps;
    }
}
