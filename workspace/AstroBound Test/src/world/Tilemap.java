package world;

import engine.rendering.Shader;

public class Tilemap {

    private short[] tiles;
    private int width;
    private int height;

    private TilesetRenderer renderer;

    // The "position" of this map
    private int offsetX;
    private int offsetY;

    public Tilemap(int width, int height, int offsetX, int offsetY, TilesetRenderer renderer) {
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.renderer = renderer;
        tiles = new short[width * height];
    }

    /**
     * Sets the tile at position (x,y) to id "id".
     * @param id
     * @param x
     * @param y
     */
    public void setTile(int id, int x, int y) {
        x -= offsetX;
        y -= offsetY;
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return;
        }
        tiles[y*width + x] = (short) id;
    }

    /**
     * @param x
     * @param y
     * @return the short ID of the tile at position (x,y)
     */
    public short getTileID(int x, int y) {
        x -= offsetX;
        y -= offsetY;
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return 0;
        }
        return tiles[y*width + x];
    }

    /**
     * Renders our tilemap with all of its tiles
     * @param renderer: The tileset renderer that we use to render our tiles
     * @param shader:   A shader that these tiles will be drawn from
     * @param camera:   Our camera
     */
    public void render(Shader shader) {
        for(int yy = 0; yy < height; yy++) {
            for(int xx = 0; xx < width; xx++) {
                renderer.renderTile(getTileID(xx + offsetX,yy + offsetY), xx + offsetX, yy + offsetY, shader);
            }
        }
    }
}
