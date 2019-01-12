package gamemaker;

/**
 * Contains all info about an instance placed in a room
 * 
 * (like object_index, position, scaling, creation code)
 */
public class BufferedInstance {

    public static final String DEFAULT_COLOR = "4294967295";

    private String objName;
    private int x, y;
    private double scaleX = 1, scaleY = 1;
    private String code = "";

    public BufferedInstance(String objName, int x, int y) {
        this.objName = objName;
        this.x = x;
        this.y = y;
    }

    /// GETTERS

    public String getObjName() {
        return objName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getXScale() {
        return scaleX;
    }

    public double getYScale() {
        return scaleY;
    }

    public String getCode() {
        return code;
    }
}
