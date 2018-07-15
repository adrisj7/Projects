package world;

import org.joml.Vector2f;

import engine.rendering.Shader;
import engine.util.CollisionBox;

public class World {

    private static final int CHUNK_WIDTH = 64;
    private static final int CHUNK_HEIGHT = 64;

    private static final Tilesheet SHEET = new Tilesheet("TilesetEarth", 4);

    private Tilemap map;
    private TilesetRenderer tileRenderer;

    public World() {
        tileRenderer = new TilesetRenderer(SHEET);
        map = new Tilemap(CHUNK_WIDTH, CHUNK_HEIGHT, 0, -4, tileRenderer);
    }

//    // TODO: Delete this, and make all world generation happen internally
//    public void setTile(short id, int x, int y) {
//        map.setTile(id, x, y);
//    }

    public short getTileID(int x, int y) {
        return map.getTileID(x, y);
    }

    public void render(Shader shader) {
        map.render(shader);
    }

    /**
     * Generate the world
     */
    public void generate() {
        for(int xx = 0; xx < CHUNK_WIDTH; xx++) {
            int height = (int)(6 * Math.sin((double)xx * 2.0*Math.PI / (CHUNK_WIDTH / 2)));
            map.setTile(1, xx, height);
            for(int yy = height - 1; yy > -10; yy--) {
                if (yy > height - 3)
                    map.setTile(1, xx, yy);
                else
                    map.setTile(2, xx, yy);
            }
        }
//        setTile((short)2, 0, 0);
    }

    /**
     * 
     * TODO: Maybe return a tile, if possible?
     * @param offset
     * @param box
     * @param tileLocation: Store the tile that we collided with in this position.
     * 
     * @return Is the collision box "box" colliding with any tiles at "offset"?
     */
    public boolean checkCollision(Vector2f offset, CollisionBox box, Vector2f tileLocation) {
        int left =   (int) Math.floor(offset.x + box.offset.x);
        int right =  (int) Math.floor(offset.x + box.offset.x + box.size.x);
        int bottom = (int) Math.floor(offset.y + box.offset.y);
        int top =    (int) Math.floor(offset.y + box.offset.y + box.size.y);

        for(int yy = bottom; yy <= top; yy++) {
            for(int xx = left; xx <= right; xx++) {
                if (getTileID(xx, yy) != 0) {
                    tileLocation.x = xx;
                    tileLocation.y = yy;
                    return true;
                }
            }
        }
        return false;
    }
}
