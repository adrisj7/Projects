package world;

import org.joml.Vector2f;

/**
 * Holds physical constants.
 * 
 * TODO: Set it up so that each planet has its own physical constants. For now it's universal
 * 
 * @author adris
 *
 */
public class Physics {

    private static Physics myInstance = new Physics();

    private Vector2f gravity;

    public Physics() {
        gravity = new Vector2f(0, -1f);
    }

    public Vector2f getGravity(float delta) {
        Vector2f realGravity = new Vector2f();
        return gravity.mul(delta, realGravity);
    }

    public static Physics getInstance() {
        return myInstance;
    }
}
