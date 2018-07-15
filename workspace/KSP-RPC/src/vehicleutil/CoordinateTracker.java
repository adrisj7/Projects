package vehicleutil;

import core.RPCClientShip;
import krpc.client.RPCException;

public class CoordinateTracker extends RPCClientShip {

	public CoordinateTracker(String serverName, String serverIPAddr, int serverPortRPC, int serverPortStream) {
		super("Marker", serverName, serverIPAddr, serverPortRPC, serverPortStream);
	}

	@Override
	protected void setup() throws RPCException {
		super.setup();
	}

	@Override
	protected void update() throws RPCException {
		System.out.println(flight.getLongitude() + ", " + flight.getLatitude());
	}

	public static void main(String[] args) {
		new CoordinateTracker("RPC Server", "127.0.0.1", 50000, 50001).start();
	}
}
