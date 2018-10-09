package net.floodlightcontroller.l3routing.web;

import org.restlet.Restlet;
import net.floodlightcontroller.restserver.RestletRoutable;
import org.restlet.Context;
import org.restlet.routing.Router;

public class L3RoutingWebRoutable implements RestletRoutable {
    /**
     * Create the Restlet router and bind to the proper resources.
     */
    @Override
    public Restlet getRestlet(Context context) {
        Router router = new Router(context);

        router.attach("/networks", SetupAndListResource.class);
        router.attach("/networks/{network-name}", EditAndRemoveResource.class);
        return router;
    }
 
    /**
     * Set the base path for the Topology
     */
    @Override
    public String basePath() {
        return "/wm/l3routing";
    }
}
