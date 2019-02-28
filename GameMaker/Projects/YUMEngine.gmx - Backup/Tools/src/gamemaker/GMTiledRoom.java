package gamemaker;

import tiled.BufferedTile;
import tiled.BufferedTileMap;
import tiled.BufferedTileMap.BufferedLayer;

/**
 * An extension of BufferedRoom to add Tiled and YUMEngine specific
 * functionality
 */
public class GMTiledRoom extends BufferedRoom {

    public static final String GM_OBJ_COLLISION_NAME = "YUMEngine_objCollision";

    private BufferedTileMap map;

    // Creates a GM Tiled Room from a Base File and a Tiled Map
    public GMTiledRoom(String baseGMXFname, BufferedTileMap map) {
        super(baseGMXFname);

        this.map = map;

        // Conversions!
        this.setWidth(map.getWidth() * map.getTileWidth());
        this.setHeight(map.getHeight() * map.getTileHeight());

        // Tiles: Layers!
        int baseDepth = 10000000;
        int idCounter = 10000001; // for the "id" attribute in each tile (idk either)
        for (int i = 0; i < map.getLayerCount(); i++) {
            BufferedLayer layer = map.getLayer(i);
            // Adjust depth based on layer order (which matters!)
            int depth = baseDepth - 10 * i;
            for (int xx = 0; xx < layer.getWidth(); xx++) {
                for (int yy = 0; yy < layer.getHeight(); yy++) {
                    // Find and add the tile
                    BufferedTile tile = layer.getTile(xx, yy);
                    if (tile == null)
                        continue;
                    String bgName = tile.parent.getGMName();
                    int width = tile.parent.getTileWidth();
                    int height = tile.parent.getTileHeight();
                    int x = width * xx;
                    int y = height * yy;
                    int tx = tile.id % tile.parent.getWidth(), ty = tile.id / tile.parent.getHeight();
                    int xo = tx * width;
                    int yo = ty * height;
                    BufferedGMTile gmTile = new BufferedGMTile(bgName, x, y, width, height, xo, yo, idCounter, depth);
                    this.addTile(gmTile);
                    // Other tile properties
                    if (tile.collision) {
                        addCollision(xx, yy);
                    }
                    idCounter++;
                }
            }
        }
    }

    /**
     * Add a collision at tileX, tileY.
     * This places a collision object at the tile's location
     */
    public void addCollision(int tileX, int tileY) {
        int tileW = map.getTileWidth(),
            tileH = map.getTileHeight();
        int xa = tileX * tileW,
            ya = tileY * tileH;
        BufferedInstance objCollision = new BufferedInstance(GM_OBJ_COLLISION_NAME, xa, ya);
        // By convention, objCollision's width and height is 1 pixel, so scale em
        objCollision.setScaleX(tileW);
        objCollision.setScaleY(tileH);
        this.addInstance(objCollision);
    }
}
