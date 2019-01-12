package control;

import krpc.client.RPCException;

public abstract class ShipController {

    protected Ship ship;

    public ShipController(Ship ship) throws RPCException {
        this.ship = ship;
    }

    public abstract void setup() throws RPCException;
    public abstract void update() throws RPCException;
}
