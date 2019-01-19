package gamemaker;

/**
 * Contains all info about an instance placed in a room
 * 
 * (like object_index, position, scaling, creation code)
 */
public class BufferedInstance {

    public static final String DEFAULT_COLOR = "4294967295";

    private String objName;
    private String utilName = ""; // the "name" attribute (ex. "inst_727F3751")
    private int x, y;
    private double scaleX = 1, scaleY = 1;
    private double rotation = 0;
    private String code = "";

    public BufferedInstance(String objName, int x, int y) {
        this.objName = objName;
        this.x = x;
        this.y = y;
    }

    /// SETTERS
    public void setUtilName(String name) {
        this.utilName = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setScaleX(double scaleX) {
        this.scaleX = scaleX;
    }

    public void setScaleY(double scaleY) {
        this.scaleY = scaleY;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    /// GETTERS

    public String getObjName() {
        return objName;
    }

    public String getUtilName() {
        return utilName;
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

    public double getRotation() {
        return rotation;
    }

    public String getCode() {
        return code;
    }
}
