package control;

import core.RPCClient;
import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Flight;
import krpc.client.services.SpaceCenter.ReferenceFrame;
import krpc.client.services.SpaceCenter.Vessel;

/** 
 * Replacement for Vessel that has extra functionality that we want
 *
 */
public class Ship {

    private Vessel vessel;
    private ReferenceFrame ref;
    private Flight flight;

    public Ship(Vessel vessel) {
        this.vessel = vessel;

        try {
            ref = ReferenceFrame.createHybrid(RPCClient.connection(), vessel.getOrbit().getBody().getReferenceFrame(),
                    vessel.getSurfaceReferenceFrame(), vessel.getOrbit().getBody().getReferenceFrame(),
                    vessel.getOrbit().getBody().getReferenceFrame());
            flight = vessel.flight(ref);
        } catch (RPCException e) {
            // It probably didn't connect at this point
            e.printStackTrace();
        }

    }

    public Vessel getVessel() {
        return vessel;
    }
    public ReferenceFrame getReferenceFrame() {
        return ref;
    }
    public ReferenceFrame getRelativeReferenceFrame() {
        try {
            return vessel.getReferenceFrame();
        } catch (RPCException e) {
            e.printStackTrace();
            return null;
        }
    }
    public Flight getFlight() {
        return flight;
    }

    /** getShipByName(name);
     * 
     * @param name
     * @return The ship who's name matches "name".
     */
    public static Ship getShipByName(String name) {
        Vessel ourVessel = null;
        try {
            for (Vessel vessel : RPCClient.spaceCenter().getVessels()) {
                if (vessel.getName().equals(name)) {
                    ourVessel = vessel;
                    break;
                }
            }
        } catch (RPCException e) {
            // Um ok At this point you assume it's not connected I think
            e.printStackTrace();
        }
        if (ourVessel == null) {
            System.err.println("Cannot find vessel of name \"" + name + "\".");
            System.exit(1);
        }

        Ship result = new Ship(ourVessel);
        return result;
    }

    public static Ship getActiveShip() {
        try {
            return new Ship(RPCClient.spaceCenter().getActiveVessel());
        } catch (RPCException e) {
            e.printStackTrace();
        }
        return null;
    }

}
