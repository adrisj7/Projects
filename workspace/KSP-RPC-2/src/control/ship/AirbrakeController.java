package control.ship;

import control.Ship;
import control.ShipController;
import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.ControlSurface;

public class AirbrakeController extends ShipController {

    private static final int DOUBLE_TAP_THRESHOLD_MS = 400;
    private static final int DOUBLE_TAP_COOLDOWN_MS = 1500;

    private int leftAirbrakeID, rightAirbrakeID;
    private ControlSurface leftAirbrake = null, rightAirbrake = null;

    private double authorityLeft, authorityRight;

    // Tapping timing
    private boolean lastPressedLeft,
                    lastPressedRight;
    private double timeLastPressedLeft,
                   timeLastPressedRight;
    private boolean isDoubleTapLeft,
                    isDoubleTapRight;

    private double lastDoubleTapped;

    public AirbrakeController(Ship ship, int leftAirbrakeID, int rightAirbrakeID) throws RPCException {
        super(ship);
        this.leftAirbrakeID = leftAirbrakeID;
        this.rightAirbrakeID = rightAirbrakeID;
    }

    public void setAirbrakes(double authorityLeft, double authorityRight) {
        this.authorityLeft = authorityLeft;
        this.authorityRight = authorityRight;
    }

    /*
     * NOTE: This unsets the double tapping AS SOON as it's read!
     */
    public boolean checkDoubleTapLeft() {
        boolean result = isDoubleTapLeft;
        isDoubleTapLeft = false;
        return result;
    }
    public boolean checkDoubleTapRight() {
        boolean result = isDoubleTapRight;
        isDoubleTapRight = false;
        return result;
    }

    @Override
    public void setup() throws RPCException {
        leftAirbrake = ship.getVessel().getParts().getControlSurfaces().get(leftAirbrakeID);
        rightAirbrake = ship.getVessel().getParts().getControlSurfaces().get(rightAirbrakeID);
        leftAirbrake.setDeployed(false);
        rightAirbrake.setDeployed(false);
    }

    @Override
    public void update() throws RPCException {
        boolean pressedLeft = authorityLeft > 0.5;
        boolean pressedRight = authorityRight > 0.5;

        leftAirbrake.setDeployed(pressedLeft);
        rightAirbrake.setDeployed(pressedRight);
        
        // If just pressed
        double now = System.currentTimeMillis();
        if (now - lastDoubleTapped > DOUBLE_TAP_COOLDOWN_MS) {
            if (pressedLeft && !lastPressedLeft) {
                if (now - timeLastPressedLeft < DOUBLE_TAP_THRESHOLD_MS) {
                    isDoubleTapLeft = true;
                    lastDoubleTapped = now;
                }
                timeLastPressedLeft = now;
            }
            if (pressedRight && !lastPressedRight) {
                if (now - timeLastPressedRight < DOUBLE_TAP_THRESHOLD_MS) {
                    isDoubleTapRight = true;
                    lastDoubleTapped = now;
                }
                timeLastPressedRight = now;
            }
        } else {
            isDoubleTapLeft = false;
            isDoubleTapRight = false; 
        }

        lastPressedLeft = pressedLeft;
        lastPressedRight = pressedRight;
    }

}
