package core;

import java.io.IOException;

import krpc.client.Connection;
import krpc.client.RPCException;
import krpc.client.services.KRPC;
import krpc.client.services.SpaceCenter;

public abstract class RPCClient {

	protected Connection connection;
	protected SpaceCenter spaceCenter;

	private boolean running = false;

	public RPCClient(String serverName, String serverIPAddr, int serverPortRPC, int serverPortStream) {
		try {
			connection = Connection.newInstance(serverName, serverIPAddr, serverPortRPC, serverPortStream);
			spaceCenter = SpaceCenter.newInstance(connection);
			System.out.println("Connected to kRPC version " + KRPC.newInstance(connection).getStatus().getVersion());
		} catch (IOException | RPCException e) {
			System.out.println("I really don't see the point in catching this regularly...");
			e.printStackTrace();
		}
	}

	public void start() {
		if (running)
			return;

		running = true;
		try {
			setup();
			loop();
		} catch (RPCException e) {
			System.out.println("idk what's up fam, it threw the rpc exception");
			e.printStackTrace();
		}
	}

	private void loop() throws RPCException {
		while (running) {
			update();
		}
	}

	protected abstract void setup() throws RPCException;

	protected abstract void update() throws RPCException;

}
