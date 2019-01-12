package gamemaker;

import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import util.FileHandler;

/**
 * Holds ALL data relevant to a room in GMS
 */
public class BufferedRoom {


    /// General room settings

    // useless probably, just leave empty
    private String caption = "";
    // room_width and room_height
    private int width, height;
    // useless probably, unless you edit it which you won't
    private int vsnap = 32, hsnap = 32;
    // room_speed
    private int speed = 60;
    // Room persistence. Almost always keep false
    private boolean persistent = false;
    // background color RGB but with integers. Keep at 0 for black.
    private int colour = 0;
    // Whether to fill background with our BG color
    private boolean showColour = true;
    // Are views enabled? (almost always true)
    private boolean enableViews = true;
    // Should our view background be cleared by our background color? (usually true)
    private boolean clearViewBackground = true;
    // "Clear display buffer with Window Colour"? (usually true)
    private boolean clearDisplayBuffer = true;

    /// Maker settings are ignored

    /// Backgrounds are ignored

    /// View settings: Only one view supported

    // view_wview, view_hview, view_wport and view_hport
    // rpgmaker standards (13 x 17 grid of 32x32 tiles)
    private int viewWview = 544, viewHview = 416;
    private int viewWport = 544, viewHport = 416;

    /// Instances
    private List<BufferedInstance> instances;

    /// Tiles
    private List<BufferedGMTile> tiles;

    public BufferedRoom(String roomGMXFname) {
        this();
        Document doc = FileHandler.readXML(roomGMXFname);
        Element root = doc.getDocumentElement();

    }
    
    // Empty constructor
    public BufferedRoom() {
        instances = new LinkedList<>();
        tiles = new LinkedList<>();
    }

    public void saveToFile(String roomGMXFname) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            System.out.println("FAILED TO CONFIGURE PARSER WHILE TRYING TO SAVE XML FILE TO " + roomGMXFname + "!");
            e.printStackTrace();
            return;
        }
        Document doc = dBuilder.newDocument();
        Element root = doc.createElement("room");
        doc.appendChild(root);

        // The simple variables at the top
        createSimpleVariable(doc, "caption", caption);
        createSimpleVariable(doc, "width", width);
        createSimpleVariable(doc, "height", height);
        createSimpleVariable(doc, "vsnap", vsnap);
        createSimpleVariable(doc, "hsnap", hsnap);
        createSimpleVariable(doc, "isometric", 0); // we never change this
        createSimpleVariable(doc, "speed", speed);
        createSimpleVariable(doc, "persistent", persistent);
        createSimpleVariable(doc, "colour", colour);
        createSimpleVariable(doc, "showcolour", showColour);
        createSimpleVariable(doc, "code", ""); // This is a bit of a hassle, but maybe later?
        createSimpleVariable(doc, "enableViews", enableViews);
        createSimpleVariable(doc, "clearViewBackground", clearViewBackground);
        createSimpleVariable(doc, "clearDisplayBuffer", clearDisplayBuffer);

        // Maker settings (zero them all)
        Element makerSettings = doc.createElement("makerSettings");
        createSimpleVariable(doc, makerSettings, "isSet", 0);
        createSimpleVariable(doc, makerSettings, "w", 0);
        createSimpleVariable(doc, makerSettings, "h", 0);
        createSimpleVariable(doc, makerSettings, "showGrid", 0);
        createSimpleVariable(doc, makerSettings, "showObjects", 0);
        createSimpleVariable(doc, makerSettings, "showTiles", 0);
        createSimpleVariable(doc, makerSettings, "showBackgrounds", 0);
        createSimpleVariable(doc, makerSettings, "showForegrounds", 0);
        createSimpleVariable(doc, makerSettings, "showViews", 0);
        createSimpleVariable(doc, makerSettings, "deleteUnderlyingObj", 0);
        createSimpleVariable(doc, makerSettings, "deleteUnderlyingTiles", 0);
        createSimpleVariable(doc, makerSettings, "page", 0);
        createSimpleVariable(doc, makerSettings, "xoffset", 0);
        createSimpleVariable(doc, makerSettings, "yoffset", 0);
        root.appendChild(makerSettings);

        // Add backgrounds, all invisible and empty
        Element backgrounds = doc.createElement("backgrounds");
        for (int i = 0; i < 8; i++) {
            Element background = doc.createElement("background");
            background.setAttribute("visible", "0");
            background.setAttribute("foreground", "0");
            background.setAttribute("name", "");
            background.setAttribute("x", "0");
            background.setAttribute("y", "0");
            background.setAttribute("htiled", "-1");
            background.setAttribute("vtiled", "-1");
            background.setAttribute("hspeed", "0");
            background.setAttribute("vspeed", "0");
            background.setAttribute("stretch", "0");
            backgrounds.appendChild(background);
        }
        root.appendChild(backgrounds);

        // Add views, only the first one matters
        Element views = doc.createElement("views");
        for (int i = 0; i < 8; i++) {
            Element view = doc.createElement("view");
            view.setAttribute("visible", (i == 0) ? "-1" : "0");
            view.setAttribute("xview", "0");
            view.setAttribute("yview", "0");
            view.setAttribute("wview", Integer.toString(viewWview));
            view.setAttribute("hview", Integer.toString(viewHview));
            view.setAttribute("xport", "0");
            view.setAttribute("yport", "0");
            view.setAttribute("wport", Integer.toString(viewWport));
            view.setAttribute("hport", Integer.toString(viewHport));
            view.setAttribute("hborder", "32");
            view.setAttribute("vborder", "32");
            view.setAttribute("hspeed", "-1");
            view.setAttribute("vspeed", "-1");
            views.appendChild(view);
        }
        root.appendChild(views);

        // Instances!
        Element ielems = doc.createElement("instances");
        int nameCounter = 0;
        for (BufferedInstance instance : instances) {
            Element ielem = doc.createElement("instance");
            ielem.setAttribute("objName", instance.getObjName());
            ielem.setAttribute("x", "" + instance.getX());
            ielem.setAttribute("y", "" + instance.getY());
            ielem.setAttribute("name", "inst_" + ++nameCounter);
            ielem.setAttribute("locked", "0");
            ielem.setAttribute("code", instance.getCode());
            ielem.setAttribute("scaleX", "" + instance.getXScale());
            ielem.setAttribute("scaleY", "" + instance.getYScale());
            ielem.setAttribute("colour", "4294967295");
            ielem.setAttribute("rotation", "0");
            ielems.appendChild(ielem);
        }
        root.appendChild(ielems);

        // Tiles!
        Element telems = doc.createElement("tiles");
        int idCounter = 10000000;
        for (BufferedGMTile tile : tiles) {
            Element telem = doc.createElement("tile");
            telem.setAttribute("bgName", tile.getBgName());
            telem.setAttribute("x", "" + tile.getX());
            telem.setAttribute("y", "" + tile.getY());
            telem.setAttribute("w", "" + tile.getWidth());
            telem.setAttribute("h", "" + tile.getHeight());
            telem.setAttribute("xo", "" + tile.getXo());
            telem.setAttribute("yo", "" + tile.getYo());
            telem.setAttribute("id", "" + ++idCounter);
            telem.setAttribute("name", "inst_" + ++nameCounter);
            telem.setAttribute("depth", "" + tile.getDepth());
            telem.setAttribute("locked", "0");
            telem.setAttribute("colour", "4294967295");
            telem.setAttribute("scaleX", "1");
            telem.setAttribute("scaleY", "1");
            telems.appendChild(telem);
        }
        root.appendChild(telems);

        // Useless but (I think required?) Physics settings
        createSimpleVariable(doc, "PhysicsWorld", "0");
        createSimpleVariable(doc, "PhysicsWorldTop", "0");
        createSimpleVariable(doc, "PhysicsWorldLeft", "0");
        createSimpleVariable(doc, "PhysicsWorldRight", width);
        createSimpleVariable(doc, "PhysicsWorldBottom", height);
        createSimpleVariable(doc, "PhysicsWorldGravityX", "0");
        createSimpleVariable(doc, "PhysicsWorldGravityY", "0");
        createSimpleVariable(doc, "PhysicsWorldPixToMeters", "0.100000001490116");

        // Now we're done building our document! Just gotta save
        FileHandler.saveXML(doc, roomGMXFname, true);

        System.out.println("Saved Room to " + roomGMXFname + ".");
    }

    /// Utilities
    private static void createSimpleVariable(Document doc, Element root, String name, Object value) {
        if (value instanceof Boolean) {
            value = new String((boolean) value? "-1" : "0");
        }
        Element newElem = doc.createElement(name);
        newElem.appendChild(doc.createTextNode(value.toString()));
        root.appendChild(newElem);
    }

    private static void createSimpleVariable(Document doc, String name, Object value) {
        Element root = doc.getDocumentElement();
        createSimpleVariable(doc, root, name, value);
    }

    /// GETTERS AND SETTERS

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getVsnap() {
        return vsnap;
    }

    public void setVsnap(int vsnap) {
        this.vsnap = vsnap;
    }

    public int getHsnap() {
        return hsnap;
    }

    public void setHsnap(int hsnap) {
        this.hsnap = hsnap;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public int getColour() {
        return colour;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }

    public boolean isShowColour() {
        return showColour;
    }

    public void setShowColour(boolean showColour) {
        this.showColour = showColour;
    }

    public boolean isEnableViews() {
        return enableViews;
    }

    public void setEnableViews(boolean enableViews) {
        this.enableViews = enableViews;
    }

    public boolean isClearViewBackground() {
        return clearViewBackground;
    }

    public void setClearViewBackground(boolean clearViewBackground) {
        this.clearViewBackground = clearViewBackground;
    }

    public boolean isClearDisplayBuffer() {
        return clearDisplayBuffer;
    }

    public void setClearDisplayBuffer(boolean clearDisplayBuffer) {
        this.clearDisplayBuffer = clearDisplayBuffer;
    }

    public int getViewWview() {
        return viewWview;
    }

    public void setViewWview(int viewWview) {
        this.viewWview = viewWview;
    }

    public int getViewHview() {
        return viewHview;
    }

    public void setViewHview(int viewHview) {
        this.viewHview = viewHview;
    }

    public int getViewWport() {
        return viewWport;
    }

    public void setViewWport(int viewWport) {
        this.viewWport = viewWport;
    }

    public int getViewHport() {
        return viewHport;
    }

    public void setViewHport(int viewHport) {
        this.viewHport = viewHport;
    }

    public List<BufferedInstance> getInstances() {
        return instances;
    }

    public List<BufferedGMTile> getTiles() {
        return tiles;
    }

    public static void main(String[] args) {
        BufferedRoom room = new BufferedRoom();
        room.saveToFile("ROOM_TEST.room.gmx");
    }
}
