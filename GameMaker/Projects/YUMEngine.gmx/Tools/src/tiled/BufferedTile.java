package tiled;

// Struct style
public class BufferedTile {

    public BufferedTileset parent;

    // What id it is (from the parent tileset)
    public int id;

    // TILE SPECIFIC PROPERTIES
    public boolean collision;

    @Override
    public String toString() {
        return Integer.toString(id);
    }

}
