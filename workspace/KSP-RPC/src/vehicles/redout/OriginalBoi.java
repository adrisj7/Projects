package vehicles.redout;

import java.util.List;

import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Engine;
import vehicles.redout.RedoutBaby2.EngineController;

public class OriginalBoi extends RedoutBaby2 {

    public static final String VESSEL_NAME = "Redout Baby 2";
    
    public OriginalBoi(String serverName, String serverIPAddr, int serverPortRPC,
            int serverPortStream) {
        super(VESSEL_NAME, serverName, serverIPAddr, serverPortRPC, serverPortStream);
    }

    @Override
    public void assignEngine(int id, Engine engine, List<Engine> thrustEngines, List<Engine> topEngines,
            List<Engine> rightSideEngines, List<Engine> leftSideEngines, List<Engine> rightRotationEngines,
            List<Engine> leftRotationEngines, List<EngineController> bottomEngines, List<Engine> airbrakeLeftEngines,List<Engine> airbrakeRightEngines) throws RPCException {

        switch (id) {
          case 4:
              topEngines.add(engine);
              break;
          case 12:
              rightSideEngines.add(engine);
              break;
          case 11:
              leftSideEngines.add(engine);
              break;
          case 6:
          case 13:
              rightRotationEngines.add(engine);
              break;
          case 5:
          case 14:
              leftRotationEngines.add(engine);
              break;
          case 2:
          case 9:
          case 7:
          case 3:
          case 10:
          case 8:
              bottomEngines.add(new EngineController(engine, relativeRef, flight));
              break;
          default:
              thrustEngines.add(engine);
              System.err.println("Unaccounted hover engine of ID " + id + "." );
              break;
      }

    }

    public static void main(String[] args) {
        new OriginalBoi("RPC Server", "127.0.0.1", 50000, 50001).start();
    }
}
