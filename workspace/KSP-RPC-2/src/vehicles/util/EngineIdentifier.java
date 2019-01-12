package vehicles.util;

import java.util.List;

import control.Ship;
import core.RPCClient;
import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Engine;

/**
 * EngineIdentifier.java
 * 
 * Simple utility tool that goes through all engines and tells you its "ID", based on their order in the engine list
 *
 */
public class EngineIdentifier extends RPCClient {

	@Override
	protected void setup() throws RPCException {
		List<Engine> engines = Ship.getActiveShip().getVessel().getParts().getEngines();

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
		new EngineIdentifier().start();
	}
}
