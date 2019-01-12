package control.ship;

import java.util.LinkedList;

import control.Ship;
import control.ShipController;
import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Engine;

public class ThrustController extends ShipController {

    private int engineIDs[];
    private LinkedList<Engine> engines;

    private double throttle;

    public ThrustController(Ship ship, int engineIDs[]) throws RPCException {
        super(ship);
        this.engineIDs = engineIDs;
        
        engines = new LinkedList<>();
    }

    public void start() throws RPCException {
        for (Engine engine : engines) {
            engine.setActive(true);
            engine.setThrustLimit(0);
        }
    }

    public void setThrottle(double throttle) {
        this.throttle = throttle;
    }

    @Override
    public void setup() throws RPCException {
        for (int id : engineIDs) {
            Engine newEngine = ship.getVessel().getParts().getEngines().get(id);
            engines.add(newEngine);
        }
    }

    @Override
    public void update() throws RPCException {
        for (Engine engine : engines) {
            engine.setThrustLimit((float) throttle);
        }
    }

}
