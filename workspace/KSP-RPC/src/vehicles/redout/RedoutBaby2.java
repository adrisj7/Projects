package vehicles.redout;

import java.util.LinkedList;
import java.util.List;

import org.javatuples.Triplet;

import core.PIDController;
import core.PIDController.PIDTimeMode;
import core.RPCClientShip;
import core.RPCMath;
import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.CameraMode;
import krpc.client.services.SpaceCenter.CelestialBody;
import krpc.client.services.SpaceCenter.Engine;
import krpc.client.services.SpaceCenter.Flight;
import krpc.client.services.SpaceCenter.ReferenceFrame;
import util.Gamepad;

public abstract class RedoutBaby2 extends RPCClientShip {

//	private static final String VESSEL_NAME = "Redout Baby 2";

	private static final double HOVER_ALTITUDE = 5;

	// Rotation
	private static final double K_ROT_P = 0.02 * 0.05, // 1
			K_ROT_I = 0.0, K_ROT_D = 0.005 * 0.05; // 0.08

	// Altitude
	private static final double K_ALT_P = -0.016 * 10 * 0.04, K_ALT_I = -0.000001 * 0, K_ALT_D = -0.045 * 5 * 0.05;

	// Side stopper
	private static final double K_SIDE_P = -0.1 * 0.05, K_SIDE_I = 0, K_SIDE_D = 0;

	// Turning velocity
    private static final double K_TURN_P = -0.1 * 0.05, K_TURN_I = 0, K_TURN_D = 0;

    private boolean allowFlying = true;
    private boolean sideAestheticRotation = true;
    private double turnSpeed = 1.6;
    private double wipeoutRegularSideForce = 1; // Wipeout only
    private double wipeoutAirbrakeEngineForce = 0; // Wipeout only
    private double wipeoutSpeedClampSpeed = 0;
    private double wipeoutSpeedClampFactor = 0;

    // For thrusters on the top vs thrusters on the bottom
    private double topToBottomRatio;
    
	protected Gamepad gamepad;
	public enum ControlMode {
	    REDOUT,
	    WIPEOUT
	};
	private ControlMode controlMode;

	private boolean isPlayerControlling = true; // Are we controlled by the player?

	private LinkedList<EngineController> bottomEngines;
	private LinkedList<Engine> topEngines;
	private LinkedList<Engine> thrustEngines;
	// Side Engines
	private LinkedList<Engine> rightSideEngines;
	private LinkedList<Engine> leftSideEngines;
	// Rotation Engines
	private LinkedList<Engine> rightRotationEngines;
	private LinkedList<Engine> leftRotationEngines;
	// Airbrake Engines (only Wipeout)
    private LinkedList<Engine> airbrakeLeftEngines;
    private LinkedList<Engine> airbrakeRightEngines;


	private PIDController rollPID, pitchPID;
	private PIDController altitudePID;
    private PIDController sidePID;
    private PIDController turnPID;

	protected ReferenceFrame relativeRef;

	private double lastAltitudeVal = 0;
	private double ALTITUDE_VAL_THRESHOLD = 1;//0.01;

	public RedoutBaby2(String vesselName, String serverName, String serverIPAddr, int serverPortRPC,
			int serverPortStream) {
		super(vesselName, serverName, serverIPAddr, serverPortRPC, serverPortStream);

		gamepad = new Gamepad(0);

		bottomEngines = new LinkedList<EngineController>();
		topEngines = new LinkedList<Engine>();
		thrustEngines = new LinkedList<Engine>();
		rightSideEngines = new LinkedList<Engine>();
		leftSideEngines = new LinkedList<Engine>();
		rightRotationEngines = new LinkedList<Engine>();
		leftRotationEngines = new LinkedList<Engine>();
		airbrakeLeftEngines = new LinkedList<Engine>();
        airbrakeRightEngines = new LinkedList<Engine>();

		rollPID = new PIDController(K_ROT_P, K_ROT_I, K_ROT_D, 0, PIDTimeMode.TIME_KSP);
		pitchPID = new PIDController(K_ROT_P, K_ROT_I, K_ROT_D, 0, PIDTimeMode.TIME_KSP);

		altitudePID = new PIDController(K_ALT_P, K_ALT_I, K_ALT_D, 0, 10000000, HOVER_ALTITUDE, PIDTimeMode.TIME_KSP);
		sidePID = new PIDController(K_SIDE_P, K_SIDE_I, K_SIDE_D, 0, PIDTimeMode.TIME_KSP);
		turnPID = new PIDController(K_TURN_P, K_TURN_I, K_TURN_D, 0, PIDTimeMode.TIME_KSP);

		controlMode = ControlMode.REDOUT;
	}

	public abstract void assignEngine(int id, Engine engine, List<Engine> thrustEngines, List<Engine> topEngines, List<Engine> rightSideEngines, List<Engine> leftSideEngines, List<Engine> rightRotationEngines, List<Engine> leftRotationEngines, List<EngineController> bottomEngines, List<Engine> airbrakeLeftEngines, List<Engine> airbrakeRightEngines) throws RPCException;

	@Override
	public void setup() throws RPCException {
		super.setup();

		// For engines
		relativeRef = vessel.getReferenceFrame();

		List<Engine> engines = vessel.getParts().getEngines();
		int id = 0;
		for(Engine engine : engines) {
            assignEngine(id, engine, thrustEngines, topEngines, rightSideEngines, leftSideEngines, rightRotationEngines, leftRotationEngines, bottomEngines, airbrakeLeftEngines, airbrakeRightEngines);

			engine.setThrustLimit(0);

			id++;
		}

		vessel.getControl().setThrottle(1);
		System.out.println(vessel.getControl().getCurrentStage());
		if (spaceCenter.getActiveVessel() == vessel)
		    vessel.getControl().activateNextStage();
		
		topToBottomRatio = (double) topEngines.size() / bottomEngines.size();

		// Camera
		spaceCenter.getCamera().setMode(CameraMode.AUTOMATIC);
	}

	@Override
	protected void update() throws RPCException {

	    
		/// INPUTS
		float inputThrust = 0;
		float turning = 0;
		float rise = 0;
		float side = 0;
		float wipeoutBrakeLeft = 0;
		float wipeoutBrakeRight = 0;
		if (isPlayerControlling) {
    		switch(controlMode) {
    		    case REDOUT:
    		        inputThrust =  gamepad.getComponents().getAxes().rt;
    		        turning  =  threshAxis(gamepad.getComponents().getAxes().lx);
    		        rise =      threshAxis(gamepad.getComponents().getAxes().ry);
    		        side =      threshAxis(gamepad.getComponents().getAxes().rx);
    		        break;
    		    case WIPEOUT:
                    inputThrust =  gamepad.getComponents().getButtons().a ? 1 : 0;
                    turning  =  threshAxis(gamepad.getComponents().getAxes().lx);
                    rise =      threshAxis(gamepad.getComponents().getAxes().ly);
                    side =      threshAxis(gamepad.getComponents().getAxes().rt - gamepad.getComponents().getAxes().lt);
                    wipeoutBrakeLeft = gamepad.getComponents().getAxes().lt;
                    wipeoutBrakeRight = gamepad.getComponents().getAxes().rt;
                    if (Math.abs(rise) < 0.5)
                        rise = 0;
                    rise *= 0.8; // No
    		}
		}

		/// SHIP VALUES
		double altitude = flight.getSurfaceAltitude();
		double roll = flight.getRoll();
		double pitch = -1 * flight.getPitch();
		double headingVel = vessel.angularVelocity(ref).getValue0();
		double sideVel = getSideVel();
		double frontVel = getFrontVel();
		double sideAngle = 0;//getSideAngle();

//		double groundRoll = getSideAngle();

        // PID Targets
		pitchPID.setTarget(rise * 15);
		rollPID.setTarget(sideAngle + (sideAestheticRotation ? (Math.max(-20, Math.min(side * 5, 20))) : 0)); // For effect, really
		sidePID.setTarget(controlMode == ControlMode.REDOUT ? (side * 12) : 0); // Don't move left/right with airbrakes in wipeout mode
		turnPID.setTarget(turnSpeed * turning);

//		System.out.println("pitch: " + pitch +
//		        ", " + pitchPID.getTarget());

		// PID Updates
		rollPID.update(roll, spaceCenter);
		pitchPID.update(pitch, spaceCenter);
		altitudePID.update(altitude, spaceCenter);
		sidePID.update(sideVel, spaceCenter);
		turnPID.update(headingVel, spaceCenter);

		// PID Values
		float rollVal =     (float) rollPID.getValue();
		float pitchVal =    (float) pitchPID.getValue();
		float altitudeVal = (float) altitudePID.getValue();
		float sideVal =     (float) sidePID.getValue();
		float turnVal =     (float) turnPID.getValue();

//		System.out.println(altitudeVal);

		// If we're upside down, bad stuff happens to our altitude
		if (Math.abs(roll) > 80 || Math.abs(pitch) > 80) {
		    altitudeVal = 0;
		    sideVal = 0;
		    turnVal = 0;
		}

	    // Altitude adjustment/deWHAMMing
        if (Math.abs(altitudeVal - lastAltitudeVal) > ALTITUDE_VAL_THRESHOLD) {
            altitudeVal = (float) (lastAltitudeVal + Math.signum(altitudeVal - lastAltitudeVal) * ALTITUDE_VAL_THRESHOLD);
        }

		//System.out.println(rollVal + ", " + pitchVal + ", " + altitudeVal + ", " + sideVal + ", " + turnVal);

		// Rotation canceling
		if (Math.abs(rollVal) < 0.01) {
			rollVal = 0;
		}
		if (Math.abs(pitchVal) < 0.01) {
			pitchVal = 0;
		}

		if (allowFlying) {
            if (altitude - altitudePID.getTarget() > 15) {
                altitudeVal *= 0.5; // Artificial thing that prevents us from SLAMMING down
            }
		}

		// BOTTOM ENGINE HOVERING
		for(EngineController engineController : bottomEngines) {
			engineController.thrust(altitudeVal * topToBottomRatio, rollVal, pitchVal);
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
			engine.setThrustLimit(inputThrust);
		}

		// TURNING
		for(Engine engine : leftRotationEngines) {
			engine.setThrustLimit(turnVal);
		}
		for(Engine engine : rightRotationEngines) {
			engine.setThrustLimit(-1 * turnVal);
		}

	    // Airbrakes
		double airBrakeSlowDown = (frontVel > wipeoutSpeedClampSpeed) ? ((frontVel - wipeoutSpeedClampSpeed) * wipeoutSpeedClampFactor) : 0;
        for(Engine engine : airbrakeLeftEngines) {
            engine.setThrustLimit((float) (airBrakeSlowDown + wipeoutBrakeLeft * wipeoutAirbrakeEngineForce * frontVel / 50f));
        }
        for(Engine engine : airbrakeRightEngines) {
            engine.setThrustLimit((float) (airBrakeSlowDown + wipeoutBrakeRight * wipeoutAirbrakeEngineForce * frontVel / 50f));
        }
		
		// SIDE TO SIDE
        if (controlMode == ControlMode.WIPEOUT) {
            if (Math.abs(side) < 0.1) {
                // Clamp our side vel to the maximum wipeout side vel, if we're not airbreaking in wipeout mode.
                sideVal = (float) (Math.min(Math.abs(sideVal), wipeoutRegularSideForce) * Math.signum(sideVal));
            }
        }
		for(Engine engine : leftSideEngines) {
            engine.setThrustLimit(sideVal);
		}
		for(Engine engine : rightSideEngines) {
            engine.setThrustLimit(-1 * sideVal);
		}

		// Camera
		if (isPlayerControlling && spaceCenter.getCamera().getMode() == CameraMode.AUTOMATIC) {
    		float cameraHeading = spaceCenter.getCamera().getHeading();
    		float deltaAngle = flight.getHeading() - cameraHeading;
    		deltaAngle = (float) (deltaAngle - Math.floor(deltaAngle / 360.0) * 360.0);
    		if (deltaAngle > 180) {
    		    deltaAngle = -1 * (360 - deltaAngle);
    		}
    		cameraHeading += 0.1 * deltaAngle;
    		spaceCenter.getCamera().setHeading(cameraHeading);
    		spaceCenter.getCamera().setDistance((float) (10 + 0.1*Math.max(0, frontVel)) );
		}

		lastAltitudeVal = altitudeVal;
	}

	/**
	 * Calculates and spits out our ship's side velocity
	 * @return our ship's side velocity (positive is in the RIGHT direction)
	 * @throws RPCException
	 */
	private double getSideVel() throws RPCException {
	       // RELATIVE TO AIR STRIP: 
        // vx points to the right, vy points forward
        // theta is clockwise, airstrip forward is 0 degrees
        double vx = -1 * vessel.velocity(ref).getValue1();
        double vy = vessel.velocity(ref).getValue2();
        double theta = flight.getHeading() - 90;

        double velMagnitude = Math.sqrt(vx * vx + vy * vy);
        double velAngle = Math.toDegrees(Math.atan2(vy, vx));

        double angleRelative = velAngle - (-1 * theta) - 90;

        return -1 * velMagnitude * Math.sin(Math.toRadians(angleRelative));
	}
	
	/**
	 * Calculates and spits out ships forward velocity
	 * @return
	 * @throws RPCException
	 */
	private double getFrontVel() throws RPCException {
        double vx = -1 * vessel.velocity(ref).getValue1();
        double vy = vessel.velocity(ref).getValue2();
        double theta = flight.getHeading() - 90;

        double velMagnitude = Math.sqrt(vx * vx + vy * vy);
        double velAngle = Math.toDegrees(Math.atan2(vy, vx));

        double angleRelative = velAngle - (-1 * theta);

        return velMagnitude * Math.sin(Math.toRadians(angleRelative));
	}

	// This is jank...
	private double getSideAngle() throws RPCException {
	    Triplet<Double, Double, Double> euler = RPCMath.quaternionToEuler(vessel.rotation(ref));
	    System.out.printf("%.02f\t %.02f\t %.02f\n", Math.toDegrees(euler.getValue0()), Math.toDegrees(euler.getValue1()), Math.toDegrees(euler.getValue2()));
	    return 0;
	    /*CelestialBody b = vessel.getOrbit().getBody();
	    Triplet<Double, Double, Double> position = vessel.position(ref);
	    double theta = Math.toRadians(flight.getHeading() + 90);
	    //System.out.println(Math.toDegrees(theta));
	    double distance = 0.0004;

	    Triplet<Double, Double, Double> other = new Triplet<>(position.getValue0() + distance * Math.cos(theta), position.getValue1() + distance * Math.sin(theta), position.getValue2());

	    double altHere = b.altitudeAtPosition(position, ref);
	    double altThere = b.altitudeAtPosition(other, ref);

	    double theoreticalAngle = -1 * Math.toDegrees(Math.atan(1 * (altThere - altHere) / distance));
	    theoreticalAngle = -1 * 60 * Math.pow(Math.abs(theoreticalAngle) / 60.0, 2) * Math.signum(theoreticalAngle);
	    return theoreticalAngle;
	    */
	}

	// Applies a deadzone/threshold to the gamepad 
	private float threshAxis(float val) {
	    if (Math.abs(val) < 0.05f) {
	        return 0;
	    }
	    return val;
	}

	static class EngineController {
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
			output = Math.max(0, output);

			/*if (Math.abs(output - lastThrustLimit) > ENGINE_RAMP_LIMIT) {
				double delta = output - lastThrustLimit;
				output = lastThrustLimit + ENGINE_RAMP_LIMIT * Math.signum(delta);
			}*/

//			System.out.println("ENGINE: roll: " + rollOutput + ", pitch: " + pitchOutput + " = " + output);
			engine.setThrustLimit((float) (output));

			lastThrustLimit = output;
		}
	}


	protected void setTargetAltitude(double target) {
	    altitudePID.setTarget(target);
	}

	protected void setAltitudePID(double kP, double kI, double kD) {
	    altitudePID.setPID(kP, kI, kD);
	}

	protected void setTurnPID(double kP, double kI, double kD) {
	    turnPID.setPID(kP, kI, kD);
	}

	protected void setRotatePID(double kP, double kI, double kD) {
	    pitchPID.setPID(kP, kI, kD);
        rollPID.setPID(kP, kI, kD);
	}

	protected void setSidePID(double kP, double kI, double kD) {
	    sidePID.setPID(kP, kI, kD);
	}
	
	protected void setTurnSpeed(double turnSpeed) {
	    this.turnSpeed = turnSpeed;
	}
	
	protected void allowFlying() {
	    allowFlying = true;
	}

	protected void disallowFlying() {
	    allowFlying = false;
	}

	protected void setSideAestheticRotation(boolean sideAestheticRotation) {
        this.sideAestheticRotation = sideAestheticRotation;
    }

	protected void setInputMode(ControlMode controlMode) {
	    this.controlMode = controlMode;
	}
	
	protected void setWipeoutRegularSideForce(float wipeoutRegularSideForce) {
	    this.wipeoutRegularSideForce = wipeoutRegularSideForce;
	}
	protected void setWipeoutAirbrakeEngineForce(float wipeoutAirbrakeEngineForce) {
	    this.wipeoutAirbrakeEngineForce = wipeoutAirbrakeEngineForce;
	}
	protected void setWipeoutSpeedClamp(double wipeoutSpeedClampSpeed, double wipeoutSpeedClampFactor) {
	    this.wipeoutSpeedClampSpeed = wipeoutSpeedClampSpeed;
	    this.wipeoutSpeedClampFactor = wipeoutSpeedClampFactor;
	}
	
	protected void setMainPlayer(boolean isPlayerControlling) {
	    this.isPlayerControlling = isPlayerControlling; 
	}


	
}
