package world;

import org.joml.Matrix4f;
import org.joml.Vector2f;

import engine.core.Main;
import engine.rendering.Camera;
import engine.rendering.Model;
import engine.rendering.Shader;

public class TilesetRenderer {

    /**
     * How many blocks away from the edges do we still render?
     */
    private static final float RENDER_BUFFER_THRESHOLD = 1.5f;

    // Square tile model
    private static Model tileModel;
//    private static HashMap<String, Texture> tileTextures = null;

    static {
        // Set up our tile model

        //TODO: Maybe use a sprite for this?
        float[] tileVertices = new float[] {
                0, 1, // TOP LEFT
                1, 1, // TOP RIGHT
                1, 0, // BOT RIGHT
                0, 0  // BOT LEFT        
        };
        float[] tileTexVertices = new float[] { 
                0, 0, 
                1, 0, 
                1, 1, 
                0, 1 
        };
        int[] tileIndices = new int[] { 
                0, 1, 
                2, 2, 
                3, 0 
        };

        tileModel = new Model(tileVertices, tileTexVertices, tileIndices);
    }

//    private Tileset tileset;
    private Tilesheet tilesheet;

//    public TilesetRenderer(Tileset tileset) {
      public TilesetRenderer(Tilesheet tilesheet) {
        this.tilesheet = tilesheet;
        // First time: Load up all tilemaps
//        if (tileTextures == null) {
//            tileTextures = new HashMap<>();
//            addTileset("test");
//        }
    }

    /**
     * Utility method used to add a tileset to our map by its name
     */
//    private static void addTileset(String name) {
//        tileTextures.put(name, new Texture("res/Sprites/Tilesets/" + name + ".png"));
//    }

    /**
     * Render a tile
     * 
     * TODO: Maybe there shouldn't be so many arguments...
     * 
     * @param id
     * @param x
     * @param y
     * @param shader
     * @param world
     * @param camera
     */
    public void renderTile(short id, int x, int y, Shader shader) {

        Camera camera = Main.getInstance().getCamera();

        Vector2f cposition = camera.getPosition();
        Vector2f cview     = camera.getTileView();

        // Is this tile within view?
        if ((cposition.x - cview.x/2 - x   > RENDER_BUFFER_THRESHOLD) ||
            (x - (cposition.x + cview.x/2) > RENDER_BUFFER_THRESHOLD) ||
            (cposition.y - cview.y/2 - y   > RENDER_BUFFER_THRESHOLD) ||
            (y - (cposition.y + cview.y/2) > RENDER_BUFFER_THRESHOLD)) {
            return;
        }

        shader.bind();

        // If the texture is there, bind it
//        if (tileTextures.containsKey(tileset.getTile(id).getTileset())) {
//            tileTextures.get(tileset.getTile(id).getTileset()).bind(0);
//        }
        tilesheet.bindTile(shader, id);
        Matrix4f tilePos = new Matrix4f().translate(x, y, 0);
        Matrix4f result = new Matrix4f();

        camera.getProjection().mul(tilePos, result);

        shader.setUniformInt("sampler", 0);
        shader.setUniformMatrix4f("projection", result);

        tileModel.render();
    }

}
