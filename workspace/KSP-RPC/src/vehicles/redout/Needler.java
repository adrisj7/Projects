package vehicles.redout;

import java.util.List;

import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Engine;
import vehicles.redout.RedoutBaby2.EngineController;

public class Needler extends RedoutBaby2 {

    public static final String VESSEL_NAME = "The Needlerino";

    public Needler(String serverName, String serverIPAddr, int serverPortRPC, int serverPortStream) {
        super(VESSEL_NAME, serverName, serverIPAddr, serverPortRPC, serverPortStream);
    }

    @Override
    public void setup() throws RPCException {
        super.setup();
        super.setTargetAltitude(15);
        super.setAltitudePID(-0.016 * 10 * 0.3, 0.0, -0.045 * 5 * 0.5);
        super.setRotatePID(0.02 * 1.25 * 5, 0.0, 0.005 * 0.55 * 5);
        super.setTurnPID(-0.1 * 0.75, 0, 0);
        super.setSidePID(-0.1 * 0.55, 0, 0);
        super.setTurnSpeed(1.6 * 1.5);
        super.disallowFlying();

        vessel.getControl().setGear(false);
    }

    @Override
    public void assignEngine(int id, Engine engine, List<Engine> thrustEngines, List<Engine> topEngines,
            List<Engine> rightSideEngines, List<Engine> leftSideEngines, List<Engine> rightRotationEngines,
            List<Engine> leftRotationEngines, List<EngineController> bottomEngines, List<Engine> airbrakeLeftEngines,List<Engine> airbrakeRightEngines) throws RPCException {

        switch (id) {
            case 0:
                thrustEngines.add(engine);
                break;
            case 13:
                topEngines.add(engine);
                break;
            case 1:
                rightSideEngines.add(engine);
                break;
            case 9:
                leftSideEngines.add(engine);
                break;
            case 11:
            case 8:
                rightRotationEngines.add(engine);
                break;
            case 3:
            case 7:
                leftRotationEngines.add(engine);
                break;
            case 4:
            case 12:
            case 5:
            case 10:
            case 2:
            case 6:
                bottomEngines.add(new EngineController(engine, relativeRef, flight));
                break;
            default:
                System.err.println("Unaccounted hover engine of ID " + id + ".");
                break;
        }
    }
    
    @Override
    protected void update() throws RPCException {
        super.update();
        // Braking
        float ltrigger =  gamepad.getComponents().getAxes().lt;
        if (ltrigger > 0.1) {
            vessel.getControl().setBrakes(true);
        } else {
            vessel.getControl().setBrakes(false);
        }
    }

    public static void main(String[] args) {
        new Needler("RPC Server", "127.0.0.1", 50000, 50001).start();
    }
}
