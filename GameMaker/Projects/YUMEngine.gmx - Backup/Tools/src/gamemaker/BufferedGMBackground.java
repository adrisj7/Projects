package gamemaker;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import util.FileHandler;

public class BufferedGMBackground {
    private Document doc;
    private String fname = "";

    // Properties and defaults
    private boolean isTileset = false;
    private int tileWidth = 32;
    private int tileHeight = 32;
    private int tileXOff = 0;
    private int tileYOff = 0;
    private int tileHSep = 0;
    private int tileVSep = 0;
    private boolean hTile = false;
    private boolean vTile = false;

    private int width = 0;
    private int height = 0;
    private String data = ""; // ex. "images\tileset_0.png"

    // Initialize with file
    public BufferedGMBackground(String fname) {
        this.fname = fname;
        doc = FileHandler.readXML(fname);

        isTileset = readElement("istileset").equals("-1");
        tileWidth = Integer.parseInt(readElement("tilewidth"));
        tileHeight = Integer.parseInt(readElement("tileheight"));
        tileXOff = Integer.parseInt(readElement("tilexoff"));
        tileYOff = Integer.parseInt(readElement("tileyoff"));
        tileHSep = Integer.parseInt(readElement("tilehsep"));
        tileVSep = Integer.parseInt(readElement("tilevsep"));
        hTile = readElement("HTile").equals("-1");
        vTile = readElement("VTile").equals("-1");
        width = Integer.parseInt(readElement("width"));
        height = Integer.parseInt(readElement("height"));
        data = readElement("data");
    }

    // Initialize without file
    public BufferedGMBackground() {
    }

    public void save(String fname) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return;
        }
        doc = dBuilder.newDocument();
        doc.appendChild(doc.createElement("background"));
        addElement("istileset", isTileset ? "-1" : "0");
        addElement("tilewidth", tileWidth);
        addElement("tileheight", tileHeight);
        addElement("tilexoff", tileXOff);
        addElement("tileyoff", tileYOff);
        addElement("tilehsep", tileHSep);
        addElement("tilevsep", tileVSep);
        addElement("HTile", hTile ? "-1" : "0");
        addElement("VTile", vTile ? "-1" : "0");
        // TextureGroups that are kinda useless here
        Element textureGroups = doc.createElement("TextureGroups");
        Element subTextureGroup = doc.createElement("TextureGroup0");
        subTextureGroup.setTextContent("0");
        textureGroups.appendChild(subTextureGroup);
        doc.getDocumentElement().appendChild(textureGroups);

        addElement("For3D", 0);
        addElement("width", width);
        addElement("height", height);
        addElement("data", data);

        FileHandler.saveXML(doc, fname, true);
    }

    public void save() {
        if (fname.equals("")) {
            System.err.println("BufferedGMBackground.save(): Invalid usage!");
            System.err.println("Must specify directory, since this file hasn't been loaded yet!");
            System.exit(1);
        }
        save(fname);
    }

    // Adds an element to root
    private Element addElement(String name, Object value) {
        if (doc == null) {
            System.err.println("CALLED THIS AT THE WRONG PLACE!");
        }
        Element root = doc.getDocumentElement();

        Element newElement = doc.createElement(name);
        newElement.setTextContent(value.toString());
        root.appendChild(newElement);
        return newElement;
    }

    // Reads the value of an element
    private String readElement(String name) {
        if (doc == null) {
            System.err.println("CALLED THIS AT THE WRONG PLACE!");
        }
        Element sub = (Element) doc.getElementsByTagName(name).item(0);
        return sub.getTextContent();
    }

    /// GETTERS AND SETTERS

    public boolean isTileset() {
        return isTileset;
    }

    public void setTileset(boolean isTileset) {
        this.isTileset = isTileset;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public void setTileWidth(int tileWidth) {
        this.tileWidth = tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public void setTileHeight(int tileHeight) {
        this.tileHeight = tileHeight;
    }

    public int getTileXOff() {
        return tileXOff;
    }

    public void setTileXOff(int tileXOff) {
        this.tileXOff = tileXOff;
    }

    public int getTileYOff() {
        return tileYOff;
    }

    public void setTileYOff(int tileYOff) {
        this.tileYOff = tileYOff;
    }

    public int getTileHSep() {
        return tileHSep;
    }

    public void setTileHSep(int tileHSep) {
        this.tileHSep = tileHSep;
    }

    public int getTileVSep() {
        return tileVSep;
    }

    public void setTileVSep(int tileVSep) {
        this.tileVSep = tileVSep;
    }

    public boolean ishTile() {
        return hTile;
    }

    public void sethTile(boolean hTile) {
        this.hTile = hTile;
    }

    public boolean isvTile() {
        return vTile;
    }

    public void setvTile(boolean vTile) {
        this.vTile = vTile;
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
    
    public String getData() {
        return data;
    }
    
    public void setData(String data) {
        this.data = data;
    }
    
    /// end GETTERS AND SETTERS

    // Test creation and saving!
    public static void main(String[] args) {
        String testPath = "C:\\Users\\adris\\Documents\\Projects\\GameMaker\\Projects\\YUMEngine.gmx\\background\\tileset_0.background.gmx";

        BufferedGMBackground bg = new BufferedGMBackground();
        bg.width = 32;
        bg.data = "images\\tileset_1.png";
        bg.save(testPath);
        System.out.println("Done");
    }
}
