package core;

import java.io.IOException;

import krpc.client.Connection;
import krpc.client.RPCException;
import krpc.client.services.KRPC;
import krpc.client.services.SpaceCenter;

public abstract class RPCClient {

	private static Connection connection;
	private static SpaceCenter spaceCenter;

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

	// Constructor for when we're lazy
	public RPCClient() {
	    this("Default Server", "127.0.0.1", 50000, 50001);
	}

	// Static getters of stuff that should be accessed statically
	public static Connection connection() {
	    return connection;
	}
	public static SpaceCenter spaceCenter() {
	    return spaceCenter;
	}

	/**
	 * Run to initialize our rpcclient!
	 */
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

	public void stop() {
	    running = false;
	}

	protected abstract void setup() throws RPCException;

	protected abstract void update() throws RPCException;

}
