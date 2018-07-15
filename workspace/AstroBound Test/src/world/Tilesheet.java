package world;

import org.joml.Matrix4f;

import engine.rendering.Shader;
import engine.rendering.Texture;

public class Tilesheet {

    private Texture texture;
    private Matrix4f scale;
    private Matrix4f translation; // TODO: Is this needed?

    private int widthTiles;

    public Tilesheet(String tileset, int widthTiles) {
        this.widthTiles = widthTiles;
        //TODO: Should we instead use width and height?
        this.texture = new Texture("res/Sprites/Tilesets/" + tileset + ".png");

        scale = new Matrix4f().scale(1.0f / (float)widthTiles);
        translation = new Matrix4f();
    }

    /**
     * Binds a tile to render it
     * @param shader
     * @param id
     */
    public void bindTile(Shader shader, short id) {
        int tileX = id % widthTiles;
        int tileY = id / widthTiles;

        scale.translate(tileX,tileY,0, translation);

        shader.setUniformInt("sampler", 0);
        // NOTE: This assumes that the shader supports tilesheets
        shader.setUniformMatrix4f("texModifier", translation);

        texture.bind(0);
    }
}
