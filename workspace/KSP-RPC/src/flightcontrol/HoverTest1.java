package flightcontrol;

import java.util.LinkedList;

import org.javatuples.Triplet;

import core.PIDController;
import core.PIDController.PIDTimeMode;
import core.RPCClient;
import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Flight;
import krpc.client.services.SpaceCenter.ReferenceFrame;
import krpc.client.services.SpaceCenter.Vessel;

public class HoverTest1 extends RPCClient {

	double targetAltitude;
	double kP = -0.026, kI = 0, kD = -0.045;
	double iMin = -10000, iMax = 10000;

	LinkedList<Triplet<PIDController, Vessel, Flight>> controllers;

	public HoverTest1(float targetAltitude, String serverName, String serverIPAddr, int serverPortRPC,
			int serverPortStream) {
		super(serverName, serverIPAddr, serverPortRPC, serverPortStream);

		this.targetAltitude = targetAltitude;
		controllers = new LinkedList<Triplet<PIDController, Vessel, Flight>>();
	}

	@Override
	protected void setup() throws RPCException {
		for (Vessel vessel : spaceCenter.getVessels()) {
			if (vessel.getName().equals("HoverPad")) {

				ReferenceFrame ref = ReferenceFrame.createHybrid(connection, vessel.getOrbit().getBody().getReferenceFrame(),
						vessel.getSurfaceReferenceFrame(), vessel.getOrbit().getBody().getReferenceFrame(),
						vessel.getOrbit().getBody().getReferenceFrame());
				Flight flight = vessel.flight(ref);

				PIDController controller = new PIDController(kP, kI, kD, targetAltitude, iMin, iMax, PIDTimeMode.TIME_SPECIFIED);
				controller.init();

				controllers.add(new Triplet<PIDController, Vessel, Flight>(controller, vessel, flight));
			}
		}
	}

	@Override
	protected void update() throws RPCException {
		for (Triplet<PIDController, Vessel, Flight> controller : controllers) {
			double altitude = controller.getValue2().getElevation();
			controller.getValue0().update(altitude,spaceCenter);
		}
	}

	// Close loop for what throttle to use
	public static void main(String[] args) {
		new HoverTest1(1000, "RPC Server", "127.0.0.1", 50000, 50001).start();
	}
	
}
