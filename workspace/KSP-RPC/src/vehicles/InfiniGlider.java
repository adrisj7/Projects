package vehicles;

import core.PIDController;
import core.RPCClientShip;
import core.PIDController.PIDTimeMode;
import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Engine;

public class InfiniGlider extends RPCClientShip {

	// Surface Altitude
	private static final double K_ALT_P = -0.86, K_ALT_I = 0, K_ALT_D = -0.40;

	private Engine engineThrust, engineTop, engineBottom;

	private PIDController altitudePID;

	public InfiniGlider(String serverName, String serverIPAddr, int serverPortRPC,
			int serverPortStream) {
		super("InfiniGlider 1", serverName, serverIPAddr, serverPortRPC, serverPortStream);
		altitudePID = new PIDController(K_ALT_P, K_ALT_I, K_ALT_D, 10, PIDTimeMode.TIME_KSP);
	}

	@Override
	public void setup() throws RPCException {
		super.setup();
		engineThrust = vessel.getParts().getEngines().get(0);
		engineTop = vessel.getParts().getEngines().get(2);
		engineBottom = vessel.getParts().getEngines().get(1);
		engineTop.setThrustLimit(0.5f);
	}

	@Override
	protected void update() throws RPCException {
		altitudePID.update(flight.getSurfaceAltitude(), spaceCenter);
		float val = (float)altitudePID.getValue();

		engineBottom.setThrustLimit(val);
		engineTop.setThrustLimit(-1 * val);
	}

	public static void main(String[] args) {
		new InfiniGlider("RPC Server", "127.0.0.1", 50000, 50001).start();
	}


}
