package vehicles.redout;

import java.util.List;

import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Camera;
import krpc.client.services.SpaceCenter.ControlSurface;
import krpc.client.services.SpaceCenter.Engine;

public class Needler2 extends RedoutBaby2 {

    public static final String VESSEL_NAME = "The Needlerino 2";

    private ControlSurface leftAirbrake = null, rightAirbrake = null;

    public Needler2(String serverName, String serverIPAddr, int serverPortRPC, int serverPortStream) {
        super(VESSEL_NAME, serverName, serverIPAddr, serverPortRPC, serverPortStream);
    }

    @Override
    public void setup() throws RPCException {

        super.setup();
        super.setTargetAltitude(4);
        super.setAltitudePID(
                -0.016 * 10 * 0.3,
                0.0, 
                -0.045 * 5 * 0.5);
//        super.setAltitudePID(
//                -0.016 * 10 * 0.1, 
//                0.0, 
//               -0.045 * 200 * 2.5 * 2 * 10);
//        super.setAltitudePID(-0.016 * 2 * 0.1, 0.0, -0.0045 * 5 * 4.5);
        super.setRotatePID(0.02 * 1.25 * 5, 0.0, 0.005 * 0.55 * 5);
        super.setTurnPID(-0.1 * 0.75, 0, 0);
        super.setSidePID(-0.1 * 0.55 * 0.5, 0, 0);
        super.setTurnSpeed(1.6 * 0.8);
        super.setInputMode(ControlMode.WIPEOUT);
        super.setWipeoutRegularSideForce(0.2f);
        super.setWipeoutAirbrakeEngineForce(0.3f);
        //super.setWipeoutSpeedClamp(100, 0.1f);
        super.disallowFlying();
        super.setSideAestheticRotation(false);

        vessel.getControl().setGear(false);

        rightAirbrake = vessel.getParts().getControlSurfaces().get(0);
        leftAirbrake = vessel.getParts().getControlSurfaces().get(1);
//        int c = 0;
//        for(ControlSurface surface: vessel.getParts().getControlSurfaces()) {
//            c++;
//            if (c == 2) {
//                leftAirbrake = surface;
//            } else if (c == 1) {
//                rightAirbrake = surface;
//            }
//        }

    }

    @Override
    public void assignEngine(int id, Engine engine, List<Engine> thrustEngines, List<Engine> topEngines,
            List<Engine> rightSideEngines, List<Engine> leftSideEngines, List<Engine> rightRotationEngines,
            List<Engine> leftRotationEngines, List<EngineController> bottomEngines, List<Engine> airbrakeLeftEngines,List<Engine> airbrakeRightEngines) throws RPCException {

        switch (id) {
            case 4:
                thrustEngines.add(engine);
                break;
            case 15:
            case 16:
                topEngines.add(engine);
                break;
            case 13:
                rightSideEngines.add(engine);
                break;
            case 14:
                leftSideEngines.add(engine);
                break;
            case 1:
            case 11:
                leftRotationEngines.add(engine);
                break;
            case 0:
            case 12:
                rightRotationEngines.add(engine);
                break;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                bottomEngines.add(new EngineController(engine, relativeRef, flight));
                break;
            case 3:
                airbrakeLeftEngines.add(engine);
                break;
            case 2:
                airbrakeRightEngines.add(engine);
                break;
            default:
                System.err.println("Unaccounted hover engine of ID " + id + ".");
                break;
        }
    }

    @Override
    protected void update() throws RPCException {
        super.update();

        // Air Braking
        float ltrigger =  gamepad.getComponents().getAxes().lt;
        float rtrigger =  gamepad.getComponents().getAxes().rt;
        leftAirbrake.setDeployed(ltrigger > 0.1);
        rightAirbrake.setDeployed(rtrigger > 0.1);
    }

    public static void main(String[] args) {
        new Needler2("RPC Server", "127.0.0.1", 50000, 50001).start();
    }
}
