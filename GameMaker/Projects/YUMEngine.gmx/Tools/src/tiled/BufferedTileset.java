package tiled;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import util.FileHandler;

/**
 * Contains all relevant data that a Tiled tileset has
 */
public class BufferedTileset {

    private int width, height;
    private int tileWidth, tileHeight;
    private File sourceImage;

    private BufferedTile[] tiles;

    /**
     * 
     * @param tsxPath: Path to a Tiled .tsx (tilset) file
     */
    public BufferedTileset(String tsxFname) {

        // Grab XML Data from the path.
        Document doc = FileHandler.readXML(tsxFname);
        Element root = doc.getDocumentElement();
        // Tile width and height
        tileWidth = Integer.parseInt(root.getAttribute("tilewidth"));
        tileHeight = Integer.parseInt(root.getAttribute("tilewidth"));

        // Tileset width and height
        int tcount = Integer.parseInt(root.getAttribute("tilecount"));
        int cols = Integer.parseInt(root.getAttribute("columns"));
        width = cols;
        height = tcount / cols;
        tiles = new BufferedTile[tcount];

        // Image
        Element imgElement = (Element) doc.getElementsByTagName("image").item(0);
        String relativeImageFname = imgElement.getAttribute("source");
        Path tsxPath = Paths.get(tsxFname);
        Path imagePath = Paths.get(tsxPath.getParent() + "\\" + relativeImageFname);
        imagePath.normalize();
        sourceImage = imagePath.toFile();

        // Tiles
        NodeList parseTiles = doc.getElementsByTagName("tile");
        for (int i = 0; i < parseTiles.getLength(); i++) {
            Element elem = (Element) parseTiles.item(i);
            BufferedTile tile = parseTileData(elem);
            tiles[tile.id] = tile;
        }
    }

    /**
     * @param tileElement: A <tile id="#"> ... </tile> xml thing
     * @return A BufferedTile (packaged for our convenience)
     */
    private BufferedTile parseTileData(Element tileElement) {
        BufferedTile result = new BufferedTile();

        // General properties
        int id = Integer.parseInt(tileElement.getAttribute("id"));
        result.id = id;
        result.parent = this;

        // Property list (Custom Properties)
        NodeList properties = tileElement.getElementsByTagName("properties").item(0).getChildNodes();
        for (int i = 0; i < properties.getLength(); i++) {
            Element property;
            try {
                property = (Element) properties.item(i);
            } catch (Exception e) {
                continue;
            }
            String name = property.getAttribute("name");
            String value = property.getAttribute("value");

            // Parse ALL of our special properties
            if (name.equals("collision")) {
                result.collision = (value.equals("true"));
                continue;
            }
        }
        return result;
    }

    /// GETTERS AND SETTERS

    /** @return How many tiles wide this tileset is */
    public int getWidth() {
        return width;
    }

    /** @return How many tiles high this tileset is */
    public int getHeight() {
        return height;
    }

    /** @return How wide (px) each tile is */
    public int getTileWidth() {
        return tileWidth;
    }

    /** @return How high (px) each tile is */
    public int getTileHeight() {
        return tileHeight;
    }

    public File getSourceImage() {
        return sourceImage;
    }

    public BufferedTile getTile(int id) {
        return tiles[id];
    }

    // Tileset loading tests
    public static void main(String[] args) {
        new BufferedTileset("C:/Users/adris/Documents/TEMP/TestTileset.tsx");
    }
}
