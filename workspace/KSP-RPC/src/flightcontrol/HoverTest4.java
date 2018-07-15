package flightcontrol;

import java.util.LinkedList;

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

public class HoverTest4 extends RPCClient {

	private static final String VESSEL_NAME = "HoverPad 2 + Light";
	private static final String TARGET_VESSEL_NAME = "Marker";
	private static final double MAX_POS_FACTOR = 999; // 8...
	// private static final double CLOSE_ENOUGH_POSITION = 1;

	// What's the maximum percent by which an engine can change its thrust per loop?
	// (2 is the theoretical maximum)
	private static final double ENGINE_RAMP_LIMIT = 999; 

	private static final double ROTATE_LIMIT = 45; // Don't rotate beyond this angle

	// Rotation
	private static final double K_ROT_P = 0.1, K_ROT_I = 0.0, K_ROT_D = 0.08;

	// Altitude
	private static final double K_ALT_P = -1 * 5 * 0.016, K_ALT_I = -0.000001, K_ALT_D = -1 * 3 * 0.045;

	// Position
	private static final double K_POS_P = -1 * 2 * 20000, K_POS_I = 0.0, K_POS_D = -1 * 3 * 40000;

	private Vessel vessel;
	private Vessel targetVessel;
	private ReferenceFrame ref;
	private Flight flight;
	private Flight targetFlight;

	private PIDController rollPID, pitchPID;
	private PIDController altitudePID;
	private PIDController positionLatPID, positionLonPID;

	private LinkedList<EngineController> engines;

	private double targetLatitude, targetLongitude;

	public HoverTest4(double altitude, String serverName, String serverIPAddr, int serverPortRPC,
			int serverPortStream) {
		super(serverName, serverIPAddr, serverPortRPC, serverPortStream);

		rollPID = new PIDController(K_ROT_P, K_ROT_I, K_ROT_D, 0, PIDTimeMode.TIME_KSP);
		pitchPID = new PIDController(K_ROT_P, K_ROT_I, K_ROT_D, 0, PIDTimeMode.TIME_KSP);
		altitudePID = new PIDController(K_ALT_P, K_ALT_I, K_ALT_D, 0, 10000000, altitude, PIDTimeMode.TIME_KSP);
		positionLatPID = new PIDController(K_POS_P, K_POS_I, K_POS_D, 0, PIDTimeMode.TIME_KSP);
		positionLonPID = new PIDController(K_POS_P, K_POS_I, K_POS_D, 0, PIDTimeMode.TIME_KSP);

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
			if (vessel.getName().equals(TARGET_VESSEL_NAME)) {
				this.targetVessel = vessel;
			}
		}

		ref = ReferenceFrame.createHybrid(connection, vessel.getOrbit().getBody().getReferenceFrame(),
				vessel.getSurfaceReferenceFrame(), vessel.getOrbit().getBody().getReferenceFrame(),
				vessel.getOrbit().getBody().getReferenceFrame());

		ReferenceFrame relativeRef = vessel.getReferenceFrame();

		flight = vessel.flight(ref);
		
		ReferenceFrame targetRef = ReferenceFrame.createHybrid(connection, targetVessel.getOrbit().getBody().getReferenceFrame(),
				targetVessel.getSurfaceReferenceFrame(), targetVessel.getOrbit().getBody().getReferenceFrame(),
				targetVessel.getOrbit().getBody().getReferenceFrame());
		targetFlight = targetVessel.flight(targetRef);

		// Scroll through each engine and assign our engine objects
		for (Engine engine : vessel.getParts().getEngines()) {
			EngineController engineController = new EngineController(engine, relativeRef, flight); 
			engines.add(engineController);
			engineController.init();
		}

		vessel.getControl().setThrottle(1);

		// Setup position roll/pitch pid displacement
		targetLatitude = flight.getLatitude();// + 0.02;
		targetLongitude = flight.getLongitude();

	}

	@Override
	protected void update() throws RPCException {
		targetLatitude = targetFlight.getLatitude();
		targetLongitude = targetFlight.getLongitude();

		double roll = flight.getRoll();
		double pitch = -1 * flight.getPitch();

		// DISPLACEMENT MATH
		double latitude = flight.getLatitude();
		double longitude = flight.getLongitude();
		double deltaLat = -1 * (targetLatitude - latitude); // good I think
		double deltaLon = -1 * (targetLongitude - longitude); // good I think
//		double theta = Math.toRadians(-1 * flight.getHeading()); // good I think
//		System.out.println("THETA: " + Math.toDegrees(theta));

//		double distanceToTarget = Math.sqrt(deltaLat*deltaLat + deltaLon*deltaLon);
//		double angleToTarget = Math.atan2(deltaLat, deltaLon); // good I think
// 		double deltaSideDistance =  distanceToTarget * Math.cos(angleToTarget - theta);
//		double deltaFrontDistance = distanceToTarget * Math.sin(angleToTarget - theta);

		// Update target roll+pitch
		// These are on the absolute coordinate system
		double latVal = /*-1 * */positionLatPID.getValue();
		double lonVal = /*-1 * */positionLonPID.getValue();
		double theta = Math.toRadians(-1 * flight.getHeading()); // good I think
//		double angleToTarget = Math.atan2(deltaLat, deltaLon); // good I think
		double latLonAngle = Math.atan2(latVal, lonVal);
		double latLonMagnitude = Math.sqrt(latVal*latVal + lonVal*lonVal);
 		double deltaSideDistance =  latLonMagnitude * Math.cos(latLonAngle - theta);
		double deltaFrontDistance = latLonMagnitude * Math.sin(latLonAngle - theta);

		double targetRoll = /*-1 * */deltaSideDistance;
		double targetPitch = /*-1 * */deltaFrontDistance;
		targetRoll = Math.max(-ROTATE_LIMIT, Math.min(targetRoll, ROTATE_LIMIT));
		targetPitch = Math.max(-ROTATE_LIMIT, Math.min(targetPitch, ROTATE_LIMIT));
//		System.out.println("Target Theta: (" + targetRoll + ", " + targetPitch + "). Delta distance: (" + deltaLat + ", " + deltaLon + ")");

		float altitudeVal = (float) altitudePID.getValue();
//		altitudeVal = Math.max(0, altitudeVal); // lower limit is 0

		// UPDATE PID CONTROLLERS

		altitudePID.update(flight.getMeanAltitude(), spaceCenter);

		rollPID.setTarget(targetRoll);
		pitchPID.setTarget(targetPitch);

		rollPID.update(roll, spaceCenter);
		pitchPID.update(pitch, spaceCenter);

		positionLatPID.setTarget(targetLatitude);//targetLatitude);
		positionLonPID.setTarget(targetLongitude);//targetLongitude);
		positionLatPID.update(latitude/*deltaSideDistance*/, spaceCenter);
		positionLonPID.update(longitude/*deltaFrontDistance*/, spaceCenter);
//		System.out.println("delta lon: " + deltaLon);
//		System.out.println("custom val: " + ((positionLonPID.getValue() > 0) ? "POSITIVE" : "neg"));
		System.out.println("custom val: " + positionLonPID.tempGetDPart());
		// GRAB PID CONTROLLER VALUES
		float rollVal = (float) rollPID.getValue();
		float pitchVal = (float) pitchPID.getValue();
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
//			// Scale to a factor that we can use
//			rollTarget = Math.abs(rollTarget / 30.0);
//			pitchTarget = Math.abs(pitchTarget / 30.0);

//			altitudeOutput *= (1 + rollTarget + pitchTarget);

			rollOutput *= rollFactor;
			pitchOutput *= pitchFactor;
//			rollOutput = Math.max(0, rollOutput);
//			pitchOutput = Math.max(0, pitchOutput);

			// Maybe square them / find their magnitude?
			rollOutput = Math.max(rollOutput, 0);
			pitchOutput = Math.max(pitchOutput, 0);
			//double output = altitudeOutput + rollOutput + pitchOutput;
			double output = altitudeOutput + Math.max(0, Math.min(altitudeOutput, 1)) * (rollOutput + pitchOutput);// + 0.2*(Math.abs(rollOutput) + Math.abs(pitchOutput));
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
		new HoverTest4(100, "RPC Server", "127.0.0.1", 50000, 50001).start();
	}
}
