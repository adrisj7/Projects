package vehicles;

import core.PIDController;
import core.PIDController.PIDTimeMode;
import core.RPCClientShip;
import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Engine;

public class HoverBoard extends RPCClientShip {

	private static final String VESSEL_NAME = "HoverBoard 1";

	// Surface Altitude
	private static final double K_ALT_P = -0.066, K_ALT_I = 0, K_ALT_D = -0.3;

	private PIDController altitudePID;

	private Engine thruster1, thruster2;
	private Engine lifter1, lifter2;

	public HoverBoard(String serverName, String serverIPAddr, int serverPortRPC, int serverPortStream) {
		super(VESSEL_NAME, serverName, serverIPAddr, serverPortRPC, serverPortStream);

		altitudePID = new PIDController(K_ALT_P, K_ALT_I, K_ALT_D, 5, PIDTimeMode.TIME_KSP);
	}

	@Override
	protected void setup() throws RPCException {
		super.setup();
		
		thruster1 = vessel.getParts().getEngines().get(0);
		thruster2 = vessel.getParts().getEngines().get(1);
		lifter1 = vessel.getParts().getEngines().get(2);
		lifter2 = vessel.getParts().getEngines().get(3);

		thruster1.setThrustLimit(0);
		thruster2.setThrustLimit(0);
		lifter1.setThrustLimit(0);
		lifter2.setThrustLimit(0);
	}

	@Override
	protected void update() throws RPCException {
		altitudePID.update(flight.getSurfaceAltitude(), spaceCenter);

		float val = (float)altitudePID.getValue();
		lifter1.setThrustLimit(val);
		lifter2.setThrustLimit(val);

		if (vessel.getControl().getBrakes()) {
			thruster1.setThrustLimit(0.25f);
			thruster2.setThrustLimit(0.25f);
		} else {
			thruster1.setThrustLimit(0);
			thruster2.setThrustLimit(0);			
		}
	}

	public static void main(String[] args) {
		new HoverBoard("RPC Server", "127.0.0.1", 50000, 50001).start();
	}
}
