package vehicles;

import control.CameraControl;
import control.Ship;
import control.ship.AirbrakeController;
import control.ship.HoverController;
import control.ship.SideController;
import control.ship.ThrustController;
import core.RPCClient;
import krpc.client.RPCException;
import util.Gamepad;
import util.MathUtil;

public class FloatyTest2 extends RPCClient {

    /*
     * * PID VALUES
     */
    // Rotation
    private static final double K_ROT_P = -0.1 * 1, // 1
            K_ROT_I = -0.00001 * 0, K_ROT_D = -0.08 * 0.3; // 0.08
    // Altitude
    private static final double K_ALT_P = 0.020, K_ALT_I = 0.000001 * 0, K_ALT_D = 0.045;
    // Side
    private static final double K_SIDE_P = 0.1 * 0.2, K_SIDE_I = 0, K_SIDE_D = 0.01 * 0;
    // Turning velocity
    private static final double K_TURN_P = 0.1 * 0.5, K_TURN_I = 0, K_TURN_D = 0;

    // Our ship
    private Ship ship;

    // Controllers
    private HoverController hoverer;
    private SideController sider;
    private ThrustController thruster;
    private AirbrakeController airbrakes;

    // Gamepad
    private Gamepad pad;

    @Override
    protected void setup() throws RPCException {
        ship = Ship.getShipByName("Feisar Speed");

        int bottomEngineIDs[] = { 0, 3, 6, 7 };
        int topEngineIDs[] = {};
        int rightSideEngineID = 9, leftSideEngineID = 8;
        int rotateRightEngineIDs[] = { 4 };
        int rotateLeftEngineIDs[] = { 5 };
        int thrustEngineIDs[] = { 1, 2 };


        hoverer = new HoverController(ship, K_ALT_P, K_ALT_I, K_ALT_D, K_ROT_P, K_ROT_I, K_ROT_D, bottomEngineIDs,
                topEngineIDs);
        hoverer.setup();
        hoverer.start();
        hoverer.setTargetHeight(2.5f);

        sider = new SideController(ship, K_SIDE_P, K_SIDE_I, K_SIDE_D, K_TURN_P, K_TURN_I, K_TURN_D, leftSideEngineID,
                rightSideEngineID, rotateRightEngineIDs, rotateLeftEngineIDs);
        sider.setup();
        sider.start();

        thruster = new ThrustController(ship, thrustEngineIDs);
        thruster.setup();
        thruster.start();

        airbrakes = new AirbrakeController(ship, 0, 1);
        airbrakes.setup();

        pad = new Gamepad(0);
    }

    @Override
    protected void update() throws RPCException {
        // Camera
        CameraControl.smoothSetHeading(ship.getFlight().getHeading(), 0.05f);
        CameraControl.smoothSetDistance((float)MathUtil.clamp(0.1*ship.getFlight().getSpeed(), 10, 15), 0.1f);

        // Input
        float thrust   = pad.getButtons().a ? 1 : 0;
        float steering = pad.getAxes().lx;
        float airbleft = pad.getButtons().lShoulder ? 1 : 0;
        float airbright = pad.getButtons().rShoulder ? 1 : 0;

        // Special stuff
        float deltaTurn = calculateAirbrakeTurning(airbleft, airbright);
        if (airbrakes.checkDoubleTapLeft()) {
            sider.sideShift(-1);
        }
        if (airbrakes.checkDoubleTapRight()) {
            sider.sideShift(1);
        }

        thruster.setThrottle(thrust);
        sider.setTargetRotationSpeed(steering * 2 + deltaTurn);
        hoverer.setRollTarget(steering * 5 + Math.min(10, deltaTurn));
        airbrakes.setAirbrakes(airbleft, airbright);

        // System controllers
        thruster.update();
        hoverer.update();
        sider.update();
        airbrakes.update();
    }

    // We're pressing the airbrakes, how much quicker do we turn?
    private float calculateAirbrakeTurning(float airbleft, float airbright) throws RPCException {
        float vel = (float) ship.getFlight().getSpeed();
        float delta = (float) (vel/ 40) * (airbright - airbleft);
        return delta;
    }
    
    public static void main(String[] args) {
        (new FloatyTest2()).start();
    }
}
