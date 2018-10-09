package net.floodlightcontroller.l3routing.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

import net.floodlightcontroller.l3routing.core.L3Router;
import net.floodlightcontroller.l3routing.service.IL3RoutingService;

import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IPv4AddressWithMask;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SetupAndListResource extends ServerResource {

    protected static final Logger log = LoggerFactory.getLogger(SetupAndListResource.class);
    
    @Get
    public Object list() throws IOException {

        IL3RoutingService service = (IL3RoutingService) getContext().getAttributes().get(IL3RoutingService.class.getCanonicalName());
        Collection<L3Router> routers =  service.getRouters();

        // TODO: convert `routers` to a JSON object to return

        List<Map> maps = new ArrayList<>();
        maps.add(ImmutableMap.of("enabled", true));

        return maps;

    }

    @Post
    @Put
    public Object setup(String jsonString) throws IOException {

        if (jsonString == null) {
            setStatus(org.restlet.data.Status.CLIENT_ERROR_BAD_REQUEST, "One or more required fields missing.");
            return null;
        }

        JsonNode json = new ObjectMapper().readTree(jsonString);
        
        String name                 = json.get("name").asText();
        String gatewayIpString      = json.get("gateway").asText();
        String networkWithMask      = json.get("network").asText();
        String switchIdString       = json.get("switch").asText();

        IPv4AddressWithMask network = IPv4AddressWithMask.of(networkWithMask);
        DatapathId switchId         = DatapathId.of(switchIdString);
        IPv4Address gateway         = IPv4Address.of(gatewayIpString);
        

        IL3RoutingService service = (IL3RoutingService) getContext().getAttributes().get(IL3RoutingService.class.getCanonicalName());
        service.setupNetwork(name, network, gateway, switchId);

        List<Map> maps = new ArrayList<>();
        maps.add(ImmutableMap.of("enabled", true));

        return maps;
    }
}
