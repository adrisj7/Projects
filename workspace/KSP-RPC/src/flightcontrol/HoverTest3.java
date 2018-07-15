package flightcontrol;

import java.util.LinkedList;

import org.javatuples.Triplet;

import core.PIDController;
import core.PIDController.PIDTimeMode;
import core.RPCClient;
import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Engine;
import krpc.client.services.SpaceCenter.Flight;
import krpc.client.services.SpaceCenter.ReferenceFrame;
import krpc.client.services.SpaceCenter.Vessel;

/*
 * This hover test does what HoverTest2 does
 * 
 * BUT
 * 
 * you can place the engines at any position (assuming they're all on the same plane and facing downward)
 * 
 * letting you make floating islands that aren't just squares
 */

public class HoverTest3 extends RPCClient {

	private static final String VESSEL_NAME = "HoverPad 3.3 Rainbow Road";

	private static final double MAX_POS_FACTOR = 999; // 8...
	// private static final double CLOSE_ENOUGH_POSITION = 1;

	// What's the maximum percent by which an engine can change its thrust per loop?
	// (2 is the theoretical maximum)
	private static final double ENGINE_RAMP_LIMIT = 999; 

	// Rotation
	private static final double K_ROT_P = 0.1, // 1
			K_ROT_I = 0.0, K_ROT_D = 0.08; // 0.08

	// Altitude
	private static final double K_ALT_P = -0.016, K_ALT_I = -0.000001, K_ALT_D = -0.045;

	private Vessel vessel;
	private ReferenceFrame ref;
	private Flight flight;

	private PIDController rollPID, pitchPID;
	private PIDController altitudePID;

	private LinkedList<EngineController> engines;

	public HoverTest3(double altitude, String serverName, String serverIPAddr, int serverPortRPC,
			int serverPortStream) {
		super(serverName, serverIPAddr, serverPortRPC, serverPortStream);
		rollPID = new PIDController(K_ROT_P, K_ROT_I, K_ROT_D, 0, PIDTimeMode.TIME_KSP);
		pitchPID = new PIDController(K_ROT_P, K_ROT_I, K_ROT_D, 0, PIDTimeMode.TIME_KSP);

		altitudePID = new PIDController(K_ALT_P, K_ALT_I, K_ALT_D, 0, 10000000, altitude, PIDTimeMode.TIME_KSP);

		engines = new LinkedList<EngineController>();
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

		ReferenceFrame relativeRef = vessel.getReferenceFrame();

		flight = vessel.flight(ref);

		// Scroll through each engine and assign our engine objects
		for (Engine engine : vessel.getParts().getEngines()) {
			EngineController engineController = new EngineController(engine, relativeRef, flight); 
			engines.add(engineController);
			engineController.init();
		}

		vessel.getControl().setThrottle(1);

	}

	@Override
	protected void update() throws RPCException {
		double roll = flight.getRoll();
		double pitch = -1 * flight.getPitch();

		rollPID.update(roll, spaceCenter);
		pitchPID.update(pitch, spaceCenter);

		altitudePID.update(flight.getMeanAltitude(), spaceCenter);

		float rollVal = (float) rollPID.getValue();
		float pitchVal = (float) pitchPID.getValue();

		float altitudeVal = (float) altitudePID.getValue();
		altitudeVal = Math.max(0, altitudeVal); // lower limit is 0

		// Rotation canceling
		if (closeEnough(rollVal, 0, 0.01)) {
			rollVal = 0;
		}
		if (closeEnough(pitchVal, 0, 0.01)) {
			pitchVal = 0;
		}

//		System.out.println("\n\n==============================");
		for(EngineController engineController : engines) {
			engineController.thrust(altitudeVal, rollVal, pitchVal);
		}
	}

	// Controls engines based on their distance from the center and PID Outputs
	private class EngineController {
		// The engine we control
		private Engine engine;

		// How much roll and pitch affect this thruster by
		private double rollFactor;
		private double pitchFactor;

		private double lastThrustLimit;

		public EngineController(Engine engine, ReferenceFrame ref, Flight flight) throws RPCException {
			this.engine = engine;
			double xpos = engine.getPart().position(ref).getValue0();
			double ypos = engine.getPart().position(ref).getValue1();
//			xpos -= flight.getCenterOfMass().getValue1();
//			ypos -= flight.getCenterOfMass().getValue2();
			// Roll: X, Pitch: Y
			// The closer to the center, the stronger the force
			rollFactor =  xpos;
			pitchFactor = ypos;

			if (Math.abs(rollFactor) > MAX_POS_FACTOR) {
				rollFactor = MAX_POS_FACTOR * Math.signum(rollFactor);
			}
			if (Math.abs(pitchFactor) > MAX_POS_FACTOR) {
				pitchFactor = MAX_POS_FACTOR * Math.signum(pitchFactor);
			}
//			System.out.println("Engine: (" + xpos + ", " + ypos + "), extra : " + extra); //, rollFactor: " + rollFactor + ", pitchFactor: " + pitchFactor);
		}

		public void init() throws RPCException {
			engine.setActive(true);
			engine.setThrustLimit(0);
		}

		public void thrust(double altitudeOutput, double rollOutput, double pitchOutput) throws RPCException {
			rollOutput *= rollFactor;
			pitchOutput *= pitchFactor;
//			rollOutput = Math.max(0, rollOutput);
//			pitchOutput = Math.max(0, pitchOutput);

			// Maybe square them / find their magnitude?
			double output = altitudeOutput + altitudeOutput * (rollOutput + pitchOutput);
			output = Math.max(output, 0);

			if (Math.abs(output - lastThrustLimit) > ENGINE_RAMP_LIMIT) {
				double delta = output - lastThrustLimit;
				output = lastThrustLimit + ENGINE_RAMP_LIMIT * Math.signum(delta);
			}

//			System.out.println("ENGINE: roll: " + rollOutput + ", pitch: " + pitchOutput + " = " + output);
			engine.setThrustLimit((float) (output));

			lastThrustLimit = output;
		}
	}

	public static void main(String[] args) {
		new HoverTest3(70000, "RPC Server", "127.0.0.1", 50000, 50001).start();
	}
}
