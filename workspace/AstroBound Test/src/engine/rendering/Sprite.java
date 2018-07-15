package engine.rendering;

import org.joml.Matrix4f;
import org.joml.Vector2f;

import engine.core.Main;

/**
 * Holds a rectangular model with a texture, to simplify sprite rendering.
 *
 */
public class Sprite {

    public static final float SCALE_FACTOR = 4;

    /* 
     * Standard texture coordinates and indices for a rectangular texture 
     */
    private static final float texCoords[] = {
            0, 0, 
            SCALE_FACTOR, 0, 
            SCALE_FACTOR, SCALE_FACTOR, 
            0, SCALE_FACTOR 
    };
    private static final int indices[] = {
            0, 1, 
            2, 2, 
            3, 0 
    };

    private Model model;
    private Texture texture;
    private Shader shader;

    private Vector2f center; // The center of the sprite (where to rotate + scale from)

    public Sprite(String fname) {
        texture = new Texture(fname);

        // TODO: Why 4??
        float w = (float)texture.getWidth() / (4*SCALE_FACTOR);
        float h = texture.getHeight()/ (4*SCALE_FACTOR);
        float[] vertices = new float[] { 
                0, h, // TOP LEFT
                w, h, // TOP RIGHT
                w, 0, // BOT RIGHT
                0, 0  // BOT LEFT
         };
        model = new Model(vertices,texCoords,indices);

        shader = Main.getInstance().getMainShader();

        center = new Vector2f(0,0);
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    public void setCenter(Vector2f center) {
        this.center = center;
    }

    /**
     * Renders the sprite, given a transformation
     */
    public void render(Transform trans) {
        Camera camera = Main.getInstance().getCamera();

        // Deal with the center thing
        trans.translate(-center.x, -center.y);
        Matrix4f result = new Matrix4f();
        camera.getProjection().translate(center.x,center.y, 0, result);

        shader.bind();
        shader.setUniformInt("sampler", 0);
        shader.setUniformMatrix4f("projection", trans.getProjection(result).translate(-1 * center.x, -1 * center.y,0));
        texture.bind(0);

        model.render();

        // Deal with the center thing
        trans.translate(center.x, center.y);

    }

    /**
     * Renders the sprite, given position, scale, and rotation
     */
    public void render(Vector2f position, Vector2f scale, float rotation) {
        Transform trans = new Transform().setTranslation(position).setScale(scale).setRotation(rotation);
        render(trans);
    }

}
