package control;

import core.RPCClient;
import krpc.client.RPCException;
import krpc.client.services.SpaceCenter.Camera;
import krpc.client.services.SpaceCenter.CameraMode;

public class CameraControl {

    public static Camera getCamera() throws RPCException {
        return RPCClient.spaceCenter().getCamera();
    }

    public static CameraMode getCameraMode() throws RPCException {
        return getCamera().getMode();
    }

    public static void setDistance(float distance) throws RPCException {
        getCamera().setDistance(distance);
    }

    public static void setHeading(float heading) throws RPCException {
        getCamera().setHeading(heading);
    }

    public static void smoothSetHeading(float heading, float interpFactor) throws RPCException {

        if (getCameraMode() == CameraMode.AUTOMATIC) {
            float cameraHeading = getCamera().getHeading();
            float deltaAngle = heading - cameraHeading;
            deltaAngle = (float) (deltaAngle - Math.floor(deltaAngle / 360.0) * 360.0);
            if (deltaAngle > 180) {
                deltaAngle = -1 * (360 - deltaAngle);
            }
            cameraHeading += interpFactor * deltaAngle;
            setHeading(cameraHeading);
        }
    }

    public static void smoothSetDistance(float distance, float interpFactor) throws RPCException {
        if (getCameraMode() == CameraMode.AUTOMATIC) {
            float cameraDistance = getCamera().getDistance();
            cameraDistance += (distance - cameraDistance) * interpFactor;
            setDistance(cameraDistance);
        }
    }
}
