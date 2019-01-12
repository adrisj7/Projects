package control.ship;

import java.util.LinkedList;

import control.PIDController;
import control.Ship;
import control.ShipController;
import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Engine;
import krpc.client.services.SpaceCenter.Flight;
import krpc.client.services.SpaceCenter.ReferenceFrame;

public class HoverController extends ShipController {

    private static final double MAX_POS_FACTOR = 20;
    private static final double ENGINE_RAMP_LIMIT = 0.4;
    
    private static final double HEIGHT_ALTPID_CUTOFF = 5;
    private static final double VEL_ALTPID_DOWN_FORCE = 30;

    private LinkedList<EngineController> bottomEngines;
    private int bottomEngineIDs[] = null;
    private LinkedList<EngineController> topEngines;
    private int topEngineIDs[] = null;

    private PIDController heightController;
    private PIDController rollController, pitchController;

    public HoverController(Ship ship, double kp_alt, double ki_alt, double kd_alt, double kp_rot, double ki_rot,
            double kd_rot, int bottomEngineIDs[], int topEngineIDs[]) throws RPCException {
        super(ship);
        this.bottomEngineIDs = bottomEngineIDs;
        this.topEngineIDs = topEngineIDs;

        bottomEngines = new LinkedList<>();
        topEngines = new LinkedList<>();

        heightController = new PIDController(kp_alt, ki_alt, kd_alt, 0);
        rollController = new PIDController(kp_rot, ki_rot, kd_rot, 0);
        pitchController = new PIDController(kp_rot, ki_rot, kd_rot, 0);
    }

    /*
     * * * * * * CONTROL * * * *
     */

    public void start() throws RPCException {
        assert bottomEngines.size() != 0 : "No engines! Did you remember to call setup()?";
        for (EngineController controller : bottomEngines) {
            controller.start();
        }
    }

    public void setTargetHeight(float height) {
        heightController.setTarget(height);
    }

    public void setRollTarget(float roll) {
        rollController.setTarget(roll);
    }

    public void setPitchTarget(float pitch) {
        pitchController.setTarget(pitch);
    }

    @Override
    public void setup() throws RPCException {
        // Initialize our controllers
        for (int id : bottomEngineIDs) {
            Engine newEngine = ship.getVessel().getParts().getEngines().get(id);
            EngineController newController = new EngineController(newEngine, 
                    ship.getRelativeReferenceFrame(),
                    ship.getFlight());
            bottomEngines.add(newController);
        }
        for (int id : topEngineIDs) {
            Engine newEngine = ship.getVessel().getParts().getEngines().get(id);
            EngineController newController = new EngineController(newEngine, 
                    ship.getRelativeReferenceFrame(),
                    ship.getFlight());
            topEngines.add(newController);
        }
    }

    @Override
    public void update() throws RPCException {
        // Inputs
        double height = ship.getFlight().getSurfaceAltitude();
        double roll = ship.getFlight().getRoll();
        double pitch = ship.getFlight().getPitch();

        // PID Update
        heightController.update(height);
        rollController.update(roll);
        pitchController.update(pitch);

        // Outputs
        double altitudeOutput = heightController.getValue();
        double rollOutput = rollController.getValue();
        double pitchOutput = pitchController.getValue();

        // Tweak outputs
        // Allow flying/falling
        if (height - heightController.getTarget() > HEIGHT_ALTPID_CUTOFF) {
            if (heightController.getDerivative() > -VEL_ALTPID_DOWN_FORCE) {
                altitudeOutput = Math.min(altitudeOutput, 0.02);
                rollOutput  *= 0.1;
                pitchOutput *= 0.1;

            }
        }

        // Response
        for (EngineController controller : bottomEngines) {
            controller.update(altitudeOutput, rollOutput, pitchOutput);
        }
        for (EngineController controller : topEngines) {
            controller.update(-altitudeOutput, -rollOutput, -pitchOutput);
        }
    }

    // Controls engines based on their distance from the center and PID Outputs
    private static class EngineController {
        // The engine we control
        private Engine engine;

        // How much roll and pitch affect this thruster by
        private double rollFactor;
        private double pitchFactor;

        private double lastThrustLimit;

        public EngineController(Engine engine, ReferenceFrame ref, Flight flight) throws RPCException {
            this.engine = engine;
            double xpos = engine.getPart().position(ref).getValue0();
            double ypos = engine.getPart().position(ref).getValue1();
            // Roll: X, Pitch: Y
            // The closer to the center, the stronger the force
            rollFactor = xpos;
            pitchFactor = -ypos;

            // Our distance strength stops at some point
            if (Math.abs(rollFactor) > MAX_POS_FACTOR) {
                rollFactor = MAX_POS_FACTOR * Math.signum(rollFactor);
            }
            if (Math.abs(pitchFactor) > MAX_POS_FACTOR) {
                pitchFactor = MAX_POS_FACTOR * Math.signum(pitchFactor);
            }
        }

        public void start() throws RPCException {
            engine.setActive(true);
            engine.setThrustLimit(0);
        }

        public void update(double altitudeOutput, double rollOutput, double pitchOutput) throws RPCException {
            rollOutput *= rollFactor;
            pitchOutput *= pitchFactor;

            // Maybe square them / find their magnitude?
            //double output = rollOutput + pitchOutput;
            double output = altitudeOutput*0.8 + (Math.max(altitudeOutput, 0) + 0.3) * (rollOutput + pitchOutput);
            output = Math.max(output, 0);

            // Ramp
            if (Math.abs(output - lastThrustLimit) > ENGINE_RAMP_LIMIT) {
                double delta = output - lastThrustLimit;
                output = lastThrustLimit + ENGINE_RAMP_LIMIT * Math.signum(delta);
            }

            engine.setThrustLimit((float) (output));

            lastThrustLimit = output;
        }

        public Engine getEngine() {
            return engine;
        }
    }

}
