package core;

import org.javatuples.Quartet;
import org.javatuples.Triplet;

public class RPCMath {

    public static Triplet<Double, Double, Double> quaternionToEuler(Quartet<Double, Double, Double, Double> q) {

        double x = q.getValue0(),
               y = q.getValue1(),
               z = q.getValue2(),
               w = q.getValue3();

        double roll, pitch, yaw;

        double sinr = +2.0 * (w * x + y * z);
        double cosr = +1.0 - 2.0 * (x * x + y * y);
        roll = Math.atan2(sinr, cosr);

        // pitch (y-axis rotation)
        double sinp = +2.0 * (w * y - z * x);
        if (Math.abs(sinp) >= 1)
            pitch = Math.PI / 2 * Math.signum(sinp); // use 90 degrees if out of range
        else
            pitch = Math.asin(sinp);

        // yaw (z-axis rotation)
        double siny = +2.0 * (w * z + x * y);
        double cosy = +1.0 - 2.0 * (y * y + z * z);  
        yaw = Math.atan2(siny, cosy);

        return new Triplet<Double, Double, Double>(roll, pitch, yaw);
    }
}
