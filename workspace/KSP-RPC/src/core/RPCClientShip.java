package core;

import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Flight;
import krpc.client.services.SpaceCenter.ReferenceFrame;
import krpc.client.services.SpaceCenter.Vessel;

public abstract class RPCClientShip extends RPCClient {

	private String vesselName;

	protected Vessel vessel;
	protected ReferenceFrame ref;
	protected Flight flight;

	public RPCClientShip(String vesselName, String serverName, String serverIPAddr, int serverPortRPC, int serverPortStream) {
		super(serverName, serverIPAddr, serverPortRPC, serverPortStream);
		this.vesselName = vesselName;
	}

	// When we're calling our own vessel
	public RPCClientShip(String serverName, String serverIPAddr, int serverPortRPC, int serverPortStream) {
	    this("", serverName, serverIPAddr, serverPortRPC, serverPortStream);
	}


	@Override
	protected void setup() throws RPCException {

	    if (vesselName.equals("")) {
	        // If our name is empty, we're just using the current vessel
	        this.vessel = spaceCenter.getActiveVessel();
	    } else {
            // Otherwise, find our vessel
    		for (Vessel vessel : spaceCenter.getVessels()) {
    			if (vessel.getName().equals(vesselName)) {
    				this.vessel = vessel;
    			}
    		}
	    }

		if (vessel == null) {
			throw new NullPointerException("Cannot find vessel of name \"" + vesselName + "\".");
		}

		ref = ReferenceFrame.createHybrid(connection, vessel.getOrbit().getBody().getReferenceFrame(),
				vessel.getSurfaceReferenceFrame(), vessel.getOrbit().getBody().getReferenceFrame(),
				vessel.getOrbit().getBody().getReferenceFrame());

		flight = vessel.flight(ref);

	}
}
