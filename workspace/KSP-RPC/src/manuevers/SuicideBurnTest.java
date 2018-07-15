package manuevers;

import org.javatuples.Triplet;

import core.RPCClient;
import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Flight;
import krpc.client.services.SpaceCenter.ReferenceFrame;
import krpc.client.services.SpaceCenter.Vessel;

public class SuicideBurnTest extends RPCClient {

	private Vessel vessel;
	private ReferenceFrame ref;
	private Flight flight;

	public SuicideBurnTest(String serverName, String serverIPAddr, int serverPortRPC, int serverPortStream) {
		super(serverName, serverIPAddr, serverPortRPC, serverPortStream);
	}

	@Override
	protected void setup() throws RPCException {
		vessel = spaceCenter.getActiveVessel();
		ref = ReferenceFrame.createHybrid(connection, vessel.getOrbit().getBody().getReferenceFrame(),
				vessel.getSurfaceReferenceFrame(), vessel.getOrbit().getBody().getReferenceFrame(),
				vessel.getOrbit().getBody().getReferenceFrame());
		flight = vessel.flight(ref);
	}

	@Override
	protected void update() throws RPCException {
		double height = flight.getSurfaceAltitude();
		Triplet<Double,Double,Double> vel = flight.getVelocity();
		// TODO: Somehow suicide burn... in 3 axis.... huh

	}

}
