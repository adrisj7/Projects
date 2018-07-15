package vehicleutil;

import java.util.List;

import core.RPCClientShip;
import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Engine;

/**
 * EngineIdentifier.java
 * 
 * Simple utility tool that goes through all engines and tells you its "ID", based on their order in the engine list
 *
 */
public class EngineIdentifier extends RPCClientShip {

	public EngineIdentifier(String serverName, String serverIPAddr, int serverPortRPC, int serverPortStream) {
		super(serverName, serverIPAddr, serverPortRPC, serverPortStream);
	}

	@Override
	protected void setup() throws RPCException {
		super.setup();
		List<Engine> engines = vessel.getParts().getEngines();

		int id = 0;
        for(Engine engine : engines) {
            engine.setThrustLimit((float)id / 100f);
            System.out.printf("Engine # %2d: %s\n", id, engine.getPart().getName());
            id++;
        }
	}

	@Override
	protected void update() throws RPCException {
	}

	public static void main(String[] args) {
		new EngineIdentifier("RPC Server", "127.0.0.1", 50000, 50001).start();
	}
}
