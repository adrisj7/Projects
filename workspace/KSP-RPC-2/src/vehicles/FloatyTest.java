package vehicles;

import control.PIDController;
import control.Ship;
import control.ShipController;
import core.RPCClient;
import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Engine;

public class FloatyTest extends RPCClient {

    // Dividing systems
    private Hoverer hoverer;

    public FloatyTest(String serverName, String serverIPAddr, int serverPortRPC, int serverPortStream) {
        super(serverName, serverIPAddr, serverPortRPC, serverPortStream);
    }

    @Override
    protected void setup() throws RPCException {
        hoverer = new Hoverer(Ship.getShipByName("Hover Test 1"));
        hoverer.setTargetHeight(10);
    }

    @Override
    protected void update() throws RPCException {
        hoverer.update();
    }

    private static class Hoverer extends ShipController { // Extends System {

        private static final double KP_ALT = 1, KI_ALT = 0, KD_ALT = 1;

        private Engine engine;

        private PIDController heightController;

        public Hoverer(Ship ship) throws RPCException {
            super(ship);
        }

        @Override
        public void setup() throws RPCException {
            // Assuming there's only 1 engine
            engine = ship.getVessel().getParts().getEngines().get(0);

            heightController = new PIDController(KP_ALT, KI_ALT, KD_ALT, 0);
            
        }
        @Override
        public void update() throws RPCException {
            heightController.update((float)getHeight(), spaceCenter());

            engine.setThrustLimit((float)heightController.getValue());
        }

        public void setTargetHeight(double height) {
            heightController.setTarget(height);
        }

        public double getTargetHeight() {
            return heightController.getTarget();
        }

        public double getHeight() throws RPCException {
            return ship.getFlight().getSurfaceAltitude();
        }
    }

    public static void main(String[] args) {
        (new FloatyTest("Default Server", "127.0.0.1", 50000, 50001)).start();
    }

}
