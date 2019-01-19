package control;

import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Engine;

/**
 * Better control for our thrusters!
 * @author adris
 *
 */
@Deprecated
public class ThrusterSRX {
    private Engine engine;

    public ThrusterSRX(Ship ship, int id) {
        try {
            engine = ship.getVessel().getParts().getEngines().get(id);
        } catch (RPCException e) {
            engine = null;
            e.printStackTrace();
        }
    }

    public Engine getEngine() {
        return engine;
    }
}
