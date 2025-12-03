package utilz;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import entities.Pig;
import main.Game;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import static utilz.Constants.ObjectConstants.DIAMOND;
import static utilz.Constants.ObjectConstants.HEART;

public class LoadSave {

    public static final String PLAYER_ATLAS = "player_sprites.png";
    public static final String LEVEL_ATLAS = "extendedsupermap.png";
    public static final String MENU_BUTTONS = "button_atlas.png";
    public static final String MENU_BACKGROUND = "menu_background.png";
    public static final String PAUSE_BACKGROUND = "pause_menu.png";
    public static final String SOUND_BUTTONS = "sound_button.png";
    public static final String URM_BUTTONS = "urm_buttons.png";
    public static final String VOLUME_BUTTONS = "volume_buttons.png";
    public static final String MENU_BACKGROUND_IMG = "menu_background_green.jpeg";
    public static final String PIG_SPRITE = "pig.png";
    public static final String STATUS_BAR = "health_power_bar.png";
    public static final String COMPLETED_IMG = "completed_sprite.png";
    public static final String ITEM_ATLAS = "items2.png";
    public static final String CONTAINER_ATLAS = "objects_sprites.png";
    public static final String CANNON_ATLAS = "cannon_sprites.png";
    public static final String CANNON_BALL = "ball.png";
    public static final String DEATH_SCREEN = "death_screen.png";
    public static final String OPTIONS_MENU = "options_background.png";


    // New constant for the TMX file name
    public static final String TMX_LEVEL = "supermap2.tmx";

    public static final String TMX_LEVEL_FOLDER = "/TMXlvls";

    // Existing method to load PNG/Image files
    public static BufferedImage GetSpriteAtlas(String fileName){
        BufferedImage img = null;
        InputStream is = LoadSave.class.getResourceAsStream("/"+fileName);

        try {
            img = ImageIO.read(is);
        } catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                if (is != null) is.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        return img;
    }

    public static BufferedImage[] GetAllLevels() {
        URL url = LoadSave.class.getResource("/lvls");
        File file = null;

        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        File[] files = file.listFiles();
        File[] filesSorted = new File[files.length];

        for (int i = 0; i < filesSorted.length; i++)
            for (int j = 0; j < files.length; j++) {
                if (files[j].getName().equals((i + 1) + ".png"))
                    filesSorted[i] = files[j];

            }

        BufferedImage[] imgs = new BufferedImage[filesSorted.length];

        for (int i = 0; i < imgs.length; i++)
            try {
                imgs[i] = ImageIO.read(filesSorted[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }

        return imgs;
    }


    // The levelIndex argument starts at 0 for level 1 (1.tmx)
    public static File GetTMXLevel(int levelIndex) {
        // We add 1 to the index to get the filename (0 -> "1", 1 -> "2", etc.)
        String fileName = (levelIndex + 1) + ".tmx";

        // Construct the full resource path
        String fullPath = TMX_LEVEL_FOLDER + "/" + fileName;

        // Use the ClassLoader to get the resource URL
        URL url = LoadSave.class.getResource(fullPath);

        if (url == null) {
            System.err.println("Error: TMX file not found at path: " + fullPath);
            return null;
        }

        File tmxFile = null;
        try {
            // Convert the URL to a File object using URI (necessary for files in jar/resource folders)
            tmxFile = new File(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return tmxFile;
    }

    // NEW method to load collision data from the TMX file
    public static int[][] GetLevelDataFromTMX(String fileName, String collisionLayerName, int mapWidth, int mapHeight) {

        int[][] levelData = new int[mapHeight][mapWidth];

        try (InputStream is = LoadSave.class.getResourceAsStream("/" + fileName)) {

            // 1. Initialize XML Parser
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            // 2. Find all <layer> tags
            NodeList layerNodes = doc.getElementsByTagName("layer");

            for (int i = 0; i < layerNodes.getLength(); i++) {

                // Check if this is the target collision layer
                String layerName = layerNodes.item(i).getAttributes().getNamedItem("name").getNodeValue();

                if (layerName.equals(collisionLayerName)) {

                    // 3. Get the <data> node (containing the CSV)
                    NodeList dataNodes = ((org.w3c.dom.Element) layerNodes.item(i)).getElementsByTagName("data");
                    String csvData = dataNodes.item(0).getTextContent().trim();

                    // 4. Parse the CSV data string
                    String[] tiles = csvData.split(",");

                    // 5. Populate the 2D array
                    for (int j = 0; j < mapHeight; j++) {
                        for (int k = 0; k < mapWidth; k++) {

                            // The index in the 1D tiles array
                            int arrayIndex = j * mapWidth + k;

                            // Check for array bounds before parsing
                            if (arrayIndex >= tiles.length) {
                                System.err.println("TMX Parsing Error: Data index out of bounds.");
                                break;
                            }

                            int tileId = Integer.parseInt(tiles[arrayIndex].trim());

                            // Tiled uses 0 for empty space. Any tile ID > 0 is a solid tile.
                            levelData[j][k] = (tileId > 0) ? 1 : 0;
                        }
                    }
                    break; // Collision layer found, exit the loop
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading TMX file: " + e.getMessage());
            e.printStackTrace();
        }



        return levelData;
    }

    public static ArrayList<Float> GetPigSpawnsFromTMX(String fileName, String objectLayerName) {
        // We'll return an ArrayList of Floats: [x1, y1, x2, y2, ...]
        ArrayList<Float> pigSpawnCoords = new ArrayList<>();

        try (InputStream is = LoadSave.class.getResourceAsStream("/" + fileName)) {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            // 1. Find all <objectgroup> tags
            NodeList objectGroupNodes = doc.getElementsByTagName("objectgroup");

            for (int i = 0; i < objectGroupNodes.getLength(); i++) {

                Element groupElement = (Element) objectGroupNodes.item(i);
                String groupName = groupElement.getAttribute("name");

                // 2. Check if this is the target "Spawns" layer
                if (groupName.equals(objectLayerName)) {

                    // 3. Get all <object> nodes within this group
                    NodeList objectNodes = groupElement.getElementsByTagName("object");

                    for (int j = 0; j < objectNodes.getLength(); j++) {
                        Element objectElement = (Element) objectNodes.item(j);

                        // 4. Filter objects by name="pig" (as set in your TMX)
                        String objectName = objectElement.getAttribute("name");

                        if (objectName.equals("pig")) {

                            // 5. Extract x and y coordinates
                            // TMX stores coordinates as attributes in the <object> tag
                            float x = Float.parseFloat(objectElement.getAttribute("x"));
                            float y = Float.parseFloat(objectElement.getAttribute("y"));

                            // Store the coordinates
                            pigSpawnCoords.add(x* Game.SCALE);
                            pigSpawnCoords.add(y* Game.SCALE);
                        }
                    }
                    break; // Spawns layer found, exit the object group loop
                }
            }

        } catch (Exception e) {
            System.err.println("Error loading Pig Spawns from TMX file: " + e.getMessage());
            e.printStackTrace();
        }

        return pigSpawnCoords;
    }

    // The placeholder function can now be removed or updated to use the new method
    // If you need a list of actual Pig objects, you'd process the coordinates here.
    // For simplicity, I've made the extractor the main public method.
    public static ArrayList<Float> GetPigSpawns() {
        // Use the new, robust method
        return GetPigSpawnsFromTMX(TMX_LEVEL, "Spawns");
    }

    public static ArrayList<Float> GetItemSpawnsFromTMX(String fileName, String objectLayerName) {
        // We'll return an ArrayList of Floats representing [x1, y1, type1, x2, y2, type2, ...]
        ArrayList<Float> itemData = new ArrayList<>();

        try (InputStream is = LoadSave.class.getResourceAsStream("/" + fileName)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            NodeList objectGroupNodes = doc.getElementsByTagName("objectgroup");

            for (int i = 0; i < objectGroupNodes.getLength(); i++) {
                Element groupElement = (Element) objectGroupNodes.item(i);
                String groupName = groupElement.getAttribute("name");

                if (groupName.equals(objectLayerName)) { // Target "Items" layer
                    NodeList objectNodes = groupElement.getElementsByTagName("object");

                    for (int j = 0; j < objectNodes.getLength(); j++) {
                        Element objectElement = (Element) objectNodes.item(j);
                        String objectName = objectElement.getAttribute("name");

                        if (objectName.equals("heart") || objectName.equals("diamond")) {
                            float x = Float.parseFloat(objectElement.getAttribute("x"));
                            float y = Float.parseFloat(objectElement.getAttribute("y"));

                            // Map the name string to the constant integer type
                            int objectType = objectName.equals("diamond") ? DIAMOND : HEART;

                            itemData.add(x * Game.SCALE);
                            itemData.add(y * Game.SCALE);
                            itemData.add((float) objectType); // Store the object type
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading Item Spawns from TMX file: " + e.getMessage());
            e.printStackTrace();
        }

        return itemData;
    }

    public static ArrayList<Float> GetContainerSpawnsFromTMX(String fileName, String objectLayerName) {
        // We'll return an ArrayList of Floats representing [x1, y1, type1, x2, y2, type2, ...]
        ArrayList<Float> containerData = new ArrayList<>();

        try (InputStream is = LoadSave.class.getResourceAsStream("/" + fileName)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            NodeList objectGroupNodes = doc.getElementsByTagName("objectgroup");

            for (int i = 0; i < objectGroupNodes.getLength(); i++) {
                Element groupElement = (Element) objectGroupNodes.item(i);
                String groupName = groupElement.getAttribute("name");

                if (groupName.equals(objectLayerName)) { // Target "Containers" layer
                    NodeList objectNodes = groupElement.getElementsByTagName("object");

                    for (int j = 0; j < objectNodes.getLength(); j++) {
                        Element objectElement = (Element) objectNodes.item(j);
                        String objectName = objectElement.getAttribute("name");

                        if (objectName.equals("box") || objectName.equals("barrel")) {
                            float x = Float.parseFloat(objectElement.getAttribute("x"));
                            float y = Float.parseFloat(objectElement.getAttribute("y"));

                            // Map the name string to the constant integer type
                            int objectType = objectName.equals("box") ? Constants.ObjectConstants.BOX : Constants.ObjectConstants.BARREL;

                            containerData.add(x * Game.SCALE);
                            containerData.add(y * Game.SCALE);
                            containerData.add((float) objectType);
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading Container Spawns from TMX file: " + e.getMessage());
            e.printStackTrace();
        }

        return containerData;
    }


    public static ArrayList<Float> GetCannonSpawnsFromTMX(String fileName, String objectLayerName) {
        // Returns data as [x1, y1, type1, x2, y2, type2, ...]
        ArrayList<Float> cannonData = new ArrayList<>();

        try (InputStream is = LoadSave.class.getResourceAsStream("/" + fileName)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            NodeList objectGroupNodes = doc.getElementsByTagName("objectgroup");

            for (int i = 0; i < objectGroupNodes.getLength(); i++) {
                Element groupElement = (Element) objectGroupNodes.item(i);
                String groupName = groupElement.getAttribute("name");

                if (groupName.equals(objectLayerName)) { // Target "Cannons" layer
                    NodeList objectNodes = groupElement.getElementsByTagName("object");

                    for (int j = 0; j < objectNodes.getLength(); j++) {
                        Element objectElement = (Element) objectNodes.item(j);
                        String objectName = objectElement.getAttribute("name");

                        if (objectName.equals("left_cannon") || objectName.equals("right_cannon")) {
                            float x = Float.parseFloat(objectElement.getAttribute("x"));
                            float y = Float.parseFloat(objectElement.getAttribute("y"));

                            // Map the name string to the constant integer type
                            int objectType = objectName.equals("right_cannon") ? Constants.ObjectConstants.CANNON_RIGHT : Constants.ObjectConstants.CANNON_LEFT;

                            cannonData.add(x * Game.SCALE);
                            cannonData.add(y * Game.SCALE);
                            cannonData.add((float) objectType); // Store the cannon type
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading Cannon Spawns from TMX file: " + e.getMessage());
            e.printStackTrace();
        }

        return cannonData;
    }


}

