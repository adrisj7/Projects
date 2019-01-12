package gamemaker;

/**
 * All data held in a GameMaker Tile
 */
public class BufferedGMTile {

    private String bgName;
    private int x, y;
    private int width, height;
    private int xo, yo; // coordinates on the tile
    private int depth = 10000000;
    private double scaleX = 1, scaleY = 1;

    public BufferedGMTile(String bgName, int x, int y, int width, int height, int xo, int yo, int id, int depth) {
        this.bgName = bgName;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.xo = xo;
        this.yo = yo;
        this.depth = depth;

    }


    /// GETTERS

    public String getBgName() {
        return bgName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getXo() {
        return xo;
    }

    public int getYo() {
        return yo;
    }

    public int getDepth() {
        return depth;
    }

    public double getXScale() {
        return scaleX;
    }

    public double getYScale() {
        return scaleY;
    }
}
