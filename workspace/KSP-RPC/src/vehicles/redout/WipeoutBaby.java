package vehicles.redout;

import java.awt.AWTException;
import java.awt.RenderingHints.Key;
import java.awt.event.KeyEvent;
import java.awt.Robot;
import java.util.List;

import com.ivan.xinput.XInputAxes;

import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.ControlSurface;
import krpc.client.services.SpaceCenter.Engine;

public class WipeoutBaby extends RedoutBaby2 {

    public static final String VESSEL_NAME = "Wipeout Baby" + " Weaponized";

    private ControlSurface leftAirbrake = null, rightAirbrake = null;

    private Robot keyPresser;

    public WipeoutBaby(String serverName, String serverIPAddr, int serverPortRPC, int serverPortStream) {
        super(VESSEL_NAME, serverName, serverIPAddr, serverPortRPC, serverPortStream);
    }

    @Override
    public void setup() throws RPCException {
        super.setup();
        super.setTargetAltitude(3);
        super.setAltitudePID(
                -0.016 * 10 * 0.3,
                0.0, 
                -0.045 * 5 * 0.5);
//        super.setAltitudePID(
//                -0.016 * 10 * 0.1, 
//                0.0, 
//               -0.045 * 200 * 2.5 * 2 * 10);
//        super.setAltitudePID(-0.016 * 2 * 0.1, 0.0, -0.0045 * 5 * 4.5);
        super.setRotatePID(0.02 * 1.25 * 5 * 0.1, 0.0, 0.005 * 0.55 * 5 * 0.1);
        super.setTurnPID(-0.1 * 0.75, 0, 0);
        super.setSidePID(-0.1 * 0.55 * 0.5, 0, 0);
        super.setTurnSpeed(1.6 * 0.8);
        super.setInputMode(ControlMode.WIPEOUT);
        super.setWipeoutRegularSideForce(0.2f);
        super.setWipeoutAirbrakeEngineForce(0.3f);
        super.setWipeoutSpeedClamp(100, 0.1f);
        super.disallowFlying();
        super.setSideAestheticRotation(false);

        vessel.getControl().setGear(false);

        rightAirbrake = vessel.getParts().getControlSurfaces().get(1);
        leftAirbrake = vessel.getParts().getControlSurfaces().get(0);
        
        try {
            keyPresser = new Robot();
        } catch (AWTException e) {
            // oof nice programming here buddy
            e.printStackTrace();
        }

    }

    @Override
    public void assignEngine(int id, Engine engine, List<Engine> thrustEngines, List<Engine> topEngines,
            List<Engine> rightSideEngines, List<Engine> leftSideEngines, List<Engine> rightRotationEngines,
            List<Engine> leftRotationEngines, List<EngineController> bottomEngines, List<Engine> airbrakeLeftEngines,List<Engine> airbrakeRightEngines) throws RPCException {

        switch (id) {
            case 0:
                thrustEngines.add(engine);
                break;
            case 11:
            case 12:
                topEngines.add(engine);
                break;
            case 16:
                rightSideEngines.add(engine);
                break;
            case 15:
                leftSideEngines.add(engine);
                break;
            case 1:
            case 3:
                leftRotationEngines.add(engine);
                break;
            case 2:
            case 4:
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
            case 13:
                airbrakeLeftEngines.add(engine);
                break;
            case 14:
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

        // Weapon system, if we have one.
        boolean fire = gamepad.getComponents().getButtons().x;
        boolean switchNext = (gamepad.getComponents().getAxes().dpad == XInputAxes.DPAD_DOWN);
        boolean switchPrev = (gamepad.getComponents().getAxes().dpad == XInputAxes.DPAD_UP);
        if (fire) {
            keyPresser.keyPress(KeyEvent.VK_1);
            keyPresser.keyRelease(KeyEvent.VK_1);
        }
        if (switchNext) {
            keyPresser.keyPress(KeyEvent.VK_3);
        } else {
            keyPresser.keyRelease(KeyEvent.VK_3);            
        }
        if (switchPrev) {
            keyPresser.keyPress(KeyEvent.VK_2);
        } else {
            keyPresser.keyRelease(KeyEvent.VK_2);
        }
    }

    public static void main(String[] args) {
        new WipeoutBaby("RPC Server", "127.0.0.1", 50000, 50001).start();
    }
}
