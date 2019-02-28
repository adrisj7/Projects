package gamemaker;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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

    // Creation code
    String code = "";

    // Open From File constructor
    public BufferedRoom(String roomGMXFname) {
        this();
        // If file doesn't exist, move on.
        if (!new File(roomGMXFname).exists()) {
            JOptionPane.showMessageDialog(null,
                    "Room BASE file not found at " + roomGMXFname + ". Ignoring and making room file from scratch",
                    "Room BASE File Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Document doc = FileHandler.readXML(roomGMXFname);
        caption = readProperty(doc, "caption");
        width = Integer.parseInt(readProperty(doc, "width"));
        height = Integer.parseInt(readProperty(doc, "height"));
        vsnap = Integer.parseInt(readProperty(doc, "vsnap"));
        hsnap = Integer.parseInt(readProperty(doc, "hsnap"));
        speed = Integer.parseInt(readProperty(doc, "speed"));
        persistent = readProperty(doc, "persistent").equals("-1");
        colour = Integer.parseInt(readProperty(doc, "colour"));
        showColour = readProperty(doc, "showcolour").equals("-1");
        code = readProperty(doc, "code");
        enableViews = readProperty(doc, "enableViews").equals("-1");
        clearViewBackground = readProperty(doc, "clearViewBackground").equals("-1");
        clearDisplayBuffer = readProperty(doc, "clearDisplayBuffer").equals("-1");

        // Oh boy
        // Multi lined baby.
        // This grabs the first ``<views> <view visible="-1" ... /> ... </views>`` view
        Element view0 = (Element) ((Element) doc.getDocumentElement().getElementsByTagName("views").item(0))
                .getElementsByTagName("view").item(0);
        viewWview = Integer.parseInt(view0.getAttribute("wview"));
        viewHview = Integer.parseInt(view0.getAttribute("hview"));
        viewWport = Integer.parseInt(view0.getAttribute("wport"));
        viewHport = Integer.parseInt(view0.getAttribute("hport"));

        // Save instances
        NodeList instances = ((Element) doc.getDocumentElement().getElementsByTagName("instances").item(0))
                .getElementsByTagName("instance");
        for (int i = 0; i < instances.getLength(); i++) {
            if (!(instances.item(i) instanceof Element))
                continue;
            Element elem = (Element) instances.item(i);
            String objName = elem.getAttribute("objName");
            String utilName = elem.getAttribute("name");
            String code = elem.getAttribute("code");
            int x = Integer.parseInt(elem.getAttribute("x"));
            int y = Integer.parseInt(elem.getAttribute("y"));
            double scaleX = Double.parseDouble(elem.getAttribute("scaleX"));
            double scaleY = Double.parseDouble(elem.getAttribute("scaleX"));
            double rotation = Double.parseDouble(elem.getAttribute("rotation"));
            BufferedInstance instance = new BufferedInstance(objName, x, y);
            instance.setUtilName(utilName);
            instance.setCode(code);
            instance.setScaleX(scaleX);
            instance.setScaleY(scaleY);
            instance.setRotation(rotation);
            addInstance(instance);
        }

        // Save Tiles
        NodeList tiles = ((Element) doc.getDocumentElement().getElementsByTagName("tiles").item(0))
                .getElementsByTagName("tile");
        for (int i = 0; i < tiles.getLength(); i++) {
            if (!(tiles.item(i) instanceof Element))
                continue;
            Element elem = (Element) tiles.item(i);
            String bgName = elem.getAttribute("bgName");
            String utilName = elem.getAttribute("name");
            int x = Integer.parseInt(elem.getAttribute("x"));
            int y = Integer.parseInt(elem.getAttribute("y"));
            double scaleX = Double.parseDouble(elem.getAttribute("scaleX"));
            double scaleY = Double.parseDouble(elem.getAttribute("scaleX"));
            int w = Integer.parseInt(elem.getAttribute("w"));
            int h = Integer.parseInt(elem.getAttribute("h"));
            int xo = Integer.parseInt(elem.getAttribute("xo"));
            int yo = Integer.parseInt(elem.getAttribute("yo"));
            int id = Integer.parseInt(elem.getAttribute("id"));
            int depth = Integer.parseInt(elem.getAttribute("depth"));
            BufferedGMTile tile = new BufferedGMTile(bgName, x, y, w, h, xo, yo, id, depth);
            tile.setUtilName(utilName);
            tile.setScaleX(scaleX);
            tile.setScaleY(scaleY);
            addTile(tile);
        }
    }

    // Make New constructor
    public BufferedRoom(int tileWidth, int tileHeight, int widthInTiles, int heightInTiles) {
        this();
        width = tileWidth * widthInTiles;
        height = tileHeight * heightInTiles;
        vsnap = tileWidth;
        hsnap = tileHeight;
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
        createSimpleVariable(doc, "code", code);
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
            // Util name: If doesn't exist, generate. Otherwise, use last util name.
            if (instance.getUtilName().equals(""))
                ielem.setAttribute("name", "inst_" + ++nameCounter);
            else
                ielem.setAttribute("name", instance.getUtilName());
            ielem.setAttribute("locked", "0");
            ielem.setAttribute("code", instance.getCode());
            ielem.setAttribute("scaleX", "" + instance.getXScale());
            ielem.setAttribute("scaleY", "" + instance.getYScale());
            ielem.setAttribute("colour", "4294967295");
            ielem.setAttribute("rotation", "" + instance.getRotation());
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
            // Util name: If doesn't exist, generate. Otherwise, use last util name.
            if (tile.getUtilName().equals(""))
                telem.setAttribute("name", "inst_" + ++nameCounter);
            else
                telem.setAttribute("name", tile.getUtilName());
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
            value = new String((boolean) value ? "-1" : "0");
        }
        Element newElem = doc.createElement(name);
        newElem.appendChild(doc.createTextNode(value.toString()));
        root.appendChild(newElem);
    }

    private static void createSimpleVariable(Document doc, String name, Object value) {
        Element root = doc.getDocumentElement();
        createSimpleVariable(doc, root, name, value);
    }

    private static String readProperty(Document doc, String name) {
        Element root = doc.getDocumentElement();
        Element elem = (Element) root.getElementsByTagName(name).item(0);
        return elem.getTextContent();
    }
    /// end of Utilities

    /// Modifying/Creating a room functions
    public void addTile(BufferedGMTile tile) {
        tiles.add(tile);
    }

    public void addInstance(BufferedInstance instance) {
        instances.add(instance);
    }
    /// end of Modifying/Creating a room functions

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

    public static void main(String[] args) {
        BufferedRoom room = new BufferedRoom();
        room.saveToFile("ROOM_TEST.room.gmx");
    }
}
