package tiled;

import java.io.File;

import javax.swing.JOptionPane;

import gamemaker.BufferedGMBackground;
import gamemaker.BufferedGMProject;
import gamemaker.BufferedGMTile;
import gamemaker.BufferedRoom;
import tiled.BufferedTileMap.BufferedLayer;
import util.FileHandler;

/**
 * Here's what this does.
 * 
 * You give it a TILED map file
 * 
 * It imports it into gamemaker.
 * 
 * How?
 * 
 * I'm glad you asked!
 * 
 * It follows these simple steps:
 * 
 * ! 1) You give it a TILED .tmx file directory ! 2) It "buffers" the tilemap
 * AND all of the tilesets that it uses ! 3) You give it a GM Project directory
 * ! 4) It edits your project's .project.gmx file to include the tilesets as
 * backgrounds ! 5) It makes a corresponding .background file for each tileset
 * image, and copies over the image ! used in the tilset. > 6) It makes a
 * gamemaker .room.gmx using the buffered tilemap 7) ??? 8) Profit
 *
 * KEY: ! = done! > = WIP or NEXT TASK!
 */

public class TiledToGameMakerConverter {

    public static void main(String[] args) {
        /*
         * UNCOMMENT ME WHEN DONE if (args.length != 0) {
         * System.out.println("Invalid number of arguments: " + args.length +
         * ". Expecting 1."); System.exit(1); } String mapfname = args[0];
         */
        String mapfname = "C:\\Users\\adris\\Documents\\TEMP\\TestMap.tmx";

        // Buffer the Tiled Tilemap
        BufferedTileMap map = new BufferedTileMap(mapfname);

        // Get the game maker project directory
        File projectFile = FileHandler.openFilePrompt();
        if (projectFile == null) {
            // User canceled
            System.exit(0);
        }
        String baseGMPath = projectFile.getParent();
        BufferedGMProject bufferedProject = new BufferedGMProject(projectFile.getAbsolutePath());

        // Add backgrounds. NOTE: If background already exists, this will do nothing.
        for (int i = 0; i < map.getTilesetCount(); i++) {
            BufferedTileset set = map.getTileset(i);
            File srcImg = set.getSourceImage();

            // Update project file
            String newBackgroundName = set.getGMName();
            bufferedProject.addBackground(newBackgroundName);

            // Copy file to images folder
            String imagesFolder = baseGMPath + "\\background\\images";
            // The name (ex. "tileset_0.png") of the file we're saving to
            String destImageName = newBackgroundName + "." + FileHandler.getExtension(srcImg.getName());
            FileHandler.copyFile(srcImg.getAbsolutePath(), imagesFolder + "\\" + destImageName);

            // Create background file
            BufferedGMBackground background = new BufferedGMBackground();
            background.setTileset(true);
            background.setWidth(set.getWidth());
            background.setHeight(set.getHeight());
            background.setTileWidth(set.getTileWidth());
            background.setTileHeight(set.getTileHeight());
            background.setData("images\\" + destImageName);
            background.save(baseGMPath + "\\background\\" + newBackgroundName + ".background.gmx");
        }

        // Save a GameMaker room
        String mapName = map.getGMName();
        System.out.println("MAP NAME: " + mapName);
        String roomDir = baseGMPath + "\\rooms\\" + mapName + ".room.gmx";

        // Check if base room exists
        BufferedRoom room;
        String baseRoomDir = baseGMPath + "\\rooms\\room_BASE.room.gmx";
        if (!new File(baseRoomDir).exists()) {
            JOptionPane.showMessageDialog(null,
                    "Room BASE file not found at " + baseRoomDir + ". Ignoring and making a new room file from scratch",
                    "Room BASE File",
                    JOptionPane.WARNING_MESSAGE);
            room = new BufferedRoom();
        } else {
//            JOptionPane.showMessageDialog(null, "Room BASE found, and using it!", "Room BASE File",
//                    JOptionPane.WARNING_MESSAGE);
            room = new BufferedRoom(baseRoomDir);
        }

        // Conversions!
        room.setWidth(map.getWidth() * map.getTileWidth());
        room.setHeight(map.getHeight()* map.getTileHeight());

        // Tiles: Layers!
        int baseDepth = 10000000;
        int idCounter = 10000001; // for the "id" attribute in each tile (idk either)
        for (int i = 0; i < map.getLayerCount(); i++) {
            BufferedLayer layer = map.getLayer(i);
            // Adjust depth based on layer order (which matters!)
            int depth = baseDepth - 10 * i;
            for (int xx = 0; xx < layer.getWidth(); xx++) {
                for (int yy = 0; yy < layer.getHeight(); yy++) {
                    BufferedTile tile = layer.getTile(xx, yy);
                    if (tile == null)
                        continue;
                    String bgName = tile.parent.getGMName();
                    int width  = tile.parent.getTileWidth();
                    int height = tile.parent.getTileHeight();
                    int x = width  * xx;
                    int y = height * yy;
                    int tx = tile.id % tile.parent.getWidth(),
                        ty = tile.id / tile.parent.getHeight();
                    int xo = tx * width;
                    int yo = ty * height;
                    BufferedGMTile gmTile = new BufferedGMTile(bgName, x, y, width, height, xo, yo, idCounter, depth);
                    room.addTile(gmTile);
                    idCounter++;
                }
            }
        }

        room.saveToFile(roomDir);
        bufferedProject.addRoom(mapName);
        bufferedProject.save();
    }
}
