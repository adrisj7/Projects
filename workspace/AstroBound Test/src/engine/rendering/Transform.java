package engine.rendering;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Transform {

    /**
     * For rotating along the Z axis
     */
    private static final Vector3f Z_AXIS = new Vector3f(0,0,1);

    public Vector2f position;
    public Vector2f scale;
    public float rotation;

    public Transform() {
        position = new Vector2f(0,0);
        scale = new Vector2f(1,1);
        rotation = 0;
    }

    public Transform translate(float dx, float dy) {
        position.add(dx,dy);
        return this;
    }
    public Transform translate(Vector2f vec) {
        position.add(vec);
        return this;
    }
    public Transform setTranslation(float x, float y) {
        position.x = x;
        position.y = y;
        return this;
    }
    public Transform setTranslation(Vector2f vec) {
        position.x = vec.x;
        position.y = vec.y;
        return this;
    }
    public Transform scale(float sx, float sy) {
        scale.mul(sx, sy);
        return this;
    }
    public Transform scale(Vector2f scale) {
        scale.mul(scale);
        return this;
    }
    public Transform setScale(float sx, float sy) {
        scale.x = sx;
        scale.y = sy;
        return this;
    }
    public Transform setScale(Vector2f vec) {
        scale.x = vec.x;
        scale.y = vec.y;
        return this;
    }

    public Transform rotate(float angle) {
        rotation += angle;
        return this;
    }
    public Transform setRotation(float angle) {
        rotation = angle;
        return this;
    }

    /**
     * Turn all of our transformation parts into a projection matrix
     */
    public Matrix4f getProjection(Matrix4f target) {
        target.translate(position.x, position.y, 0);
        target.rotate(rotation, Z_AXIS);
        target.scale(scale.x, scale.y, 1);
        return target;
    }

}
