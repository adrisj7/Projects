package engine.rendering;

import org.joml.Matrix4f;
import org.joml.Vector2f;

public class Camera {

    // We scale everything by this, so that 1 unit = 16 pixels.
    public static final int SCALE_FACTOR = 16;

    private Vector2f position;
    private Vector2f scale;
    private Matrix4f projection;
    
    private int width, height;

    public Camera(int width, int height) {
        this.width = width;
        this.height = height;

        position = new Vector2f(0,0);
        scale = new Vector2f(SCALE_FACTOR, SCALE_FACTOR);
        projection = new Matrix4f().setOrtho2D(-width/2, width/2, -height/2, height/2);
    }

    // Methods to set our camera's position
    public void setPosition(Vector2f position) {
        this.position = position;
    }
    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
    }

    // Methods to set our camera's view
    public void setView(float width, float height) {
        this.width = (int)width;
        this.height = (int)height;
        projection = new Matrix4f().setOrtho2D(-width/2, width/2, -height/2, height/2);
    }
    public void setView(Vector2f size) {
        setView(size.x, size.y);
    }

    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getView() {
        return new Vector2f(width, height);
    }
    /**
     * @return The camera view, scaled to tile coordinates.
     */
    public Vector2f getTileView() {
        return getView().mul(1.0f / (float)SCALE_FACTOR);
    }

    /**
     * 
     * Applies our translation and scaling to get our camera's projection.
     * 
     * @return The projection that this camera is viewed from.
     */
    public Matrix4f getProjection() {
        Matrix4f result = new Matrix4f();
        Matrix4f translationMat = new Matrix4f().setTranslation(-1 * position.x, -1 * position.y, 0);
        Matrix4f scaleMat = new Matrix4f().scale(scale.x, scale.y, 1);

        projection.mul(scaleMat, result);
        result.mul(translationMat, result);
        return result;
    }
}
