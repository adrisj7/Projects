package gamemaker;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import util.FileHandler;

/**
 * Contains project data.
 * 
 * THIS DOES NOT VALIDATE that any assets are there: This merely controls an XML File.
 * 
 * This should be used IN TANDEM with BufferedRoom or any other buffered types that create and save assets.
 * 
 * @author adris
 *
 */
public class BufferedGMProject {

    private Document doc;
    private String projectFname;

    public BufferedGMProject(String projectGMXFname) {
        doc = FileHandler.readXML(projectGMXFname);
        projectFname = projectGMXFname;
    }

    private void addItem(String itemType, String itemFolderName, String itemName) {
        Element root = doc.getDocumentElement();
        Element rooms = (Element) root.getElementsByTagName(itemType + "s").item(0);

        // First check if a room with the same name exists
        NodeList roomList = rooms.getElementsByTagName(itemType);
        for (int i = 0; i < roomList.getLength(); i++) {
            if (roomList.item(i).getTextContent().equals(itemFolderName + "\\" + itemName)) {
                System.out.println(itemType + " \"" + itemName + "\" already exists!");
                return;
            }
            // System.out.println(roomList.item(i).getTextContent());
        }

        Element newRoom = doc.createElement(itemType);
        newRoom.setTextContent(itemFolderName + "\\" + itemName);
        rooms.appendChild(newRoom);
    }

    public void addRoom(String roomName) {
        addItem("room", "rooms", roomName);
    }

    public void addBackground(String backgroundName) {
        addItem("background", "background", backgroundName);
    }

    public void addSprite(String spriteName) {
        addItem("sprite", "sprites", spriteName);
    }

    /**
     * Saves to file
     */
    public void save(String fname) {
        FileHandler.saveXML(doc, fname, true);
    }

    /**
     * Saves to the same file we used to load our project
     */
    public void save() {
        save(projectFname);
    }

    // General loading testing
    public static void main(String[] args) {
        BufferedGMProject project = new BufferedGMProject(
                "C:/Users/adris/Documents/Projects/GameMaker/Projects/YUMEngine.gmx/YUMEngine.project.gmx");
        project.addBackground("bgNEW");
        project.save();
    }
}
