package gamemaker;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import util.FileHandler;

/**
 * Contains project data.
 * 
 * THIS DOES NOT VALIDATE that any assets are there: This merely controls an XML
 * File.
 * 
 * This should be used IN TANDEM with BufferedRoom or BufferedBackground or any
 * other buffered types that create and save assets.
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
        NodeList searchQuery = root.getElementsByTagName(itemType + "s");
        // Find the proper group (the one with YUMEngine)
        String groupName = "YUMEngine Generated";
        Element items = null;
        for(int i = 0; i < searchQuery.getLength(); i++) {
            if (!(searchQuery.item(i) instanceof Element))
                continue;
            Element group = (Element) searchQuery.item(i);
            if (group.getAttribute("name").equals(groupName)) {
                // We got em
                items = group;
                break;
            }
        }
        // if no item was found, just make a new one.
        if (items == null) {
            items = doc.createElement(itemType + "s");
            items.setAttribute("name", groupName);
            searchQuery.item(0).appendChild(items);
        }

        // First check if a room/sprite/background/etc. with the same name exists
        NodeList itemList = items.getElementsByTagName(itemType);
        for (int i = 0; i < itemList.getLength(); i++) {
            if (itemList.item(i).getTextContent().equals(itemFolderName + "\\" + itemName)) {
                System.out.println(itemType + " \"" + itemName + "\" already exists!");
                return;
            }
            // System.out.println(roomList.item(i).getTextContent());
        }

        Element newRoom = doc.createElement(itemType);
        newRoom.setTextContent(itemFolderName + "\\" + itemName);
        items.appendChild(newRoom);
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
