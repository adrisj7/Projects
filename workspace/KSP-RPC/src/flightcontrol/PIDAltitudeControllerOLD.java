package flightcontrol;

import krpc.client.Connection;
import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Flight;
import krpc.client.services.SpaceCenter.ReferenceFrame;
import krpc.client.services.SpaceCenter.Vessel;

public class PIDAltitudeControllerOLD {

	private Vessel vessel;
	private ReferenceFrame ref;
	private Flight flight;

	private double targetAltitude;
	private double kP = -0.026, kI = 0, kD = -0.045;

	private double kIaccumulator = 0;

	public PIDAltitudeControllerOLD(Connection connection, Vessel vessel, double targetAltitude) throws RPCException {
		this.vessel = vessel;
		this.targetAltitude = targetAltitude;

		ref = ReferenceFrame.createHybrid(connection, vessel.getOrbit().getBody().getReferenceFrame(),
				vessel.getSurfaceReferenceFrame(), vessel.getOrbit().getBody().getReferenceFrame(),
				vessel.getOrbit().getBody().getReferenceFrame());
		// vessel.getSurfaceVelocityReferenceFrame();
		flight = vessel.flight(ref);
	}

	public void init() throws RPCException {
		vessel.getParts().getEngines().get(0).setActive(true);
		vessel.getControl().setSAS(true);
		vessel.getControl().setRCS(false);
		vessel.getControl().setThrottle(0);
		//vessel.getAutoPilot().engage();
		//vessel.getAutoPilot().targetPitchAndHeading(90, 90);
	}

	public void update() throws RPCException {
		float loopOutput = Math.min(Math.max(getThrottleLoop(), 0), 1);
		vessel.getControl().setThrottle(loopOutput);
	}

	private float getThrottleLoop() throws RPCException {

		double altitude = flight.getSurfaceAltitude();
		double dTarget = altitude - targetAltitude;
		// System.out.println(kP + " * " + dTarget + " = " + (dTarget * kP));
		double vel = flight.getVelocity().getValue0();

		kIaccumulator += dTarget;

		return (float) (kP * dTarget + kI * kIaccumulator + kD * vel);
	}

}
