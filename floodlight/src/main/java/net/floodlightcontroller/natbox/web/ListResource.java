package net.floodlightcontroller.natbox.web;

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
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ListResource extends ServerResource {

    protected static final Logger log = LoggerFactory.getLogger(ListResource.class);
    
    @Get
    public Object list(String jsonString) throws IOException {

        String networkName = (String) getRequestAttributes().get("network-name");

        // TODO: design this endpoint

        return null;
    }
}
