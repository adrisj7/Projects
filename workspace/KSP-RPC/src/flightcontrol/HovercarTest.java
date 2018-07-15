package flightcontrol;

import java.util.List;

import core.RPCClient;
import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Engine;
import krpc.client.services.SpaceCenter.Flight;
import krpc.client.services.SpaceCenter.ReferenceFrame;
import krpc.client.services.SpaceCenter.Vessel;

public class HovercarTest extends RPCClient {

	private static final double CLOSE_ENOUGH = 0.012;

	private Vessel vessel;
	private ReferenceFrame ref;
	private Flight flight;

	private Engine bottomEngine; // Main hoverer
	private Engine leftEngine;
	private Engine rightEngine;
	private Engine frontEngine;
	private Engine backEngine;

	public HovercarTest(String serverName, String serverIPAddr, int serverPortRPC, int serverPortStream) {
		super(serverName, serverIPAddr, serverPortRPC, serverPortStream);
	}

	@Override
	protected void setup() throws RPCException {
		vessel = spaceCenter.getActiveVessel();
		ref = ReferenceFrame.createHybrid(connection, vessel.getOrbit().getBody().getReferenceFrame(),
				vessel.getSurfaceReferenceFrame(), vessel.getOrbit().getBody().getReferenceFrame(),
				vessel.getOrbit().getBody().getReferenceFrame());
		// vessel.getSurfaceVelocityReferenceFrame();
		flight = vessel.flight(ref);

		List<Engine> engines = vessel.getParts().getEngines();
		for (Engine engine : engines) {
			// If facing downward
			double leftright = engine.getPart().direction(ref).getValue1();
			double frontback = engine.getPart().direction(ref).getValue2();
			double vertical = engine.getPart().direction(ref).getValue0();
			System.out.println("engine: " + engine.getPart().direction(ref));
			if (closeEnough(vertical, 1)) {
				bottomEngine = engine;
			} else if (closeEnough(leftright,1)) {
				rightEngine = engine;
			} else if (closeEnough(leftright, -1)) {
				leftEngine = engine;
			} else if (closeEnough(frontback, 1)) {
				frontEngine = engine;
			} else if (closeEnough(frontback, -1)) {
				backEngine = engine;
			} else {
				System.out.println("ENGINE UNNACOUNTED FOR: " + engine.getPart().direction(ref));
				continue;
			}
			engine.setActive(true);
			engine.setThrustLimit(0); // controls engines
		}
		vessel.getControl().setThrottle(1);

	}

	@Override
	protected void update() throws RPCException {
		double mass = vessel.getMass();
		double fG = mass * vessel.getOrbit().getBody().getSurfaceGravity();

		bottomEngine.setThrustLimit((float) (fG / bottomEngine.getMaxThrust()) );

		double forceDown = bottomEngine.getThrust();

		double roll = Math.toRadians(flight.getRoll());
		double pitch = Math.toRadians(flight.getPitch());
		System.out.println(flight.getRoll() + ", " + flight.getPitch());

		double desiredForceLeftRight = (fG - forceDown * Math.cos(roll)) / Math.sin(roll);
		double desiredForceFrontBack = -(fG - forceDown * Math.cos(pitch)) / Math.sin(pitch);

		if (desiredForceLeftRight > 1) {
			leftEngine.setThrustLimit(0);
			float throttle = (float) (Math.abs(desiredForceLeftRight) / rightEngine.getMaxThrust());
			rightEngine.setThrustLimit(throttle);
		} else if (desiredForceLeftRight < 1) {
			rightEngine.setThrustLimit(0);
			float throttle = (float) (Math.abs(desiredForceLeftRight) / leftEngine.getMaxThrust());
			leftEngine.setThrustLimit(throttle);
		}

		if (desiredForceFrontBack > 1) {
			frontEngine.setThrustLimit(0);
			float throttle = (float) (Math.abs(desiredForceLeftRight) / backEngine.getMaxThrust());
			backEngine.setThrustLimit(throttle);
		} else if (desiredForceLeftRight < 1) {
			backEngine.setThrustLimit(0);
			float throttle = (float) (Math.abs(desiredForceLeftRight) / frontEngine.getMaxThrust());
			frontEngine.setThrustLimit(throttle);
		}

	}

	private static boolean closeEnough(double a, double b) {
		return Math.abs(a - b) < CLOSE_ENOUGH;
	}
	
	public static void main(String[] args) {
		new HovercarTest("RPC Server", "127.0.0.1", 50000, 50001).start();
	}

}
