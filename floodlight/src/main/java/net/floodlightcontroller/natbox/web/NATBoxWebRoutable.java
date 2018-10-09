package net.floodlightcontroller.natbox.web;

import org.restlet.Restlet;
import net.floodlightcontroller.restserver.RestletRoutable;
import org.restlet.Context;
import org.restlet.routing.Router;

public class NATBoxWebRoutable implements RestletRoutable {
    /**
     * Create the Restlet router and bind to the proper resources.
     */
    @Override
    public Restlet getRestlet(Context context) {
        Router router = new Router(context);

        router.attach("/networks", ListResource.class);
        router.attach("/networks/{network-name}", EnableAndDisableResource.class);
        return router;
    }
 
    /**
     * Set the base path for the Topology
     */
    @Override
    public String basePath() {
        return "/wm/natbox";
    }
}
