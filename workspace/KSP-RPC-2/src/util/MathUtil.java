package util;

import control.Ship;
import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Flight;
import krpc.client.services.SpaceCenter.ReferenceFrame;
import krpc.client.services.SpaceCenter.Vessel;

public class MathUtil {
    private static final double CLOSE_ENOUGH_REGULAR = 0.001;

    public static boolean closeEnough(double a, double b, double closeEnough) {
        return Math.abs(a - b) < closeEnough;
    }

    public static boolean closeEnough(double a, double b) {
        return closeEnough(a, b, CLOSE_ENOUGH_REGULAR);
    }
    
    /**
     * Calculates and spits out our ship's side velocity
     * @return our ship's side velocity (positive is in the RIGHT direction)
     * @throws RPCException
     */
    public static double getSideVel(Ship ship) throws RPCException {
           // RELATIVE TO AIR STRIP: 
        // vx points to the right, vy points forward
        // theta is clockwise, airstrip forward is 0 degrees
        
        Vessel vessel = ship.getVessel();
        ReferenceFrame ref = ship.getReferenceFrame();
        Flight flight = ship.getFlight();
        
        double vx = -1 * vessel.velocity(ref).getValue1();
        double vy = vessel.velocity(ref).getValue2();
        double theta = flight.getHeading() - 90;

        double velMagnitude = Math.sqrt(vx * vx + vy * vy);
        double velAngle = Math.toDegrees(Math.atan2(vy, vx));

        double angleRelative = velAngle - (-1 * theta) - 90;

        return -1 * velMagnitude * Math.sin(Math.toRadians(angleRelative));
    }

    /**
     * Calculates and spits out ships forward velocity
     * @return
     * @throws RPCException
     */
    public static double getFrontVel(Ship ship) throws RPCException {
        Vessel vessel = ship.getVessel();
        ReferenceFrame ref = ship.getReferenceFrame();
        Flight flight = ship.getFlight();

        double vx = -1 * vessel.velocity(ref).getValue1();
        double vy = vessel.velocity(ref).getValue2();
        double theta = flight.getHeading() - 90;

        double velMagnitude = Math.sqrt(vx * vx + vy * vy);
        double velAngle = Math.toDegrees(Math.atan2(vy, vx));

        double angleRelative = velAngle - (-1 * theta);

        return velMagnitude * Math.sin(Math.toRadians(angleRelative));
    }

    public static double clamp(double val, double min, double max) {
        return Math.max(Math.min(val, max), min);
    }
}
