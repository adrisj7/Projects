package vehicles;

import java.util.LinkedList;
import java.util.List;

import core.PIDController;
import core.PIDController.PIDTimeMode;
import core.RPCClientShip;
import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Engine;
import krpc.client.services.SpaceCenter.Flight;
import krpc.client.services.SpaceCenter.ReferenceFrame;
import util.Gamepad;

public class RedoutBaby extends RPCClientShip {

	private static final String VESSEL_NAME = "Redout Baby";

	private static final double HOVER_ALTITUDE = 10;

	// Rotation
	private static final double K_ROT_P = 0.1 * 5, // 1
			K_ROT_I = 0.0, K_ROT_D = 0.08; // 0.08

	// Altitude
	private static final double K_ALT_P = -0.016 * 10, K_ALT_I = -0.000001, K_ALT_D = -0.045 * 5;

	private Gamepad gamepad;

	private LinkedList<EngineController> bottomEngines;
	private LinkedList<Engine> topEngines;
	private LinkedList<Engine> thrustEngines;
	// Side Engines
	private LinkedList<Engine> rightSideEngines;
	private LinkedList<Engine> leftSideEngines;
	// Rotation Engines
	private LinkedList<Engine> rightRotationEngines;
	private LinkedList<Engine> leftRotationEngines;

	private PIDController rollPID, pitchPID;
	private PIDController altitudePID;

	private ReferenceFrame relativeRef;
	
	public RedoutBaby(String serverName, String serverIPAddr, int serverPortRPC,
			int serverPortStream) {
		super(VESSEL_NAME, serverName, serverIPAddr, serverPortRPC, serverPortStream);

		gamepad = new Gamepad(0);

		bottomEngines = new LinkedList<EngineController>();
		topEngines = new LinkedList<Engine>();
		thrustEngines = new LinkedList<Engine>();
		rightSideEngines = new LinkedList<Engine>();
		leftSideEngines = new LinkedList<Engine>();
		rightRotationEngines = new LinkedList<Engine>();
		leftRotationEngines = new LinkedList<Engine>();

		rollPID = new PIDController(K_ROT_P, K_ROT_I, K_ROT_D, 0, PIDTimeMode.TIME_KSP);
		pitchPID = new PIDController(K_ROT_P, K_ROT_I, K_ROT_D, 0, PIDTimeMode.TIME_KSP);

		altitudePID = new PIDController(K_ALT_P, K_ALT_I, K_ALT_D, 0, 10000000, HOVER_ALTITUDE, PIDTimeMode.TIME_KSP);
	}

	@Override
	public void setup() throws RPCException {
		super.setup();

		// For engines
		relativeRef = vessel.getReferenceFrame();

		List<Engine> engines = vessel.getParts().getEngines();
		for(Engine engine : engines) {
			String name = engine.getPart().getName();
			if (name.equals("smallRadialEngine")) {
				bottomEngines.add(new EngineController(engine, relativeRef, flight));
			} else if (name.equals("liquidEngine2")) {
				thrustEngines.add(engine);
			} else if (name.equals("toroidalAerospike")){
				topEngines.add(engine);
			} else if (name.equals("omsEngine")){
				if (engine.getPart().rotation(relativeRef).getValue2() < 0) {
					rightRotationEngines.add(engine);
				} else {
					leftRotationEngines.add(engine);
				}
			} else if (name.equals("radialLiquidEngine1-2")) {
				// NOTE: Inverted
				if (engine.getPart().rotation(relativeRef).getValue3() < 0) {
					leftSideEngines.add(engine);
				} else {
					rightSideEngines.add(engine);
				}
			} else {
				System.out.println("UNIDENTIFIED ENGINE NAME: " + name + ", " + engine.getPart().rotation(relativeRef).getValue2());
			}

			engine.setThrustLimit(0);
		}

		vessel.getControl().setThrottle(1);
		//vessel.getAutoPilot().engage();
	}

	@Override
	protected void update() throws RPCException {

		/// INPUTS
		float rtrigger = gamepad.getComponents().getAxes().rt;
		float turning  = gamepad.getComponents().getAxes().lx;
		float rise = -1 * gamepad.getComponents().getAxes().ry;
		float side = gamepad.getComponents().getAxes().rx;

		/// PID
		double roll = flight.getRoll();
		double pitch = -1 * flight.getPitch();
		
		pitchPID.setTarget(Math.max(-20, Math.min(rise * 15, 20)));
		rollPID.setTarget(Math.max(-20, Math.min(turning * 5, 20))); // For effect, really

		rollPID.update(roll, spaceCenter);
		pitchPID.update(pitch, spaceCenter);
		altitudePID.update(flight.getSurfaceAltitude(), spaceCenter);
		float rollVal = (float) rollPID.getValue();
		float pitchVal = (float) pitchPID.getValue();
		float altitudeVal = (float) altitudePID.getValue();

//		altitudeVal = Math.max(0, altitudeVal); // lower limit is 0

		// Rotation canceling
		if (Math.abs(rollVal) < 0.01) {
			rollVal = 0;
		}
		if (Math.abs(pitchVal) < 0.01) {
			pitchVal = 0;
		}

		// BOTTOM ENGINE HOVERING
		for(EngineController engineController : bottomEngines) {
			engineController.thrust(altitudeVal, rollVal, pitchVal);
		}
		
		// TOP GO DOWN
		for(Engine engine : topEngines) {
			if (altitudeVal < 0) {
				engine.setThrustLimit(-1 * altitudeVal);
			} else {
				engine.setThrustLimit(0);				
			}
		}

		// THRUSTING
		for(Engine engine : thrustEngines) {
			engine.setThrustLimit(rtrigger);
		}

		// MAGIC FORCE PUSHING IT ALL AROUND?

		/*
		for(ReactionWheel wheel : vessel.getParts().getReactionWheels()) {
			wheel.getPart().instantaneousForce(new Triplet<Double, Double, Double>(0.0, 0.0, 10 * 100000.0 * 1 * altitudeVal), new Triplet<Double, Double, Double>(0.0,0.0,0.0), relativeRef);
			// Rotation?
//			wheel.getPart().addForce(new Triplet<Double, Double, Double>(0.0, 0.0, -100000.0 * rollVal), new Triplet<Double, Double, Double>(100.0,0.0,0.0), relativeRef);

		}
		*/


		// TURNING
		for(Engine engine : leftRotationEngines) {
			engine.setThrustLimit(turning);
		}
		for(Engine engine : rightRotationEngines) {
			engine.setThrustLimit(-turning);
		}

		// SIDE TO SIDE
		for(Engine engine : leftSideEngines) {
			engine.setThrustLimit(side);
		}
		for(Engine engine : rightSideEngines) {
			engine.setThrustLimit(-side);
		}

//		vessel.getAutoPilot().setTargetPitch(0);
//		vessel.getAutoPilot().setTargetRoll(0);

//		vessel.getAutoPilot().setTargetPitch((float)pitchPID.getTarget());
//		vessel.getAutoPilot().setTargetRoll((float)rollPID.getTarget());
//		vessel.getAutoPilot().setTargetHeading(flight.getHeading() + 90*turning);

	}

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

			// Roll: X, Pitch: Y
			// The closer to the center, the weaker the force
			rollFactor =  xpos;
			pitchFactor = ypos;

			/*if (Math.abs(rollFactor) > MAX_POS_FACTOR) {
				rollFactor = MAX_POS_FACTOR * Math.signum(rollFactor);
			}
			if (Math.abs(pitchFactor) > MAX_POS_FACTOR) {
				pitchFactor = MAX_POS_FACTOR * Math.signum(pitchFactor);
			}*/
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

			altitudeOutput = Math.max(0, altitudeOutput);
			// Maybe square them / find their magnitude?
			double output = altitudeOutput + (altitudeOutput*0.7 + 0.3) * (rollOutput + pitchOutput);
			output = Math.max(output, 0);

			/*if (Math.abs(output - lastThrustLimit) > ENGINE_RAMP_LIMIT) {
				double delta = output - lastThrustLimit;
				output = lastThrustLimit + ENGINE_RAMP_LIMIT * Math.signum(delta);
			}*/

//			System.out.println("ENGINE: roll: " + rollOutput + ", pitch: " + pitchOutput + " = " + output);
			engine.setThrustLimit((float) (output));

			lastThrustLimit = output;
		}
	}

	public static void main(String[] args) {
		new RedoutBaby("RPC Server", "127.0.0.1", 50000, 50001).start();
	}
	
}
