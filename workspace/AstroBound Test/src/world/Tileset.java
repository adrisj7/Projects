package world;

/**
 * TODO: Replace this with Tilesheet
 * @author adris
 *
 */
public class Tileset {

    private Tile tiles[];

    public Tileset() {
        tiles = new Tile[16];

        // Add a test tile
        addTile((short) 0, "test");
    }

    public Tile addTile(short id, String tileset) {
        if (tiles[id] != null) {
            throw new IllegalStateException("Conflicting tile: Tile already exists at id " + id + "!");
        }
        if (id < 0 || id >= tiles.length) {
            throw new IllegalArgumentException("Attempted to place tile at id which is out of bounds: " + id + ", where size of tilemap is " + tiles.length + ".");
        }
        tiles[id] = new Tile(id, tileset);
        return tiles[id];
    }

    Tile getTile(short id) {
        return tiles[id];        
    }

    /** Tile
     * Our tile class that's part of this tileset.
     *
     */
    public class Tile {
        private short id;
        private String tileset; //TODO: Having this mapping might be a bad idea...

        public Tile(short id, String tileset) {
            this.id = id;
            this.tileset = tileset;
        }

        public short getID() {
            return id;
        }
        
        public String getTileset() {
            return tileset;
        }
    }
}
