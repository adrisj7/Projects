package tiled;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import util.FileHandler;

/**
 * Holds tilemap data
 *
 */
public class BufferedTileMap {

    // All tilesets that this tilemap uses
    private List<BufferedTilesetPair> tilesets;
    // Layers
    private List<BufferedLayer> layers;

    /** width/height in tiles **/
    private int width, height;
    private int tileWidth, tileHeight;

    // The name of the tilemap
    private String name;

    /**
     * @param tmxFname: Path to a Tiled .tmx (TileMap) file
     */
    public BufferedTileMap(String tmxFname) {
        name = FileHandler.getNameWithoutExtension(tmxFname);
        Document doc = FileHandler.readXML(tmxFname);
        Element root = doc.getDocumentElement();

        // width and height of map
        width = Integer.parseInt(root.getAttribute("width"));
        height = Integer.parseInt(root.getAttribute("height"));
        tileWidth = Integer.parseInt(root.getAttribute("tilewidth"));
        tileHeight = Integer.parseInt(root.getAttribute("tileheight"));

        // Parse tileset data
        tilesets = new ArrayList<>(); // Init first!
        NodeList tilesetNodes = root.getElementsByTagName("tileset");
        for (int i = 0; i < tilesetNodes.getLength(); i++) {
            if (!(tilesetNodes.item(i) instanceof Element))
                continue;
            Element tilesetElement = (Element) tilesetNodes.item(i);

            BufferedTilesetPair data = new BufferedTilesetPair();

            // Get relative path of tileset
            String relativeImageFname = tilesetElement.getAttribute("source");
            Path tmxPath = Paths.get(tmxFname);
            String tilesetFilePath = tmxPath.getParent() + "\\" + relativeImageFname;
            Path tilesetPath = Paths.get(tilesetFilePath);
            try {
                tilesetPath = tilesetPath.toRealPath();
            } catch (IOException e) {
                System.out.println("Failed to interpret Tiled TileMap path!");
                e.printStackTrace();
            }
            BufferedTileset tileset = new BufferedTileset(tilesetPath.toString());

            // Get firstgid (the thing that we offset our tiles for to distinguish between
            // different tilesets)
            int firstgid = Integer.parseInt(tilesetElement.getAttribute("firstgid"));

            data.tileset = tileset;
            data.firstgid = firstgid;

            tilesets.add(data);
        }

        // Parse layers
        layers = new ArrayList<>();
        NodeList layerNodes = root.getElementsByTagName("layer");
        for (int i = 0; i < layerNodes.getLength(); i++) {
            if (layerNodes.item(i) instanceof Element)
                layers.add(new BufferedLayer((Element) layerNodes.item(i)));
        }
    }

    /**
     * Utility Class used for storing tileset data and interpreting tile IDs Holds a
     * tileset
     */
    private static class BufferedTilesetPair {
        public BufferedTileset tileset;
        public int firstgid;
    }

    /**
     * Utility Class used to store all data in a layer
     *
     */
    public class BufferedLayer {
        private String name;
        private BufferedTile[][] tiles;

        // <layer id="..." width="..." height="...">
        // <data encoding="csv"> ... </data>
        // </layer>
        public BufferedLayer(Element layerElement) {

            // Tile array width/height
            int width = Integer.parseInt(layerElement.getAttribute("width"));
            int height = Integer.parseInt(layerElement.getAttribute("height"));
            tiles = new BufferedTile[width][height];

            // Name
            name = layerElement.getAttribute("name");

            // The tiles themselves
            Element dataElement = (Element) layerElement.getElementsByTagName("data").item(0);
            String rawData = dataElement.getTextContent();
            rawData = rawData.replaceAll("\n", ""); // Remove newlines
            String[] ids = rawData.split(",");
            for (int i = 0; i < ids.length; i++) {
                int id = Integer.parseInt(ids[i]);
                if (id == 0)
                    continue;
                BufferedTile tile = grabTile(id);
                if (tile == null)
                    continue;
                int xx = i % width;
                int yy = i / height;
                tiles[xx][yy] = tile;
            }
        }

        // BufferedLayer GETTERS
        public String getName() {
            return name;
        }

        public BufferedTile getTile(int x, int y) {
            return tiles[x][y];
        }

        public int getWidth() {
            return tiles.length;
        }

        public int getHeight() {
            if (getWidth() == 0)
                return 0;
            return tiles[0].length;
        }

    } // end BufferedLayer

    /**
     * @param id: A given id from <data encoding="csv">...(data)... </data>
     * @return the corresponding tile, utilizing all tilesets and their firstgid's
     */
    private BufferedTile grabTile(int id) {
        // Iterate backwards
        ListIterator<BufferedTilesetPair> iter = tilesets.listIterator(tilesets.size());
        while (iter.hasPrevious()) {
            BufferedTilesetPair current = iter.previous();
            // If we've found a firstgid below our id, our id falls within this tileset.
            if (current.firstgid < id) {
                int realID = id - current.firstgid;
                return current.tileset.getTile(realID);
            }
        }
        // Shouldn't happen, unless we give a value of 0 or negative values
        // Or if there are no tilesets.
        return null;
    }

    /// BufferedTileMap GETTERS

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getTilesetCount() {
        return tilesets.size();
    }

    public BufferedTileset getTileset(int index) {
        return tilesets.get(index).tileset;
    }

    /** Returns the number of tile layers in this tilemap **/
    public int getLayerCount() {
        return layers.size();
    }

    public BufferedLayer getLayer(int layerID) {
        return layers.get(layerID);
    }

    public String getName() {
        return name;
    }

    // Gets GameMaker equivalent name
    public String getGMName() {
        return "room_" + getName();
    }

    // Generic testing
    public static void main(String[] args) {
        BufferedTileMap map = new BufferedTileMap("C:/Users/adris/Documents/TEMP/TestMap.tmx");

        for (int i = 0; i < map.getLayerCount(); i++) {
            BufferedLayer layer = map.getLayer(i);
            System.out.println("LAYER #" + i);
            for (int yy = 0; yy < layer.getHeight(); yy++) {
                for (int xx = 0; xx < layer.getWidth(); xx++) {
                    BufferedTile t = layer.getTile(xx, yy);
                    if (t == null)
                        System.out.print("0, ");
                    else
                        System.out.print(t + ", ");
                }
                System.out.println();
            }
        }
    } // end main
}
