package vehicles;

import java.util.List;

import core.PIDController;
import core.RPCClient;
import core.PIDController.PIDTimeMode;
import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Engine;
import krpc.client.services.SpaceCenter.Flight;
import krpc.client.services.SpaceCenter.ReactionWheel;
import krpc.client.services.SpaceCenter.ReferenceFrame;
import krpc.client.services.SpaceCenter.Thruster;
import krpc.client.services.SpaceCenter.Vessel;

public class BikeStabilizer extends RPCClient {

	private static final String VESSEL_NAME = "Rocket Bike";

	private static final float SIDE_THRUST_LIMIT= 0.6f;
	
	// Rotation
	private static final double K_ROT_P = 0.1, // 1
			K_ROT_I = 0.0, K_ROT_D = 0.0; // 0.08

	private Vessel vessel;
	private ReferenceFrame ref;
	private Flight flight;

//	private ReactionWheelController reactionWheels;

	private PIDController rollPID;

	private Engine mainThruster, leftPusher, rightPusher, topPusher;

	public BikeStabilizer(String serverName, String serverIPAddr, int serverPortRPC, int serverPortStream) {
		super(serverName, serverIPAddr, serverPortRPC, serverPortStream);
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

		flight = vessel.flight(ref);

		rollPID = new PIDController(K_ROT_P, K_ROT_I, K_ROT_D, 0, PIDTimeMode.TIME_KSP);

//		reactionWheels = new ReactionWheelController(vessel.getParts().getReactionWheels());

//		vessel.getAutoPilot().engage();
//		vessel.getAutoPilot().setTargetRoll(0);
//		vessel.getAutoPilot().setPitchPIDGains(new Triplet<Double, Double, Double>(0d, 0d, 0d));
//		vessel.getAutoPilot().setYawPIDGains(new Triplet<Double, Double, Double>(0d, 0d, 0d));	

//		vessel.getAutoPilot().setAutoTune(false);
//		vessel.getAutoPilot().setRollPIDGains(new Triplet<Double, Double, Double>(0d,0d,0d));

		mainThruster = vessel.getParts().getEngines().get(0);
		rightPusher = vessel.getParts().getEngines().get(1);
		leftPusher = vessel.getParts().getEngines().get(2);
		topPusher = vessel.getParts().getEngines().get(3);

		rightPusher.setActive(true);
		leftPusher.setActive(true);
		topPusher.setActive(true);
	}

	@Override
	protected void update() throws RPCException {
		rollPID.update(flight.getRoll(), spaceCenter);


//		rightPusher.setThrustLimit(1);
//		leftPusher.setThrustLimit(1);
		float val = (float)rollPID.getValue();

		if (val < -1 * SIDE_THRUST_LIMIT) {
			val = -1 * SIDE_THRUST_LIMIT;
		} else if (val > SIDE_THRUST_LIMIT) {
			val = SIDE_THRUST_LIMIT;
		}

		if (val > 0) {
			rightPusher.setThrustLimit(val);
			leftPusher.setThrustLimit(0);
		} else {
			rightPusher.setThrustLimit(0);
			leftPusher.setThrustLimit(-1 * val);
		}

		if (vessel.getControl().getBrakes()) {
			mainThruster.setThrustLimit(0);
		} else {
			mainThruster.setThrustLimit(0.5f);
		}

		// If our upwards velocity is too high, stop that stuff
		if (vessel.velocity(ref).getValue0() > 0.5f) {
			topPusher.setThrustLimit(1);
		} else {
			topPusher.setThrustLimit(0);
		}
	}

//	private static class ReactionWheelController {
//		private List<ReactionWheel> wheels;
//
//		public ReactionWheelController(List<ReactionWheel> wheels) {
//			this.wheels = wheels;
//		}
//
//		public void applyRoll(double power) {
//			for(ReactionWheel wheel : wheels) {
//				wheel.
//			}
//		}
//
//	}

	public static void main(String[] args) {
		new BikeStabilizer("RPC Server", "127.0.0.1", 50000, 50001).start();
	}
}
