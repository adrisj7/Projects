package tiled;

import java.io.File;

import gamemaker.BufferedGMBackground;
import gamemaker.BufferedGMProject;
import gamemaker.GMTiledRoom;
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
        String mapfname = "";
        File projectFile = null;
        switch (args.length) {
        // 1 arg: Just the TILED Map.
        // Prompt for GM Project
        case 1:
            mapfname = args[0];
            break;
        // 2 args: <TILED Map>, <GameMaker Project.gmx>
        case 2:
            mapfname = args[0];
            projectFile = new File(args[1]);
            if (!projectFile.exists()) {
                System.err.println("GameMaker Project File at \"" + args[1] + "\" does not exist!");
                System.exit(1);
            }
            break;
        default:
            System.out.println("Invalid number of arguments: " + args.length + ".");
            System.out.println("USAGE: yumeditor_export_to_gms [tiled_map.tmx] <gamemaker_project.gmx>");
            System.out.println("(where argument 2 is optional.)");
            System.exit(1);
        }

        // Buffer the Tiled Tilemap
        BufferedTileMap map = new BufferedTileMap(mapfname);

        // Get the game maker project directory
        if (projectFile == null)
            projectFile = FileHandler.openFilePrompt();
        // If it's still null, we cancel.
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

        String baseRoomDir = baseGMPath + "\\rooms\\room_BASE.room.gmx";
        GMTiledRoom room = new GMTiledRoom(baseRoomDir, map);

        room.saveToFile(roomDir);
        bufferedProject.addRoom(mapName);
        bufferedProject.save();
    }
}
