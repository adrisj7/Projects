package vehicles.redout;

import java.util.List;

import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Engine;
import vehicles.redout.RedoutBaby2.EngineController;

public class ESAAGR1 extends RedoutBaby2 {

    public static final String VESSEL_NAME = "ESA-AGR Lancer 1";

    public ESAAGR1(String serverName, String serverIPAddr, int serverPortRPC, int serverPortStream) {
        super(VESSEL_NAME, serverName, serverIPAddr, serverPortRPC, serverPortStream);
    }

    @Override
    public void setup() throws RPCException {
        super.setup();
        super.setTargetAltitude(8);
        super.setAltitudePID(-0.016 * 10 * 0.3, 0.0, -0.045 * 5 * 0.5);
        super.setRotatePID(0.02 * 1.25, 0.0, 0.005 * 0.55);
        super.setTurnPID(-0.1 * 0.75, 0, 0);
        super.setSidePID(-0.1 * 0.55, 0, 0);
        super.setTurnSpeed(1.6 * 1.5);
        super.disallowFlying();
    }

    @Override
    public void assignEngine(int id, Engine engine, List<Engine> thrustEngines, List<Engine> topEngines,
            List<Engine> rightSideEngines, List<Engine> leftSideEngines, List<Engine> rightRotationEngines,
            List<Engine> leftRotationEngines, List<EngineController> bottomEngines, List<Engine> airbrakeLeftEngines,List<Engine> airbrakeRightEngines) throws RPCException {

        switch (id) {
            case 0:
            case 1:
            case 8:
                thrustEngines.add(engine);
                break;
            case 7:
            case 14:
                topEngines.add(engine);
                break;
            case 3:
            case 4:
            case 5:
                rightSideEngines.add(engine);
                break;
            case 10:
            case 11:
            case 12:
                leftSideEngines.add(engine);
                break;
            case 13:
            case 17:
            case 19:
                rightRotationEngines.add(engine);
                break;
            case 6:
            case 18:
            case 20:
                leftRotationEngines.add(engine);
                break;
            case 2:
            case 9:
            case 15:
            case 16:
            case 21:
            case 22:
            case 23:
                bottomEngines.add(new EngineController(engine, relativeRef, flight));
                break;
            default:
                System.err.println("Unaccounted hover engine of ID " + id + ".");
                break;
        }
    }

    public static void main(String[] args) {
        new ESAAGR1("RPC Server", "127.0.0.1", 50000, 50001).start();
    }
}
