package control.ship;

import java.util.LinkedList;

import control.PIDController;
import control.Ship;
import control.ShipController;
import core.RPCClient;
import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Engine;
import util.MathUtil;

public class SideController extends ShipController {

    private static final double SIDE_SHIFT_STRENGTH = 900;

    private Engine leftSideEngine, rightSideEngine;
    private LinkedList<Engine> rotateRightEngines, rotateLeftEngines;

    private int leftSideEngineID, rightSideEngineID;
    private int rotateRightEngineIDs[];
    private int rotateLeftEngineIDs[];

    private PIDController sideSpeedPID;
    private PIDController turnSpeedPID;

    private double sideShift;

    public SideController(Ship ship, double kp_side, double ki_side, double kd_side, double kp_turn, double ki_turn,
            double kd_turn, int leftSideEngineID, int rightSideEngineID, int rotateRightEngineIDs[],
            int rotateLeftEngineIDs[]) throws RPCException {
        super(ship);
        this.leftSideEngineID = leftSideEngineID;
        this.rightSideEngineID = rightSideEngineID;
        this.rotateRightEngineIDs = rotateRightEngineIDs;
        this.rotateLeftEngineIDs = rotateLeftEngineIDs;

        rotateRightEngines = new LinkedList<>();
        rotateLeftEngines = new LinkedList<>();
        
        sideSpeedPID = new PIDController(kp_side, ki_side, kd_side, 0);
        turnSpeedPID = new PIDController(kp_turn, ki_turn, kd_turn, 0);
    }

    public void start() throws RPCException {
        leftSideEngine.setActive(true);
        leftSideEngine.setThrustLimit(0);
        rightSideEngine.setActive(true);
        rightSideEngine.setThrustLimit(0);
        for (Engine engine : rotateRightEngines) {
            engine.setActive(true);
            engine.setThrustLimit(0);
        }
        for (Engine engine : rotateLeftEngines) {
            engine.setActive(true);
            engine.setThrustLimit(0);
        }
    }

    public void setTargetRotationSpeed(double speed) {
        turnSpeedPID.setTarget(speed);
    }
    public void setTargetSideSpeed(double speed) {
        sideSpeedPID.setTarget(speed);
    }

    public void sideShift(double direction) {
        sideShift = direction * SIDE_SHIFT_STRENGTH;
    }

    @Override
    public void setup() throws RPCException {
        leftSideEngine = ship.getVessel().getParts().getEngines().get(leftSideEngineID);
        rightSideEngine = ship.getVessel().getParts().getEngines().get(rightSideEngineID);
        for (int id : rotateRightEngineIDs) {
            Engine newEngine = ship.getVessel().getParts().getEngines().get(id);
            rotateRightEngines.add(newEngine);
        }
        for (int id : rotateLeftEngineIDs) {
            Engine newEngine = ship.getVessel().getParts().getEngines().get(id);
            rotateLeftEngines.add(newEngine);
        }

    }

    @Override
    public void update() throws RPCException {
        // Inputs
        double sideSpeed = MathUtil.getSideVel(ship);
        double yawVel = ship.getVessel().angularVelocity(ship.getReferenceFrame()).getValue0();

        // PID Update
        sideSpeedPID.update(sideSpeed - sideShift); // Subtract by sideShift to offset the target
        turnSpeedPID.update(yawVel);

        // Outputs
        double sideOutput = sideSpeedPID.getValue();
        double turnOutput = turnSpeedPID.getValue();

        // AGHHHHHHHHH this brakes way to easily
        // If we're upside down, don't try this
        if (Math.abs(ship.getFlight().getRoll()) > 150 || Math.abs(ship.getFlight().getPitch()) > 150 || ship.getFlight().getSurfaceAltitude() > 10) {
            sideOutput = 0;
        }
        
        // Response
        leftSideEngine.setThrustLimit( (float)Math.max( 0,     sideOutput)); // Left Side
        rightSideEngine.setThrustLimit((float)Math.max(0, -1 * sideOutput)); // Right Side
        for(Engine engine : rotateRightEngines) {
            engine.setThrustLimit((float)Math.max(0,      turnOutput));      // Right Rotate
        }
        for(Engine engine : rotateLeftEngines) {
            engine.setThrustLimit((float)Math.max(0, -1 * turnOutput));      // Left Rotate
        }

        sideShift *= 0.9;
    }

}
