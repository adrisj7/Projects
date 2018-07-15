package flightcontrol;

import java.util.LinkedList;

import core.RPCClient;
import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Vessel;

public class AltitudeTestOLD extends RPCClient {

	double targetAltitude;
	LinkedList<PIDAltitudeControllerOLD> controllers;

	public AltitudeTestOLD(float targetAltitude, String serverName, String serverIPAddr, int serverPortRPC,
			int serverPortStream) {
		super(serverName, serverIPAddr, serverPortRPC, serverPortStream);
		this.targetAltitude = targetAltitude;
		controllers = new LinkedList<PIDAltitudeControllerOLD>();
	}

	@Override
	protected void setup() throws RPCException {
		for (Vessel vessel : spaceCenter.getVessels()) {
			if (vessel.getName().equals("HoverPad")) {
				PIDAltitudeControllerOLD controller = new PIDAltitudeControllerOLD(connection, vessel, targetAltitude);
				controller.init();

				controllers.add(controller);
			}
		}

	}

	@Override
	protected void update() throws RPCException {
		for (PIDAltitudeControllerOLD controller : controllers) {
			controller.update();
		}
	}

	// Close loop for what throttle to use
	public static void main(String[] args) {
		new AltitudeTestOLD(500, "RPC Server", "127.0.0.1", 50000, 50001).start();
	}

}
