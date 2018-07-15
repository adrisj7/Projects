package flightcontrol;

import java.util.List;

import core.PIDController;
import core.PIDController.PIDTimeMode;
import core.RPCClient;
import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Engine;
import krpc.client.services.SpaceCenter.Flight;
import krpc.client.services.SpaceCenter.ReferenceFrame;
import krpc.client.services.SpaceCenter.Vessel;

/*
 * This hover test makes the thing stay at a certain altitude
 * 
 * AND
 * 
 * maintain rotation through its engines
 * 
 * MEANING
 * 
 * 
 * you can land another aircraft on top of it while it's flying
 * 
 */

public class HoverTest2 extends RPCClient {

	private static final String VESSEL_NAME = "HoverPad 2.1";
	
	private static final double CLOSE_ENOUGH_POSITION = 1;

	// Rotation
	private static final double K_ROT_P = 1, // 0.2
								K_ROT_I = 0,
								K_ROT_D = 0.1;

	// Altitude
	private static final double K_ALT_P = -0.016,
								K_ALT_I = 0,
								K_ALT_D = -0.045;

	private Vessel vessel;
	private ReferenceFrame ref;
	private Flight flight;

	// Relative to y axis pointing up, x pointing right
	private Engine xFrontEngine, xBackEngine, yTopEngine, yBottomEngine;

	private PIDController rollPID, pitchPID;
	private PIDController altitudePID;

	public HoverTest2(double altitude, String serverName, String serverIPAddr, int serverPortRPC, int serverPortStream) {
		super(serverName, serverIPAddr, serverPortRPC, serverPortStream);
		rollPID = new PIDController(K_ROT_P, K_ROT_I, K_ROT_D, 0, PIDTimeMode.TIME_KSP);
		pitchPID = new PIDController(K_ROT_P, K_ROT_I, K_ROT_D, 0, PIDTimeMode.TIME_KSP);

		altitudePID = new PIDController(K_ALT_P, K_ALT_I, K_ALT_D, altitude, PIDTimeMode.TIME_KSP);
	}

	private static boolean closeEnough(double a, double b, double closeEnough) {
		return Math.abs(a - b) < closeEnough;
	}

	@Override
	protected void setup() throws RPCException {

		for (Vessel vessel : spaceCenter.getVessels()) {
			if (vessel.getName().equals(VESSEL_NAME)) {
				this.vessel = vessel;
			}
		}

		ref = ReferenceFrame.createHybrid(connection, vessel.getOrbit().getBody().getReferenceFrame(),
				vessel.getSurfaceReferenceFrame(), vessel.getOrbit().getBody().getReferenceFrame(),
				vessel.getOrbit().getBody().getReferenceFrame());
		// vessel.getSurfaceVelocityReferenceFrame();
		flight = vessel.flight(ref);

		// Scroll through each engine and assign our engine objects
		List<Engine> engines = vessel.getParts().getEngines();
		for (Engine engine : engines) {
			double xpos = engine.getPart().position(ref).getValue1();
			double ypos = engine.getPart().position(ref).getValue2();
			System.out.println("(" + xpos + ", " + ypos + ")");
			if (!closeEnough(xpos, 0, CLOSE_ENOUGH_POSITION)) {
				if (xpos < 0) {
					xFrontEngine = engine;
				} else {
					xBackEngine = engine;
				}
			} else {
				if (ypos > 0) {
					yTopEngine = engine;
				} else {
					yBottomEngine = engine;
				}
			}
			engine.setActive(true);
			engine.setThrustLimit(0);
		}

		vessel.getControl().setThrottle(1);

	}

	@Override
	protected void update() throws RPCException {
		double roll = flight.getRoll();
		double pitch = -1 * flight.getPitch();

		rollPID.update(roll, spaceCenter);
		pitchPID.update(pitch,spaceCenter);

		altitudePID.update(flight.getSurfaceAltitude(), spaceCenter);

		float rollVal = (float)rollPID.getValue();
		float pitchVal = (float)pitchPID.getValue();

		float altitudeVal = (float)altitudePID.getValue();
		altitudeVal = Math.max(0, altitudeVal); // lower limit is 0

		// Rotation canceling
		if (closeEnough(rollVal, 0, 0.01)) {
			rollVal = 0;
		}
		xFrontEngine.setThrustLimit(altitudeVal * (1.0f+rollVal));
		xBackEngine.setThrustLimit(altitudeVal * (1.0f-rollVal));			
		if (closeEnough(pitchVal, 0, 0.01)) {
			pitchVal = 0;
		}
		yTopEngine.setThrustLimit(altitudeVal * (1.0f+pitchVal));
		yBottomEngine.setThrustLimit(altitudeVal * (1.0f-pitchVal));
		
//		if (!closeEnough(rollVal, 0, 0.01)) {
//			if (rollVal > 0) {
//				xFrontEngine.setThrustLimit(altitudeVal + rollVal);
//				xBackEngine.setThrustLimit(altitudeVal);
//			} else {
//				xFrontEngine.setThrustLimit(altitudeVal);
//				xBackEngine.setThrustLimit(altitudeVal + -1 * rollVal);			
//			}
//		} else {
//			yTopEngine.setThrustLimit(altitudeVal);
//			yBottomEngine.setThrustLimit(altitudeVal);
//		}
//
//		if (!closeEnough(pitchVal, 0, 0.01)) {
//			if (pitchVal > 0) {
//				yTopEngine.setThrustLimit(altitudeVal + pitchVal);
//				yBottomEngine.setThrustLimit(altitudeVal);
//			} else {
//				yTopEngine.setThrustLimit(altitudeVal);
//				yBottomEngine.setThrustLimit(altitudeVal + -1 * pitchVal);			
//			}
//		} else {
//			yTopEngine.setThrustLimit(altitudeVal);
//			yBottomEngine.setThrustLimit(altitudeVal);
//		}

	}

	public static void main(String[] args) {
		new HoverTest2(1000,"RPC Server", "127.0.0.1", 50000, 50001).start();
	}	
}
