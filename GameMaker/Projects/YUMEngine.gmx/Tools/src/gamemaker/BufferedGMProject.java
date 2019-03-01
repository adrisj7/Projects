package gamemaker;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
    // Every object is put in this group, only visible in GM:Studio.
    // (so that our generated items are separate from our User items)
    private static final String GM_ITEM_GROUP_NAME = "YUMEngine Generated";

    private Document doc;
    private String projectFname;

    public BufferedGMProject(String projectGMXFname) {
        doc = FileHandler.readXML(projectGMXFname);
        projectFname = projectGMXFname;
    }

    /**
     * Returns an element corresponding to the group with all the items of a type
     * AND of a certain group name.
     * 
     * @param           itemType: What item type is it? (ex. "object", "background",
     *                  ect.)
     * @param groupName What group is it in? (ex. "" for root, "YUMEngine
     *                  Generated")
     * @return the element that holds all items of that type and such
     */
    public Element getItemGroup(String itemType, String groupName) {
        Element root = doc.getDocumentElement();
        NodeList searchQuery = root.getElementsByTagName(itemType + "s");
        if (groupName.equals("")) {
            return (Element) searchQuery.item(0);
        }
        for (int i = 0; i < searchQuery.getLength(); i++) {
            if (!(searchQuery.item(i) instanceof Element))
                continue;
            Element group = (Element) searchQuery.item(i);
            if (group.getAttribute("name").equals(groupName)) {
                // We got em
                return group;
            }
        }
        // We found nothin
        return null;
    }

    public boolean itemExists(String itemType, String itemName) {
        Element root = doc.getDocumentElement();
        NodeList searchQuery = root.getElementsByTagName(itemType + "s");
        return itemExists((Element) searchQuery.item(0), itemName);
    }

    /**
     * @param group    The group Element that has items in it.
     * @param itemName The name of the item we're looking for (ex. "sprDeleteMe");
     * @return Does item with "itemName" in group "group" exist?
     */
    private boolean itemExists(Element group, String itemName) {
        NodeList children = group.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (!(child instanceof Element))
                continue;
            if (child.hasChildNodes()) {
                if (itemExists((Element) child, itemName)) {
                    return true;
                }
            }
            // ex. rooms\room_BASE
            String fullName = ((Element) child).getTextContent();
            String split[] = fullName.split("\\\\");
            String name = split[split.length - 1];
            if (name.equals(itemName)) {
                return true;
            }
        }
        return false;
    }

    private void addItem(String itemType, String itemFolderName, String itemName) {
        Element group = getItemGroup(itemType, GM_ITEM_GROUP_NAME);
        // if no item group was found, just make a new one.
        if (group == null) {
            group = doc.createElement(itemType + "s");
            group.setAttribute("name", GM_ITEM_GROUP_NAME);
            // Remember, if groupName = "", return the root group!
            getItemGroup(itemType, "").appendChild(group);
        }

        // If our item exists already, don't create it!
        if (itemExists(itemType, itemName)) {
            System.out.println(itemType + " \"" + itemName + "\" already exists!");
            return;
        }

        Element newRoom = doc.createElement(itemType);
        newRoom.setTextContent(itemFolderName + "\\" + itemName);
        group.appendChild(newRoom);
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

    public boolean objectExists(String objName) {
        return itemExists("object", objName);
    }

    /**
     * Sets the value of a GM Macro (ex. TILE_WIDTH)
     * @param macroName
     * @param macroValue
     */
    public void setMacro(String macroName, String macroValue) {
        Element root = doc.getDocumentElement();
        Element constantsGroup = null;
        NodeList searchQuery = root.getElementsByTagName("constants");
        // If constants don't exist, create em
        if (searchQuery.getLength() == 0) {
            constantsGroup = doc.createElement("constants");
            constantsGroup.setAttribute("number", "0");
        }
        Element targetMacro = null;
        // Search for our macro
        NodeList constants = constantsGroup.getChildNodes();
        for(int i = 0; i < constants.getLength(); i++) {
            Element elem = (Element)constants.item(i);
            if (elem.getAttribute("name").equals(macroName)) {
                targetMacro = elem;
                break;
            }
        }
        // If our macro doesn't exist yet, append and update it
        if (targetMacro == null) {
            int number = Integer.parseInt(constantsGroup.getAttribute("number"));
            constantsGroup.setAttribute("number", "" + (number+1));
            targetMacro = doc.createElement(macroName);
            constantsGroup.appendChild(targetMacro);
        }
        // Set macro value
        targetMacro.setTextContent(macroValue);
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
