package vehicles.redout;

import java.util.List;

import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Engine;
import vehicles.redout.RedoutBaby2.EngineController;

public class Sulha1 extends RedoutBaby2 {

    public static final String VESSEL_NAME = "Sulha AG Qareen 1";

    public Sulha1(String serverName, String serverIPAddr, int serverPortRPC,
            int serverPortStream) {
        super(VESSEL_NAME, serverName, serverIPAddr, serverPortRPC, serverPortStream);
    }

    @Override
    public void setup() throws RPCException {
        super.setup();
        super.setTargetAltitude(8);
        super.setAltitudePID(
                -0.016 * 10 * 0.1, 
                 0.0, 
                -0.045 * 200 * 2.5);
        super.setRotatePID(
                0.02 * 0.45, 
                0.0, 
                0.005 * 0.25);
        super.setTurnPID(
                -0.1 * 0.55, 
                0, 
                0);
        super.setSidePID(
                -0.1 * 0.55, 
                0, 
                0);
        disallowFlying();
    }

    @Override
    public void assignEngine(int id, Engine engine, List<Engine> thrustEngines, List<Engine> topEngines,
            List<Engine> rightSideEngines, List<Engine> leftSideEngines, List<Engine> rightRotationEngines,
            List<Engine> leftRotationEngines, List<EngineController> bottomEngines, List<Engine> airbrakeLeftEngines,List<Engine> airbrakeRightEngines) throws RPCException {

        switch (id) {
            case 0:
            case 1:
            case 10:
                thrustEngines.add(engine);
                break;
            case 17:
            case 8:
                topEngines.add(engine);
                break;
          case 14:
          case 18:
              rightSideEngines.add(engine);
              break;
          case 5:
          case 9:          
              leftSideEngines.add(engine);
              break;
          case 6:
          case 16:
              rightRotationEngines.add(engine);
              break;
          case 7:
          case 15:
              leftRotationEngines.add(engine);
              break;
          case 2:
          case 3:
          case 4:
          case 11:
          case 12:
          case 13:
          case 19:
              bottomEngines.add(new EngineController(engine, relativeRef, flight));
              break;
          default:
              System.err.println("Unaccounted hover engine of ID " + id + "." );
              break;
      }
    }

    public static void main(String[] args) {
        new Sulha1("RPC Server", "127.0.0.1", 50000, 50001).start();
    }
}
